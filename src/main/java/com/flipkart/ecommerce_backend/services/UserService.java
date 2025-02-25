package com.flipkart.ecommerce_backend.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import com.flipkart.ecommerce_backend.api.models.RegistrationRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flipkart.ecommerce_backend.Exception.EmailAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.InvalidEmailVerificationTokenException;
import com.flipkart.ecommerce_backend.Exception.InvalidUserCredentialsException;
import com.flipkart.ecommerce_backend.Exception.MailNotSentException;
import com.flipkart.ecommerce_backend.Exception.MailNotfoundException;
import com.flipkart.ecommerce_backend.Exception.UserAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.UserDoesNotExistsException;
import com.flipkart.ecommerce_backend.Exception.UserNotVerifiedException;
import com.flipkart.ecommerce_backend.Exception.UserVerificationTokenAlreadyVerifiedException;
import com.flipkart.ecommerce_backend.Exception.VerificationTokenExpiredException;
import com.flipkart.ecommerce_backend.api.models.LoginBody;
import com.flipkart.ecommerce_backend.api.models.RegistrationRequest;
import com.flipkart.ecommerce_backend.api.models.UpdateUserDto;
import com.flipkart.ecommerce_backend.models.Address;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.VerificationToken;
import com.flipkart.ecommerce_backend.models.Repository.AddressRepository;
import com.flipkart.ecommerce_backend.models.Repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.models.Repository.VerificationTokenRepository;

@Service
public class UserService {

	@Autowired
	private LocalUserRepository localUserRepository;

	@Autowired
	private EncryptionService encryptionService;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Transactional(rollbackOn = {MailNotSentException.class, UserAlreadyExistsException.class, EmailAlreadyExistsException.class})
	public LocalUser registerUser(RegistrationRequest registrationRequest)
			throws UserAlreadyExistsException, EmailAlreadyExistsException, MailNotSentException {
		if (localUserRepository.findByUsernameIgnoreCase(registrationRequest.getUser_name()).isPresent()) {
			throw new UserAlreadyExistsException();
		}
		if (localUserRepository.findByEmailIgnoreCase(registrationRequest.getEmail()).isPresent()) {
			throw new EmailAlreadyExistsException();
		}
		LocalUser localuser = new LocalUser();
		localuser.setUsername(registrationRequest.getUser_name());
		localuser.setPassword(encryptionService.encryptPassword(registrationRequest.getPassword()));
		localuser.setEmail(registrationRequest.getEmail());
		localuser.setFirst_name(registrationRequest.getFirst_name());
		localuser.setLast_name(registrationRequest.getLast_name());
		VerificationToken verificationToken = createVerificationToken(localuser);
		localUserRepository.save(localuser);
		emailService.sendVerificationMail(verificationToken);
		verificationTokenRepository.save(verificationToken);
		return localuser;
	}

	public String loginUser(LoginBody loginBody) throws MailNotSentException, UserNotVerifiedException,
			UserDoesNotExistsException, InvalidUserCredentialsException {
		Optional<LocalUser> optionalUser = localUserRepository.findByUsernameIgnoreCase(loginBody.getUsername());
		if (optionalUser.isPresent()) {
			LocalUser localUser = optionalUser.get();
			boolean iscorrectPassword = encryptionService.verifyPassword(loginBody.getPassword(),
					localUser.getPassword());
			if (iscorrectPassword) {
				if (localUser.isEmailVerified()) {
					return jwtService.generateJWT(localUser);
				} else {
					List<VerificationToken> verificationTokenList = localUser.getVerificationTokens();
					boolean resend = false;
					if (verificationTokenList.size() == 0) {
						resend = true;
					} else if (verificationTokenList.get(0).getCreatedTimestamp()
							.before(new Timestamp(System.currentTimeMillis() - (1000 * 60 * 60)))) {
						resend = true;
					}
					if (resend) {
						VerificationToken verificationToken = createVerificationToken(localUser);
						emailService.sendVerificationMail(verificationToken);
					}
					throw new UserNotVerifiedException(resend);
				}
			} else {
				throw new InvalidUserCredentialsException("Invalid UserName and Password");
			}
		} else {
			throw new UserDoesNotExistsException();
		}
	}

	public LocalUser updateUser(UpdateUserDto updateduser,Long userId) throws UserDoesNotExistsException {
		Optional<LocalUser> optUser = localUserRepository.findById(userId);
		if(optUser.isPresent()) {
			LocalUser local_user = optUser.get();
			local_user.setUsername(updateduser.getUser_name());
			local_user.setFirst_name(updateduser.getFirst_name());
			local_user.setEmail(updateduser.getEmail());
			local_user.setLast_name(updateduser.getLast_name());
			localUserRepository.save(local_user);
			return local_user;
		}
		else {
			throw new UserDoesNotExistsException();
		}
	}

	public LocalUser getUserById(Long id) throws UserDoesNotExistsException {
		Optional<LocalUser> optUser = localUserRepository.findById(id);
		if(optUser.isPresent()) {
			LocalUser localUser = optUser.get();
			return localUser;
		}
		else {
			throw new UserDoesNotExistsException();
		}
	}

	public List<LocalUser> getAllUsers(){
		List <LocalUser> allUserList = (List<LocalUser>) localUserRepository.findAll();
		return allUserList;
	}

	public void deleteUser(Long id){
		Optional<LocalUser> optUser = localUserRepository.findById(id);
		if(!optUser.isPresent()) {
			throw new UserDoesNotExistsException();
		}
		localUserRepository.deleteById(id);
	}

	public VerificationToken createVerificationToken(LocalUser localUser) {
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(jwtService.generateEmailJwt(localUser));
		verificationToken.setLocalUser(localUser);
		verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
		localUser.getVerificationTokens().add(verificationToken);

		return verificationToken;
	}

	public boolean verifyToken(String token)
			throws VerificationTokenExpiredException, InvalidEmailVerificationTokenException,
			UserVerificationTokenAlreadyVerifiedException, MailNotSentException {
		String email = jwtService.getEmailFromToken(token);
		Optional<LocalUser> optionalUser = localUserRepository.findByEmailIgnoreCase(email);
		boolean isTokenExpired = jwtService.isTokenExpired(token);
		if (optionalUser.isPresent()) {
			LocalUser localUser = optionalUser.get();
			if (localUser.isEmailVerified()) {
				throw new UserVerificationTokenAlreadyVerifiedException();
			}
			if (isTokenExpired) {
				VerificationToken verificationToken = createVerificationToken(localUser);
				emailService.sendVerificationMail(verificationToken);
				throw new VerificationTokenExpiredException();
			}
			localUser.setEmailVerified(true);
			localUserRepository.save(localUser);
			return true;
		} else {
			throw new InvalidEmailVerificationTokenException();
		}
	}

	public void forgotPassword(String email) throws MailNotfoundException, MailNotSentException {
		Optional<LocalUser> opLocalUser = localUserRepository.findByEmailIgnoreCase(email);
		if(opLocalUser.isPresent()) {
			LocalUser localUser = opLocalUser.get();
			String token = jwtService.generatePasswordResetJwt(localUser);
			emailService.sendPasswordRestVerificationMail(token,localUser);
		}
		else {
			throw new MailNotfoundException();
		}
	}

	public void resetPassword(String token, String newPassword) throws InvalidEmailVerificationTokenException, VerificationTokenExpiredException {
		String email = jwtService.getPasswordResetEmailFromToken(token);
		Optional<LocalUser> opLocalUser = localUserRepository.findByEmailIgnoreCase(email);
		if(opLocalUser.isPresent()) {
			LocalUser localUser = opLocalUser.get();
			boolean isTokenexpired = jwtService.isTokenExpired(token);
			if(isTokenexpired) {
				throw new VerificationTokenExpiredException();
			}
			localUser.setPassword(encryptionService.encryptPassword(newPassword));
			localUserRepository.save(localUser);
		}
		else {
			throw new InvalidEmailVerificationTokenException();
		}
	}

	public List <Address> getAddress(Long userId){
		List<Address> address = addressRepository.findByLocalUser_Id(userId);
		return address;
	}

	public Address saveAddress(Address address) {
		return addressRepository.save(address);

	}
}
