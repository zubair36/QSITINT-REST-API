package com.qsitint.resource.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qsitint.config.JwtTokenProvider;
import com.qsitint.domain.User;
import com.qsitint.repository.RoleRepository;
import com.qsitint.repository.UserRepository;
import com.qsitint.sms.config.SMSManager;
import com.qsitint.sms.config.TwilioConfiguration;
import com.qsitint.sms.config.TwilioInitializer;
import com.qsitint.utils.ConstantUtils;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserResourceImpl {

	private static Logger log = LoggerFactory.getLogger(UserResourceImpl.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	Date date = new Date();
	

	@PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> register(@RequestBody User user) throws AddressException, MessagingException, IOException {
		log.info("UserResourceImpl : register");
		JSONObject jsonObject = new JSONObject();
		try {
			Random rnd = new Random();
	        int number = rnd.nextInt(999999);
	        String otp = String.format("%06d", number);
			user.setPassword(new BCryptPasswordEncoder().encode(otp));
			user.setRole(roleRepository.findByName(ConstantUtils.USER.toString()));
			sendOTPEmail(user.getEmail(), otp, "One Time Password (OTP)");
			
			//Method to send OTP SMS
			//sendOTPSMS(user.getMobile(), null, otp);
			
			user.setOtp(otp);
			User savedUser = userRepository.saveAndFlush(user);
			jsonObject.put("message", savedUser.getName() + " saved succesfully");
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		} catch (JSONException e) {
			try {
				jsonObject.put("exception", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			log.info(new Timestamp(date.getTime()) + " User=> " + user.getName() + ", Email=> " + user.getEmail() + " Create Sucessfully!!");
			return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping(value = "/authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> authenticate(@RequestBody User user) {
		log.info("UserResourceImpl : authenticate");
		JSONObject jsonObject = new JSONObject();

		User tempUser = userRepository.findByEmail(user.getEmail());
		if(user.getPassword().equals(tempUser.getOtp())) {
			user.setPassword(tempUser.getOtp());

		}else {
			return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.UNAUTHORIZED);
		}
		
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
			if (authentication.isAuthenticated()) {
				String email = user.getEmail();
				jsonObject.put("name", authentication.getName());
				jsonObject.put("authorities", authentication.getAuthorities());
				jsonObject.put("token", tokenProvider.createToken(email, userRepository.findByEmail(email).getRole()));
				log.info(new Timestamp(date.getTime()) + " User=> " + user.getEmail() + " Loggedin Sucessfully!!");

				return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
			}
		} catch (JSONException e) {
			try {
				jsonObject.put("exception", e.getMessage());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.UNAUTHORIZED);
		}
		return null;
	}

	//Method to send OTP Email
	private void sendOTPEmail(String userEmail, String otpPassword, String subject) {

		log.info("inside sendOTPEmail()");
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(userEmail);
        msg.setSubject(subject);  
        msg.setText(otpPassword);
        javaMailSender.send(msg);
		log.info(new Timestamp(date.getTime()) + " OTP Email Sent to user=> " + userEmail + " Sucessfully!!");

    }
	
	//Method to send OTP SMS
	private void sendOTPSMS(String to, String from, String sms) {
		log.info("inside sendOTPSMS()");
		String twilioAcountSID = null;
		String twilioAuthToken= null;
		String twilioSmsSenderTrialNumber= null;
		
		try (InputStream input = UserResourceImpl.class.getClassLoader().getResourceAsStream("application.properties")) {

            Properties prop = new Properties();

            if (input == null) {
            	log.error("Sorry, unable to find application.properties");
                return;
            }

            prop.load(input);
            twilioAcountSID = prop.getProperty("twilio.account_sid");
            twilioAuthToken = prop.getProperty("twilio.auth_token");
            twilioSmsSenderTrialNumber = prop.getProperty("twilio.sms.from.trial.number");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

		TwilioConfiguration twilioConfiguration = new TwilioConfiguration();
		twilioConfiguration.setAccountSid(twilioAcountSID);
		twilioConfiguration.setAuthToken(twilioAuthToken);
		twilioConfiguration.setTrialNumber(twilioSmsSenderTrialNumber);
		
		TwilioInitializer twilioInitializer = new TwilioInitializer(twilioConfiguration);
		SMSManager sMSManager = new SMSManager(new TwilioConfiguration());
		sMSManager.sendSms(to, twilioSmsSenderTrialNumber, sms);
		log.info(new Timestamp(date.getTime()) + " OTP SMS Sent to PH# " + to + " Sucessfully!!");

    }
	
}