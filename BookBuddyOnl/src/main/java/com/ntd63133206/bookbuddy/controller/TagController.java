package com.ntd63133206.bookbuddy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ntd63133206.bookbuddy.model.Tag;
import com.ntd63133206.bookbuddy.service.TagService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tags")
public class TagController {
	@Autowired
    private TagService tagService;

    @GetMapping("/list")
    public String listTags(Model model) {
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        return "tags/tag-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "tags/add-tag";
    }

    @PostMapping("/add")
    public String addTag(@Valid @ModelAttribute("tag") Tag tag,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "tags/add-tag";
        }
        tagService.addTag(tag);
        redirectAttributes.addFlashAttribute("successMessage", "Tag added successfully!");
        return "redirect:/tags/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Tag tag = tagService.getTagById(id);
        model.addAttribute("tag", tag);
        return "tags/edit-tag";
    }

    @PostMapping("/edit/{id}")
    public String editTag(@PathVariable("id") Long id,
                          @Valid @ModelAttribute("tag") Tag tag,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "tags/edit-tag";
        }
        tagService.updateTag(id, tag);
        redirectAttributes.addFlashAttribute("successMessage", "Tag updated successfully!");
        return "redirect:/tags/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        tagService.deleteTag(id);
        redirectAttributes.addFlashAttribute("successMessage", "Tag deleted successfully!");
        return "redirect:/tags/list";
    }
}