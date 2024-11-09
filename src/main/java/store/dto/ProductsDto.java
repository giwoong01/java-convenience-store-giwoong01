package store.dto;

import java.util.List;

public record ProductsDto(
        List<ProductDto> productsDtos
) {
    public static ProductsDto from(List<ProductDto> productsDtos) {
        return new ProductsDto(productsDtos);
    }
}
