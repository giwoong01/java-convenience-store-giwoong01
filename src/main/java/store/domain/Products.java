package store.domain;

import java.util.List;
import java.util.Objects;
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
                // 프로모션 적용 가능하고 재고가 있는 상품을 우선으로 찾음
                .filter(product -> orderProductName.equals(product.getName()) &&
                        !Objects.equals(product.getPromotion(), "") &&
                        !product.getQuantity().equals("재고 없음"))
                .map(Product::getPromotion)
                .findFirst()
                // 프로모션 상품이 없거나 재고가 없을 경우 비프로모션 상품을 찾음
                .or(() -> products.stream()
                        .filter(product -> orderProductName.equals(product.getName()) && product.getPromotion() == null)
                        .map(product -> "") // 비프로모션인 경우 "null"로 반환
                        .findFirst()
                )
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
                .ifPresentOrElse(product -> {
                            if (product.getQuantity().equals("재고 없음")) {
                                products.stream()
                                        .filter(p -> p.getName().equals(productName) && p.getPromotion().isEmpty())
                                        .findFirst()
                                        .ifPresent(nonPromoProduct -> {
                                            int nonPromoQuantity = ParseUtil.parseInt(nonPromoProduct.getQuantity());
                                            nonPromoProduct.updateQuantity(nonPromoQuantity - quantityToSubtractHolder[0]);
                                        });
                            } else {
                                int currentQuantity = ParseUtil.parseInt(product.getQuantity());
                                int remainingQuantity = currentQuantity - quantityToSubtractHolder[0];

                                if (remainingQuantity < 0) {
                                    product.updateQuantity(0);
                                    quantityToSubtractHolder[0] = -remainingQuantity;

                                    products.stream()
                                            .filter(p -> p.getName().equals(productName) && p.getPromotion().isEmpty())
                                            .findFirst()
                                            .ifPresent(nonPromoProduct -> {
                                                int nonPromoQuantity = ParseUtil.parseInt(nonPromoProduct.getQuantity());
                                                nonPromoProduct.updateQuantity(nonPromoQuantity - quantityToSubtractHolder[0]);
                                            });
                                } else {
                                    product.updateQuantity(remainingQuantity);
                                }
                            }
                        },
                        () -> {
                            products.stream()
                                    .filter(p -> p.getName().equals(productName) && p.getPromotion().isEmpty())
                                    .findFirst()
                                    .ifPresent(nonPromoProduct -> {
                                        int nonPromoQuantity = ParseUtil.parseInt(nonPromoProduct.getQuantity());
                                        nonPromoProduct.updateQuantity(nonPromoQuantity - quantityToSubtractHolder[0]);
                                    });
                        });
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

}