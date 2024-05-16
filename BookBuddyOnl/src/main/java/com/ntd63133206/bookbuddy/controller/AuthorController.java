package com.ntd63133206.bookbuddy.controller;

import com.ntd63133206.bookbuddy.model.Author;
import com.ntd63133206.bookbuddy.service.AuthorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/authors")
public class AuthorController {

	private static final String UPLOAD_DIR = "/uploads/images/authors/";

    @Autowired
    private AuthorService authorService;

    @GetMapping("/")
    public String viewAuthorList(Model model,
                                 @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Page<Author> authorsPage;
        PageRequest pageRequest = PageRequest.of(page, size);

        if ("".equals(keyword)) {
            keyword = null;
        }

        authorsPage = authorService.searchAuthorsByKeyword(keyword, pageRequest);

        model.addAttribute("authors", authorsPage.getContent());
        model.addAttribute("currentPage", authorsPage.getNumber() + 1);
        model.addAttribute("totalPages", authorsPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "admin/authors/author-list";
    }


    @PostMapping("/authors/search")
    public String searchAuthors(@RequestParam(name = "keyword", required = false) String keyword,
                                RedirectAttributes redirectAttributes) {
        return "redirect:/admin/authors/?keyword=" + keyword;
    }


    @GetMapping("/add")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "admin/authors/add-author";
    }

    @PostMapping("/add")
    public String addAuthor(@ModelAttribute("author") @Valid Author author,
                            @RequestParam("authorImage") MultipartFile authorImage,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra lỗi validation của đối tượng Author
            if (bindingResult.hasErrors()) {
                // Xử lý lỗi ở đây nếu cần
                return "admin/authors/add-author";
            }

            if (authorImage != null && !authorImage.isEmpty()) {
                // Lưu ảnh và cập nhật đường dẫn ảnh trong đối tượng Author
                String fileName = authorService.saveAuthorImage(authorImage);
                author.setAuthorImage(fileName);
            } else {
                // Sử dụng ảnh mặc định nếu không có ảnh được chọn
                String defaultAvatarPath = "default/default-avatar.jpg";
                author.setAuthorImage(defaultAvatarPath);
            }

            // Lưu thông tin tác giả vào cơ sở dữ liệu
            authorService.save(author);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm tác giả thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/authors";
    }


    

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Author> author = authorService.findById(id);
        if (author.isPresent()) {
            model.addAttribute("author", author.get());
            return "admin/authors/edit";
        }
        return "redirect:/admin/authors";
    }

    @PostMapping("/edit/{id}")
    public String editAuthor(@PathVariable Long id, @ModelAttribute Author author,
                             @RequestParam("authorImage") MultipartFile authorImage) throws IOException {
        if (!authorImage.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + authorImage.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, authorImage.getBytes());
            author.setAuthorImage(fileName);
        } else {
            Optional<Author> existingAuthor = authorService.findById(id);
            existingAuthor.ifPresent(value -> author.setAuthorImage(value.getAuthorImage()));
        }
        author.setId(id);
        authorService.save(author);
        return "redirect:/admin/authors";
    }

    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.deleteById(id);
        return "redirect:/admin/authors";
    }
}
