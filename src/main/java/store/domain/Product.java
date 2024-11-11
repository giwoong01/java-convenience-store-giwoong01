package store.domain;

import java.util.Objects;
import store.util.ParseUtil;

public class Product {

    private final String name;
    private final int price;
    private String quantity;
    private final String promotion;

    public Product(String name, int price, String quantity, String promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public static Product createProduct(String name, String price, String quantity, String promotion) {
        if (Objects.equals(promotion, "null")) {
            promotion = "";
        }

        return new Product(
                name,
                ParseUtil.parseInt(price),
                quantity,
                promotion
        );
    }

    public void updateQuantity(int quantityToSubtract) {
        if (quantityToSubtract <= 0) {
            this.quantity = "재고 없음";
            return;
        }

        this.quantity = Integer.toString(quantityToSubtract);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPromotion() {
        return promotion;
    }

}
