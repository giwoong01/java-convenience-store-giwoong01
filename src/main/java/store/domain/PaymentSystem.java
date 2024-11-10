package store.domain;

import java.time.LocalDate;
import store.util.ParseUtil;

public class PaymentSystem {

    private final Products products;
    private final OrderProduct orderProduct;
    private final Promotions promotions;

    private int totalResult;
    private int discountResult;

    public PaymentSystem(Products products, OrderProduct orderProduct, Promotions promotions) {
        this.products = products;
        this.orderProduct = orderProduct;
        this.promotions = promotions;
        this.totalResult = 0;
        this.discountResult = 0;
    }

    public int applyPromotionDiscount(String orderProductName) {
        int orderProductQuantity = orderProduct.getOrderProductQuantity(orderProductName);
        Integer productPrice = products.findApplicablePrice(orderProductName);

        totalResult += (orderProductQuantity * productPrice);

        String productPromotion = products.findApplicablePromotion(orderProductName);
        Integer requiredBuyQuantity = promotions.getPromotionBuyRequirement(productPromotion);
        Integer promotionFreeQuantity = promotions.getPromotionFreeQuantity(productPromotion);
        int productQuantity = ParseUtil.parseInt(products.getProductQuantity(orderProductName));

        while (orderProductQuantity >= requiredBuyQuantity + promotionFreeQuantity
                && productQuantity >= requiredBuyQuantity + promotionFreeQuantity) {
            discountResult += (requiredBuyQuantity * productPrice);
            products.updateProductQuantity(orderProductName, requiredBuyQuantity + promotionFreeQuantity);

            orderProductQuantity -= requiredBuyQuantity + promotionFreeQuantity;
            productQuantity -= requiredBuyQuantity + promotionFreeQuantity;
        }

        return orderProductQuantity;
    }

    public void freePromotionPayment(String orderProductName,
                                     int remainOrderProductQuantity,
                                     int promotionFreeQuantity) {
        Integer productPrice = products.findApplicablePrice(orderProductName);
        totalResult += ((remainOrderProductQuantity + promotionFreeQuantity) * productPrice);
        discountResult += (remainOrderProductQuantity * productPrice);
        products.updateProductQuantity(orderProductName, remainOrderProductQuantity + promotionFreeQuantity);
    }

    public void basicPayment(String orderProductName, int remainOrderProductQuantity) {
        Integer productPrice = products.findApplicablePrice(orderProductName);
        discountResult += (remainOrderProductQuantity * productPrice);
        products.updateProductQuantity(orderProductName, remainOrderProductQuantity);
    }

    public boolean isPromotionsApplicable(String orderProductName, LocalDate currentDate) {
        String productPromotion = products.findApplicablePromotion(orderProductName);
        return promotions.isPromotionsApplicable(productPromotion, currentDate);
    }

    public boolean isOrderProductQuantity(int orderProductQuantity) {
        return orderProductQuantity > 0;
    }

    public boolean isEligibleForFreePromotion(int orderProductQuantity,
                                              int requiredBuyQuantity,
                                              int promotionFreeQuantity) {
        return orderProductQuantity < requiredBuyQuantity + promotionFreeQuantity
                && orderProductQuantity == requiredBuyQuantity;
    }

    public void applyMembershipDiscount() {
        int membershipDiscount = (int) (discountResult * 0.30);

        if (membershipDiscount > 8000) {
            membershipDiscount = 8000;
        }

        discountResult = discountResult - membershipDiscount;
    }

    public int getTotalResult() {
        return totalResult;
    }

    public int getDiscountResult() {
        return discountResult;
    }

}
