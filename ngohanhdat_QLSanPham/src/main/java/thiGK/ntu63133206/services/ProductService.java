package thiGK.ntu63133206.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import thiGK.ntu63133206.model.Product;

@Service
public interface ProductService {
    Page<Product> findPaginated(Pageable pageable);
}