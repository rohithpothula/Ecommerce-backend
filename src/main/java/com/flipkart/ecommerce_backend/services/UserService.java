package com.flipkart.ecommerce_backend.services;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flipkart.ecommerce_backend.Exception.EmailAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.UserAlreadyExistsException;
import com.flipkart.ecommerce_backend.Exception.UserDoesNotExistsException;
import com.flipkart.ecommerce_backend.api.models.LoginBody;
import com.flipkart.ecommerce_backend.api.models.RegistrationBody;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.Repository.LocalUserRepository;

@Service
public class UserService {
	
	@Autowired
	private LocalUserRepository localUserRepository;
	
	@Autowired
	private EncryptionService encryptionService;
	
	@Autowired
	private JwtService jwtService;
	
	public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailAlreadyExistsException{
		if(localUserRepository.findByUsernameIgnoreCase(registrationBody.getUser_name()).isPresent()) {
			throw new UserAlreadyExistsException();
		}
		if(localUserRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()) {
			throw new EmailAlreadyExistsException();
		}
		LocalUser localuser = new LocalUser();
		localuser.setUsername(registrationBody.getUser_name());
		localuser.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
		localuser.setEmail(registrationBody.getEmail());
		localuser.setFirst_name(registrationBody.getFirst_name());
		localuser.setLast_name(registrationBody.getLast_name());
		return localUserRepository.save(localuser);
	}
	
	public String loginUser(LoginBody loginBody) throws Exception {	
		Optional<LocalUser> optionalUser = localUserRepository.findByUsernameIgnoreCase(loginBody.getUsername());
		if(optionalUser.isPresent()) {
			LocalUser localUser = optionalUser.get();
			boolean iscorrectPassword = encryptionService.verifyPassword(loginBody.getPassword(),localUser.getPassword());
			if(iscorrectPassword) {
				return jwtService.generateJWT(localUser);
			}
			else {
				throw new Exception("Invalid UserName and Password");
			}
		}
		else{
			throw new UserDoesNotExistsException();
		}
	}
}
