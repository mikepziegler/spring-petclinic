package org.springframework.samples.petclinic.owner;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for creating, updating and displaying Owners.
 */
@Controller
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM =
		"owners/createOrUpdateOwnerForm";

	private final OwnerRepository owners;

	public OwnerController(OwnerRepository owners) {
		this.owners = owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("owner")
	public Owner findOwner(
		@PathVariable(name = "ownerId", required = false) Integer ownerId) {

		if (ownerId == null) {
			return new Owner();
		}

		return owners.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId +
					". Please ensure the ID is correct and the owner exists."));
	}

	@GetMapping("/owners/new")
	public String initCreationForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(
		@Valid Owner owner,
		BindingResult result,
		RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute(
				"error", "There was an error in creating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "New Owner Created");
		return "redirect:/owners/" + owner.getId();
	}

	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(
		@RequestParam(defaultValue = "1") int page,
		Owner owner,
		BindingResult result,
		Model model) {

		// allow parameterless GET request for /owners to return all records
		if (owner.getLastName() == null) {
			owner.setLastName("");
		}

		Page<Owner> ownersResults =
			findPaginatedForOwnersLastName(page, owner.getLastName());

		if (ownersResults.isEmpty()) {
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}

		if (ownersResults.getTotalElements() == 1) {
			Owner found = ownersResults.iterator().next();
			return "redirect:/owners/" + found.getId();
		}

		return addPaginationModel(page, model, ownersResults);
	}

	private String addPaginationModel(
		int page, Model model, Page<Owner> paginated) {

		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}

	private Page<Owner> findPaginatedForOwnersLastName(
		int page, String lastName) {

		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return owners.findByLastNameStartingWith(lastName, pageable);
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(
		@Valid Owner owner,
		BindingResult result,
		@PathVariable("ownerId") int ownerId,
		RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute(
				"error", "There was an error in updating the owner.");
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		if (owner.getId() != ownerId) {
			result.rejectValue("id", "mismatch",
				"The owner ID in the form does not match the URL.");
			redirectAttributes.addFlashAttribute(
				"error", "Owner ID mismatch. Please try again.");
			return "redirect:/owners/{ownerId}/edit";
		}

		owner.setId(ownerId);
		owners.save(owner);
		redirectAttributes.addFlashAttribute(
			"message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		Owner owner = owners.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId +
					". Please ensure the ID is correct."));

		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		mav.addObject(owner);
		return mav;
	}
}
