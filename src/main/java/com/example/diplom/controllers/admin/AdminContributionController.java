package com.example.diplom.controllers.admin;

import com.example.diplom.dto.ContributionDTO;
import com.example.diplom.entities.Contribution;
import com.example.diplom.services.storage.ContributionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/contributions")
public class AdminContributionController {

    private final ContributionService contributionService;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminContributionController(ContributionService contributionService, ModelMapper modelMapper) {
        this.contributionService = contributionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/create_contribution")
    public String showCreateContributionPage(Model model) {
        ContributionDTO contributionDTO = convertToContributionDto(new Contribution());
        model.addAttribute("contribution", contributionDTO);
        return "admin/contributions/create_contribution";
    }

    @PostMapping(value = "/create_contribution", params = "create_contribution")
    public String createNewContribution(@Valid ContributionDTO contributionDTO, BindingResult bindingResult) {
        Contribution contribution = convertToContribution(contributionDTO);
        if (bindingResult.hasErrors()) {
            return "redirect:/admin/contributions";
        } else {
            contribution.setCreatedByAdmin(true);
            contributionService.save(contribution);
        }
        return "redirect:/admin/contributions";
    }

    @GetMapping
    public String showContributions(Model model) {
        List<ContributionDTO> contributionDTOList = contributionService.findContributionsByCreatedByAdminIsTrue().stream()
                .map(this::convertToContributionDto)
                .collect(Collectors.toList());
        model.addAttribute("contributions", contributionDTOList);
        return "admin/contributions/contributions";
    }

    @GetMapping(value = "/{contribution_id}")
    public String getContributionById(@PathVariable Long contribution_id, Model model) {
        model.addAttribute("contribution", contributionService.findById(contribution_id));
        return "admin/contributions/contribution_by_id";
    }

    @PostMapping(value = "/{contribution_id}", params = "update")
    public String updateContributionById(@PathVariable Long contribution_id,
                                         @Valid Contribution contributionToUpdate,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/contributions/contribution_by_id";
        } else {
            contributionService.updateContribution(contribution_id, contributionToUpdate);
        }
        return "redirect:/admin/contributions";
    }

    @PostMapping(params = "delete_contribution")
    public String deleteContribution(@RequestParam Long contribution_id) {
        contributionService.deleteById(contribution_id);
        return "redirect:/admin/contributions";
    }

    private ContributionDTO convertToContributionDto(Contribution contribution) {
        return modelMapper.map(contribution, ContributionDTO.class);
    }

    private Contribution convertToContribution(ContributionDTO contributionDTO) {
        return modelMapper.map(contributionDTO, Contribution.class);
    }
}
