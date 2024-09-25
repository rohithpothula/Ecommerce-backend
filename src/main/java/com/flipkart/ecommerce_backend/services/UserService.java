package com.flipkart.ecommerce_backend.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flipkart.ecommerce_backend.Exception.EmailAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.InvalidEmailVerificationTokenException;
import com.flipkart.ecommerce_backend.Exception.InvalidUserCredentialsException;
import com.flipkart.ecommerce_backend.Exception.MailNotSentException;
import com.flipkart.ecommerce_backend.Exception.UserAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.UserDoesNotExistsException;
import com.flipkart.ecommerce_backend.Exception.UserNotVerifiedException;
import com.flipkart.ecommerce_backend.Exception.UserVerificationTokenAlreadyVerifiedException;
import com.flipkart.ecommerce_backend.Exception.VerificationTokenExpiredException;
import com.flipkart.ecommerce_backend.api.models.LoginBody;
import com.flipkart.ecommerce_backend.api.models.RegistrationBody;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.VerificationToken;
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

	public LocalUser registerUser(RegistrationBody registrationBody)
			throws UserAlreadyExistsException, EmailAlreadyExistsException, MailNotSentException {
		if (localUserRepository.findByUsernameIgnoreCase(registrationBody.getUser_name()).isPresent()) {
			throw new UserAlreadyExistsException();
		}
		if (localUserRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
			throw new EmailAlreadyExistsException();
		}
		LocalUser localuser = new LocalUser();
		localuser.setUsername(registrationBody.getUser_name());
		localuser.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
		localuser.setEmail(registrationBody.getEmail());
		localuser.setFirst_name(registrationBody.getFirst_name());
		localuser.setLast_name(registrationBody.getLast_name());
		VerificationToken verificationToken = createVerificationToken(localuser);
		emailService.sendVerificationMail(verificationToken);
		verificationTokenRepository.save(verificationToken);
		return localUserRepository.save(localuser);
	}

	public String loginUser(LoginBody loginBody) throws MailNotSentException, UserNotVerifiedException, UserDoesNotExistsException, InvalidUserCredentialsException {
		Optional<LocalUser> optionalUser = localUserRepository.findByUsernameIgnoreCase(loginBody.getUsername());
		if (optionalUser.isPresent()) {
			LocalUser localUser = optionalUser.get();
			boolean iscorrectPassword = encryptionService.verifyPassword(loginBody.getPassword(),
					localUser.getPassword());
			if (iscorrectPassword) {
				if (localUser.isEmailVerified()) {
					return jwtService.generateJWT(localUser);
				}
				else {
					List<VerificationToken> verificationTokenList = localUser.getVerificationTokens();
					boolean resend = false;
					if(verificationTokenList.size()==0) {
						resend=true;
					}
					else if(verificationTokenList.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis()-(1000*60*60)))) {
						resend=true;
					}
					if(resend) {
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

	public VerificationToken createVerificationToken(LocalUser localUser) {
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(jwtService.generateEmailJwt(localUser));
		verificationToken.setLocalUser(localUser);
		verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
		localUser.getVerificationTokens().add(verificationToken);
		verificationTokenRepository.save(verificationToken);
		localUserRepository.save(localUser);
		return verificationToken;
	}

	public boolean verifyToken(String token)
			throws VerificationTokenExpiredException, InvalidEmailVerificationTokenException, UserVerificationTokenAlreadyVerifiedException, MailNotSentException {
		String email = jwtService.getEmailFromToken(token);
		Optional<LocalUser> optionalUser = localUserRepository.findByEmailIgnoreCase(email);
		boolean isTokenExpired = jwtService.isTokenExpired(token);
		if (optionalUser.isPresent()) {
			LocalUser localUser = optionalUser.get();
			if(localUser.isEmailVerified()) {
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
}
