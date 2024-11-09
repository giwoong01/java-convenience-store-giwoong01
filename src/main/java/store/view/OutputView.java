package store.view;

import java.text.NumberFormat;
import store.dto.ProductDto;
import store.dto.ProductsDto;

public class OutputView {

    public void printErrorMessage(String message) {
        System.out.println(message);
    }

    public void printIntroduction() {
        System.out.println("안녕하세요. W편의점입니다.");
    }

    public void printProducts(ProductsDto productsDto) {
        System.out.println("현재 보유하고 있는 상품입니다.\n");

        for (ProductDto productDto : productsDto.productsDtos()) {
            System.out.printf("- %s %s원 %s %s\n",
                    productDto.name(),
                    NumberFormat.getInstance().format(productDto.price()),
                    productDto.getFormattedQuantity(),
                    productDto.promotion());
        }
    }

}
