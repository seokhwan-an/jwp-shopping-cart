package cart.service;

import cart.dao.ProductDao;
import cart.dto.ProductRequestDto;
import cart.dto.ProductResponseDto;
import cart.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductDao productDao;

    public ProductService(final ProductDao productDao) {
        this.productDao = productDao;
    }

    public Long saveProduct(final ProductRequestDto productRequestDto) {
        final Product newProduct = new Product(productRequestDto);
        return productDao.insertProduct(newProduct);
    }

    public List<ProductResponseDto> findAll() {
        List<Product> products = productDao.findAll();
        return products.stream()
            .map(ProductResponseDto::new)
            .collect(Collectors.toList());
    }

    public void updateProduct(final Long id, final ProductRequestDto productRequestDto) {
        validateProductExist(id);
        productDao.updateProduct(id, new Product(productRequestDto));
    }

    private void validateProductExist(final Long id) {
        Optional<Product> product = productDao.findById(id);
        if (!product.isPresent()) {
            throw new IllegalStateException("존재하지 않는 상품입니다.");
        }
    }
}