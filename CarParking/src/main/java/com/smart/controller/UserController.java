package com.smart.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import com.smart.dao.ParkingSlotRepository;
import com.smart.dao.WorkerRepository;
import com.smart.entities.ParkingSlot;
import com.smart.entities.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WorkerRepository workerRepository;

	@Autowired
	private ParkingSlotRepository parkingSlotRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "profile");
		return "normal/profile";
	}

	@GetMapping("/add-worker")
	public String openAddWorkerForm(Model model) {
		model.addAttribute("title", "Add Worker");
		model.addAttribute("worker", new Worker());
		return "normal/add_worker_form";
	}

	@GetMapping("/add-slot")
	public String openAddSlotForm(Model model) {
		model.addAttribute("title", "Add Parking Slots");
		model.addAttribute("slot", new ParkingSlot());
		return "normal/add_slot_form";
	}

	@PostMapping("/process-worker")
	public String processWorker(@ModelAttribute Worker worker,
								 Principal principal, HttpSession session) {
		try {
			workerRepository.save(worker);
			session.setAttribute("message", new Message("Worker is added !! Add more..", "success"));

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Some went wrong !! Try again..", "danger"));

		}
		return "normal/add_worker_form";
	}

	@PostMapping("/process-slot")
	public String processParking(@ModelAttribute ParkingSlot parkingSlot,
								Principal principal, HttpSession session) {
		try {
			parkingSlotRepository.save(parkingSlot);
			session.setAttribute("message", new Message("Parking Slot is added !! Add more..", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Some went wrong !! Try again..", "danger"));
		}
		return "normal/add_slot_form";
	}

	@GetMapping("/show-worker")
	public String showWorkers(Model m, Principal principal) {
		m.addAttribute("title", "Show User Workers");
		List<Worker> worker = this.workerRepository.findAll();
		m.addAttribute("workers", worker);
		return "normal/show_worker";
	}

	@GetMapping("/show-user")
	public String showUsers(Model m, Principal principal) {
		m.addAttribute("title", "Show Users");
		List<User> users = this.userRepository.findAll();
		m.addAttribute("users", users);
		return "normal/show_user";
	}

	@GetMapping("/show-slots")
	public String showSlots(Model m, Principal principal) {
		m.addAttribute("title", "Show Parkings");
		List<ParkingSlot> slots = this.parkingSlotRepository.findAll();
		m.addAttribute("slots", slots);
		return "normal/show_slot";
	}

	@RequestMapping("/{cId}/worker")
	public String showWorkerDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<Worker> workerOptional = this.workerRepository.findById(cId);
		Worker worker = workerOptional.get();
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == worker.getUser().getId()) {
			model.addAttribute("worker", worker);
			model.addAttribute("title", worker.getName());
		}

		return "normal/worker_detail";
	}

	@GetMapping("/deleteuser/{id}")
	public String deleteUser(@PathVariable("id") Integer Id, HttpSession session,
							   Principal principal) {

		User user = this.userRepository.getOne(Id);
		this.userRepository.delete(user);
		session.setAttribute("message", new Message("User deleted succesfully...", "success"));
		return "redirect:/user/show-user";
	}

	@GetMapping("/deleteslot/{id}")
	public String deleteSlots(@PathVariable("id") Integer Id, HttpSession session,
							 Principal principal) {

		ParkingSlot parkingSlot = this.parkingSlotRepository.getOne(Id);
		this.parkingSlotRepository.delete(parkingSlot);
		session.setAttribute("message", new Message("Parking Slot deleted succesfully...", "success"));
		return "redirect:/user/show-slots";
	}

	@GetMapping("/delete/{cid}")
	@Transactional
	public String deleteWorker(@PathVariable("cid") Integer cId, HttpSession session,
								Principal principal) {
		Worker worker = this.workerRepository.findById(cId).get();
		workerRepository.delete(worker);
		session.setAttribute("message", new Message("Worker deleted succesfully...", "success"));
		return "redirect:/user/show-worker";
	}

	@PostMapping("/update-worker/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model m) {

		m.addAttribute("title", "Update Worker");
		Worker worker = this.workerRepository.findById(cid).get();
		m.addAttribute("worker", worker);
		return "normal/update_form";
	}

	@PostMapping("/update-slot/{id}")
	public String updateParking(@PathVariable("id") Integer Id, Model m) {

		m.addAttribute("title", "Update Parking Slot");
		ParkingSlot parkingSlot = this.parkingSlotRepository.getOne(Id);
		m.addAttribute("slot", parkingSlot);
		return "normal/update_slot";
	}

	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Worker worker,
								Model m, HttpSession session, Principal principal) {

		try {
			this.workerRepository.save(worker);
			session.setAttribute("message", new Message("Your worker is updated...", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/show-worker";
	}

	@RequestMapping(value = "/process-updatedslot", method = RequestMethod.POST)
	public String updateSlotHandler(@ModelAttribute ParkingSlot parkingSlot,
								Model m, HttpSession session, Principal principal) {
		try {
			this.parkingSlotRepository.save(parkingSlot);
			session.setAttribute("message", new Message("Your Slot is updated...", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/show-slots";
	}

	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}

	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
								 @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);

		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password is successfully changed..", "success"));

		} else {
			session.setAttribute("message", new Message("Please Enter correct old password !!", "danger"));
			return "redirect:/user/settings";
		}
		return "redirect:/user/index";
	}

}
