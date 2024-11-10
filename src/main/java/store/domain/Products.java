package store.domain;

import java.util.List;
import store.dto.ProductDto;
import store.dto.ProductsDto;
import store.util.ParseUtil;

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

    public Integer findApplicablePrice(String productName) {
        return products.stream()
                .filter(product -> product.getName().equals(productName))
                .map(Product::getPrice)
                .findFirst()
                .orElse(null);
    }

    public String findApplicablePromotion(String orderProductName) {
        return products.stream()
                .filter(product -> orderProductName.equals(product.getName()))
                .map(Product::getPromotion)
                .findFirst()
                .orElse(null);
    }

    public String getProductQuantity(String orderProductName) {
        return products.stream()
                .filter(product -> orderProductName.equals(product.getName()) && !product.getQuantity().equals("재고 없음")
                        && !product.getPromotion().isEmpty())
                .map(Product::getQuantity)
                .findFirst()
                .orElseGet(() -> products.stream()
                        .filter(product -> orderProductName.equals(product.getName()) && !product.getQuantity()
                                .equals("재고 없음") && product.getPromotion().isEmpty())
                        .map(Product::getQuantity)
                        .findFirst()
                        .orElse("0"));
    }

    public void updateProductQuantity(String productName, int quantityToSubtract) {
        final int[] quantityToSubtractHolder = {quantityToSubtract};
        products.stream()
                .filter(product -> product.getName().equals(productName) && !product.getPromotion().equals("null"))
                .findFirst()
                .ifPresent(product -> {
                    int remainingQuantity = ParseUtil.parseInt(product.getQuantity()) - quantityToSubtractHolder[0];
                    if (remainingQuantity < 0) {
                        product.updateQuantity(ParseUtil.parseInt(product.getQuantity()));
                        quantityToSubtractHolder[0] = -remainingQuantity;
                        products.stream()
                                .filter(p -> p.getName().equals(productName) && p.getPromotion().isEmpty())
                                .findFirst()
                                .ifPresent(
                                        nonPromoProduct -> nonPromoProduct.updateQuantity(quantityToSubtractHolder[0]));
                    } else {
                        product.updateQuantity(quantityToSubtractHolder[0]);
                    }
                });
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

}