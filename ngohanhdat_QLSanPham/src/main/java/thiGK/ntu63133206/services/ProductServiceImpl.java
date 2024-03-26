package thiGK.ntu63133206.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import thiGK.ntu63133206.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static List<Product> productList = new ArrayList<>();

    static {
        productList.add(new Product(1L, "Product 1", "image1.jpg", 10.5));
        productList.add(new Product(2L, "Product 2", "image2.jpg", 20.0));
        productList.add(new Product(3L, "Product 3", "image3.jpg", 15.75));
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
    
}
