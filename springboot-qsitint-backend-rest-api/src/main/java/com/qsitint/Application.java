package com.qsitint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.qsitint.domain.Role;
import com.qsitint.domain.User;
import com.qsitint.domain.UserRequest;
import com.qsitint.service.IRoleService;
import com.qsitint.service.IService;
import com.qsitint.sms.config.SMSManager;
import com.qsitint.sms.config.TwilioConfiguration;
import com.qsitint.sms.config.TwilioInitializer;
import com.qsitint.utils.ConstantUtils;

@SpringBootApplication
public class Application implements CommandLineRunner {
	@Autowired
	private IService<User> userService;

	@Autowired
	private IRoleService<Role> roleService;

	@Autowired
	private IService<UserRequest> userRequestService;
	

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (roleService.findAll().isEmpty()) {
			roleService.saveOrUpdate(new Role(ConstantUtils.ADMIN.toString()));
			roleService.saveOrUpdate(new Role(ConstantUtils.USER.toString()));
		}

		if (userService.findAll().isEmpty()) {
			
			User user1 = new User();
			user1.setEmail("test@user.com");
			user1.setName("Test User");
			user1.setMobile("9787456545");
			user1.setOtp("001122");
			user1.setRole(roleService.findByName(ConstantUtils.USER.toString()));
			user1.setPassword(new BCryptPasswordEncoder().encode("001122"));
			userService.saveOrUpdate(user1);

			User user2 = new User();
			user2.setEmail("test@admin.com");
			user2.setName("Test Admin");
			user2.setMobile("9787456545");
			user2.setOtp("001122");
			user2.setRole(roleService.findByName(ConstantUtils.ADMIN.toString()));
			user2.setPassword(new BCryptPasswordEncoder().encode("001122"));
			userService.saveOrUpdate(user2);
		}

		if (userRequestService.findAll().isEmpty()) {
				UserRequest userRequest = new UserRequest();
				
				userRequest.setDescription("Dummy Request Description");
				userRequest.setLng("55.394304");
				userRequest.setLat("25.3427712");
				userRequest.setRequestType("Complaint");
				userRequest.setStatus("Pending");
				userRequestService.saveOrUpdate(userRequest);
		}
	}

}
