package com.qsitint.service.impl;

import java.util.Collection;
import java.util.Optional;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.qsitint.domain.UserRequest;
import com.qsitint.repository.UserRequestRepository;
import com.qsitint.service.IPageService;
import com.qsitint.service.IService;

@Service
public class UserRequestServiceImpl implements IService<UserRequest>, IPageService<UserRequest> {

	@Autowired
	private UserRequestRepository userRequestRepository;
	
	@Override
	public Collection<UserRequest> findAll() {
		return (Collection<UserRequest>) userRequestRepository.findAll();
	}

	@Override
	public Page<UserRequest> findAll(Pageable pageable, String searchText) {
		return userRequestRepository.findAllUserRequests(pageable, searchText);
	}

	@Override
	public Page<UserRequest> findAll(Pageable pageable) {
		return userRequestRepository.findAll(pageable);
	}

	@Override
	public Optional<UserRequest> findById(Long id) {
		return userRequestRepository.findById(id);
	}

	@Override
	public UserRequest saveOrUpdate(UserRequest userRequest) {
		return userRequestRepository.save(userRequest);
	}

	@Override
	public String deleteById(Long id) {
		JSONObject jsonObject = new JSONObject();
		try {
			
			userRequestRepository.deleteById(id);
			jsonObject.put("message", "User Request deleted successfully");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

}
