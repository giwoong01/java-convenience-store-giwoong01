package store.dto;

public record OrderProductDto(
        String name,
        int totalQuantity,
        int price
) {
}