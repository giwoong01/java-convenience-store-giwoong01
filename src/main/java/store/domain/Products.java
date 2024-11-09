package store.domain;

import java.util.List;
import store.dto.ProductDto;
import store.dto.ProductsDto;

public class Products {

    private final List<Product> products;

    public Products(List<Product> products) {
        this.products = products;
    }

    public ProductsDto toProductsDto() {
        return ProductsDto.from(products.stream()
                .map(product -> ProductDto.of(
                        product.getName(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getPromotion()))
                .toList());
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

}