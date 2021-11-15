package com.qsitint.resource.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qsitint.domain.UserRequest;
import com.qsitint.resource.Resource;
import com.qsitint.service.IPageService;
import com.qsitint.service.IService;

@RestController
@RequestMapping("/userrequests")
@CrossOrigin(origins="http://localhost:3000")
public class UserRequestResourceImpl implements Resource<UserRequest> {
	
	private static Logger log = LoggerFactory.getLogger(UserRequestResourceImpl.class);
	
	@Autowired
	private IService<UserRequest> userRequestService;
	
	@Autowired
	private IPageService<UserRequest> userRequestPageService;
	
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	Date date = new Date();

	@Override
	public ResponseEntity<Page<UserRequest>> findAll(Pageable pageable, String searchText) {
		return new ResponseEntity<>(userRequestPageService.findAll(pageable, searchText), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Page<UserRequest>> findAll(int pageNumber, int pageSize, String sortBy, String sortDir) {
		return new ResponseEntity<>(userRequestPageService.findAll(
				PageRequest.of(
						pageNumber, pageSize,
						sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
				)
		), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<UserRequest> findById(Long id) {
		return new ResponseEntity<>(userRequestService.findById(id).get(), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<UserRequest> save(UserRequest userRequest) {
		userRequest.setStatus("Pending");
		return new ResponseEntity<>(userRequestService.saveOrUpdate(userRequest), HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<UserRequest> update(UserRequest userRequest) {
		return new ResponseEntity<>(userRequestService.saveOrUpdate(userRequest), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteById(Long id) {
		log.info("***********Inside deleteById() of UserResourceImpl.java");
		return new ResponseEntity<>(userRequestService.deleteById(id), HttpStatus.OK);
	}

	@GetMapping("/requestTypes")
	public  ResponseEntity<Set<String>> findAllRequestTypes() {
        return new ResponseEntity<>(new TreeSet<>(Arrays.asList("Inquiry", "Complaint")), HttpStatus.OK);
    }

}
