package thiGK.ntu63133206.controllers;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import thiGK.ntu63133206.models.Product;
import thiGK.ntu63133206.services.ImageStorageService;
import thiGK.ntu63133206.services.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ImageStorageService imageStorageService;

    @Autowired
    public ProductController(ProductService productService, ImageStorageService imageStorageService) {
        this.productService = productService;
        this.imageStorageService = imageStorageService;
    }

    @GetMapping(value = {"/index", ""})
    public String index(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 6);
        Page<Product> productPage = productService.findPaginated(pageable);
        model.addAttribute("productPage", productPage);
        return "products/index";
    }

    @GetMapping("/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/add_product";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product, @RequestParam("productImage") MultipartFile file, RedirectAttributes redirectAttributes) {
    	try {
            String imageUrl = file.isEmpty() ? "default-product.jpg" : imageStorageService.saveImage(file);
            product.setImageUrl(imageUrl);
            productService.addProduct(product);
            return "redirect:/products";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload image: " + e.getMessage());
            return "redirect:/products/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "products/edit_product";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, @ModelAttribute Product product, @RequestParam("productImage") MultipartFile file, RedirectAttributes redirectAttributes) {
    	try {
            String imageUrl = file.isEmpty() ? product.getImageUrl() : imageStorageService.saveImage(file);
            product.setImageUrl(imageUrl);
            productService.updateProduct(product);
            return "redirect:/products";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to upload image: " + e.getMessage());
            return "redirect:/products/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products/index";
    }
}
