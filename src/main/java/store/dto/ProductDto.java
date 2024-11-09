package store.dto;

public record ProductDto(
        String name,
        int price,
        String quantity,
        String promotion
) {
    public static ProductDto of(String name, int price, String quantity, String promotion) {
        return new ProductDto(name, price, quantity, promotion);
    }

    public String getFormattedQuantity() {
        if ("재고 없음".equals(quantity)) {
            return quantity;
        }
        return quantity + "개";
    }
}
