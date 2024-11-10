package store.domain;

import java.util.List;
import java.util.Map;
import store.util.ParseUtil;
import store.validator.OrderProductValidator;

public class OrderProduct {

    private final Map<String, Integer> orderProduct;

    public OrderProduct(Map<String, Integer> orderProduct, Products products) {
        validateOrderProduct(orderProduct, products);
        this.orderProduct = orderProduct;
    }

    private void validateOrderProduct(Map<String, Integer> orderProduct, Products products) {
        for (String orderProductName : orderProduct.keySet()) {
            OrderProductValidator.validateProductExists(orderProductName, getProductNames(products));
            OrderProductValidator.validateProductQuantity(
                    orderProduct.get(orderProductName),
                    getProductQuantity(orderProductName, products)
            );
        }
    }

    private List<String> getProductNames(Products products) {
        return products.getProducts().stream()
                .map(Product::getName)
                .toList();
    }

    private int getProductQuantity(String productName, Products products) {
        return products.getProducts().stream()
                .filter(product -> product.getName().equals(productName))
                .mapToInt(product -> {
                    if ("재고 없음".equals(product.getQuantity())) {
                        return 0;
                    }
                    return ParseUtil.parseInt(product.getQuantity());
                })
                .sum();
    }

    public static OrderProduct createOrderProduct(Map<String, Integer> orderProduct, Products products) {
        return new OrderProduct(orderProduct, products);
    }

    public List<String> getOrderProductNames() {
        return orderProduct.keySet().stream().toList();
    }

    public int getOrderProductQuantity(String orderProductName) {
        return orderProduct.get(orderProductName);
    }

}
