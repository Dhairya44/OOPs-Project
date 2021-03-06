package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.smart.entities.Worker;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.smart.dao.UserRepository;
import com.smart.dao.WorkerRepository;
import com.smart.entities.User;
import java.util.Date;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WorkerRepository workerRepository;

	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title","Login Page");
		return "login";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register - Car Parking");
		model.addAttribute("user", new User());
		return "signup";
	}
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,
							   @RequestParam(value = "otp", defaultValue = "") String otp,
							   @RequestParam(value = "agreement",  defaultValue = "false") boolean agreement, Model model,
							   HttpSession session) {

		String OTP = RandomString.make(8);
		String email = user.getEmail();
		if(userRepository.findByEmail(user.getEmail()).isEmpty()) {
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setPassword(this.passwordEncoder.encode(user.getPassword()));
			user.setOneTimePassword(OTP);
			user.setOtpRequestedTime(new Date());
			this.userRepository.save(user);
			try {
				OtpVerification otpVerification = new OtpVerification();
				otpVerification.generateOneTimePassword(user, OTP);
			} catch (Exception e) {
				System.out.println(e);
			}
			model.addAttribute("user", user);
			model.addAttribute("otpbool", true);
			return "signup";
		}
		User users2 = userRepository.findByEmail(email).get(0);
		System.out.println(otp + " " + users2.getOneTimePassword());
		if(users2.getOneTimePassword().compareTo(otp)==0)
			return "login";
		else {
			model.addAttribute("user", user);
			model.addAttribute("otpbool", true);
			return "signup";
		}

	}

	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("title","Login Page");
		return "login";
	}

	@GetMapping("/wlogin")
	public String openAddWorkerForm(Model model) {
		model.addAttribute("title", "Add worker");
		model.addAttribute("error", false);
		model.addAttribute("worker", new Worker());
		return "worker_login";
	}

	@PostMapping("/wdash")
	public String openWorkerDashboard(@Valid @ModelAttribute("email")String email, Model model) {
		Worker worker = workerRepository.getWorkerByUserName(email);
		model.addAttribute("title", "Add worker");
		if(workerRepository.findWorkerByEmail(email).isEmpty()) {
			model.addAttribute("error", true);
			return "worker_login";
		}
		else {
			model.addAttribute("worker", worker);
			return "worker/profile";
		}
	}
}
