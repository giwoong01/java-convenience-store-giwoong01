package store.domain;

import java.util.ArrayList;
import java.util.List;
import store.dto.OrderProductDto;
import store.dto.PromotionDto;

public class ReceiptIssuer {

    public static List<OrderProductDto> getOrderProductDetails(OrderProduct orderProduct, Products products) {
        List<OrderProductDto> orderProductDetails = new ArrayList<>();
        List<String> orderProductNames = orderProduct.getOrderProductNames();

        for (String name : orderProductNames) {
            int orderProductQuantity = orderProduct.getOrderProductQuantity(name);
            int freePromotionQuantity = orderProduct.getFreePromotionProductQuantity(name);
            int totalQuantity = orderProductQuantity + freePromotionQuantity;
            int price = products.findApplicablePrice(name);
            orderProductDetails.add(new OrderProductDto(name, totalQuantity, price));
        }

        return orderProductDetails;
    }

    public static List<PromotionDto> getPromotionDetails(PaymentSystem paymentSystem) {
        List<PromotionDto> promotionDetails = new ArrayList<>();
        for (String name : paymentSystem.getFreePromotionProducts().keySet()) {
            Integer promotionFreeQuantity = paymentSystem.getFreePromotionProducts().get(name);
            promotionDetails.add(new PromotionDto(name, promotionFreeQuantity));
        }

        return promotionDetails;
    }

    public static int getTotalQuantity(List<OrderProductDto> orderProductDetails) {
        return orderProductDetails.stream().mapToInt(OrderProductDto::totalQuantity).sum();
    }

}