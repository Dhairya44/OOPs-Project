package com.smart.controller;

import java.security.Principal;
import java.util.List;

import com.smart.dao.WorkerRepository;
import com.smart.entities.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@RestController
public class SearchController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WorkerRepository workerRepository;

	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal)
	{
		System.out.println(query);		
		User user=this.userRepository.getUserByUserName(principal.getName());		
		List<Worker> workers = this.workerRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(workers);
	}
	
}
