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

    public static List<PromotionDto> getPromotionDetails(PaymentSystem paymentSystem,
                                                         Promotions promotions,
                                                         Products products) {
        List<PromotionDto> promotionDetails = new ArrayList<>();
        for (String name : paymentSystem.getFreePromotionProducts()) {
            int promotionFreeQuantity = promotions.getPromotionFreeQuantity(products.findApplicablePromotion(name));
            promotionDetails.add(new PromotionDto(name, promotionFreeQuantity));
        }

        return promotionDetails;
    }
}