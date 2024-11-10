package store.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import store.dto.OrderProductDto;
import store.dto.ProductDto;
import store.dto.ProductsDto;
import store.dto.PromotionDto;

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

    public void printReceipt(List<OrderProductDto> orderProductDetails,
                             List<PromotionDto> promotionDetails,
                             int totalResult,
                             int promotionDiscount,
                             BigDecimal membershipDiscount,
                             int discountResult,
                             int totalQuantity) {
        printReceiptHeader();
        printOrderProducts(orderProductDetails);
        printPromotionDiscounts(promotionDetails);
        printReceiptFooter(totalResult, promotionDiscount, membershipDiscount, discountResult, totalQuantity);
    }

    private void printReceiptHeader() {
        System.out.println("\n==============W 편의점================");
        System.out.println("상품명\t\t수량\t금액");
    }

    public void printOrderProducts(List<OrderProductDto> orderProductDetails) {
        for (OrderProductDto dto : orderProductDetails) {
            System.out.printf("%s\t\t%d\t%,d\n",
                    dto.name(),
                    dto.totalQuantity(),
                    dto.price() * dto.totalQuantity());
        }
    }

    public void printPromotionDiscounts(List<PromotionDto> promotionDetails) {
        System.out.println("=============증\t정===============");
        for (PromotionDto dto : promotionDetails) {
            System.out.printf("%s\t\t%d\n", dto.name(), dto.promotionFreeQuantity());
        }
    }

    private void printReceiptFooter(int totalResult,
                                    int promotionDiscount,
                                    BigDecimal membershipDiscount,
                                    int discountResult,
                                    int totalQuantity) {
        DecimalFormat df = new DecimalFormat("#,###");
        System.out.println("==================================");
        System.out.printf("총구매액\t\t%d\t%,d\n", totalQuantity, totalResult);
        System.out.printf("행사할인\t\t\t-%,d\n", promotionDiscount);
        System.out.printf("멤버십할인\t\t\t-%s\n", df.format(membershipDiscount));
        System.out.printf("내실돈\t\t\t\t %s\n",
                df.format(BigDecimal.valueOf(discountResult).subtract(membershipDiscount)));
    }

}
