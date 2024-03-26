package thiGK.ntu63133206.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import thiGK.ntu63133206.models.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private static List<Product> productList = new ArrayList<>();

    static {
        productList.add(new Product(1L, "Bút", "p-but.jpg", 10000));
        productList.add(new Product(2L, "Gôm", "p-gom.jpg", 2000));
        productList.add(new Product(3L, "Thước sắt", "p-thuoc-sat.jpg", 150000));
    }

    @Override
    public Page<Product> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Product> list;

        if (productList.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, productList.size());
            list = productList.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), productList.size());
    }

    @Override
    public Product addProduct(Product product) {
        productList.add(product);
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        Optional<Product> result = productList.stream().filter(p -> p.getId().equals(id)).findFirst();
        return result.orElse(null);
    }

    @Override
    public Product updateProduct(Product product) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId().equals(product.getId())) {
                productList.set(i, product);
                return product;
            }
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        productList.removeIf(product -> product.getId().equals(id));
    }
}
