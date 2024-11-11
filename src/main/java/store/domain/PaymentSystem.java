package store.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import store.util.ParseUtil;

public class PaymentSystem {

    private final Products products;
    private final OrderProduct orderProduct;
    private final Promotions promotions;

    private int totalResult;
    private int discountResult;
    private int membershipResult;
    private Map<String, Integer> freePromotionProducts = new HashMap<>();

    public PaymentSystem(Products products, OrderProduct orderProduct, Promotions promotions) {
        this.products = products;
        this.orderProduct = orderProduct;
        this.promotions = promotions;
        this.totalResult = 0;
        this.discountResult = 0;
        this.membershipResult = 0;
    }

    public int applyPromotionPayment(String orderProductName) {
        int orderProductQuantity = orderProduct.getOrderProductQuantity(orderProductName);
        Integer productPrice = products.findApplicablePrice(orderProductName);
        String productPromotion = products.findApplicablePromotion(orderProductName);
        Integer requiredBuyQuantity = promotions.getPromotionBuyRequirement(productPromotion);
        Integer promotionFreeQuantity = promotions.getPromotionFreeQuantity(productPromotion);
        int productQuantity = ParseUtil.parseInt(products.getProductQuantity(orderProductName));

        while (orderProductQuantity >= requiredBuyQuantity + promotionFreeQuantity
                && productQuantity >= requiredBuyQuantity + promotionFreeQuantity) {
            totalResult += ((requiredBuyQuantity + promotionFreeQuantity) * productPrice);
            discountResult += (requiredBuyQuantity * productPrice);
            products.updateProductQuantity(orderProductName, requiredBuyQuantity + promotionFreeQuantity);

            freePromotionProducts.merge(orderProductName, promotionFreeQuantity, Integer::sum);

            orderProductQuantity -= requiredBuyQuantity + promotionFreeQuantity;
            productQuantity -= requiredBuyQuantity + promotionFreeQuantity;
        }

        return orderProductQuantity;
    }

    public void nonPromotionPayment(String orderProductName, int remainOrderProductQuantity) {
        Integer productPrice = products.findApplicablePrice(orderProductName);
        totalResult += (remainOrderProductQuantity * productPrice);
        membershipResult += (remainOrderProductQuantity * productPrice);
        discountResult += (remainOrderProductQuantity * productPrice);
        products.updateProductQuantity(orderProductName, remainOrderProductQuantity);
    }

    public void YFreePromotionPayment(String orderProductName,
                                      int remainOrderProductQuantity,
                                      int promotionFreeQuantity) {
        Integer productPrice = products.findApplicablePrice(orderProductName);
        totalResult += ((remainOrderProductQuantity + promotionFreeQuantity) * productPrice);
        discountResult += (remainOrderProductQuantity * productPrice);
        freePromotionProducts.merge(orderProductName, promotionFreeQuantity, Integer::sum);
        products.updateProductQuantity(orderProductName, remainOrderProductQuantity + promotionFreeQuantity);
        orderProduct.addFreePromotionProduct(orderProductName, promotionFreeQuantity);
    }

    public void NFreePromotionPayment(String orderProductName,
                                      int remainOrderProductQuantity) {
        Integer productPrice = products.findApplicablePrice(orderProductName);
        totalResult += (remainOrderProductQuantity * productPrice);
        discountResult += (remainOrderProductQuantity * productPrice);
        products.updateProductQuantity(orderProductName, remainOrderProductQuantity);
    }

    public void basicPayment(String orderProductName, int remainOrderProductQuantity) {
        Integer productPrice = products.findApplicablePrice(orderProductName);

        totalResult += (remainOrderProductQuantity * productPrice);
        membershipResult += (remainOrderProductQuantity * productPrice);
        discountResult += (remainOrderProductQuantity * productPrice);
        products.updateProductQuantity(orderProductName, remainOrderProductQuantity);
    }

    public void YBasicPayment(String orderProductName, int remainOrderProductQuantity) {
        Integer productPrice = products.findApplicablePrice(orderProductName);

        totalResult += (remainOrderProductQuantity * productPrice);
        membershipResult += (remainOrderProductQuantity * productPrice);
        discountResult += (remainOrderProductQuantity * productPrice);
        products.updateProductQuantity(orderProductName, remainOrderProductQuantity);
    }

    public void NBasicPayment(String orderProductName, int remainOrderProductQuantity, int updateQuantity) {
        int adjustedQuantity = remainOrderProductQuantity - updateQuantity;
        orderProduct.updateOrderProductQuantity(orderProductName, adjustedQuantity);
    }

    public boolean isPromotionsApplicable(String orderProductName, LocalDateTime currentDate) {
        String productPromotion = products.findApplicablePromotion(orderProductName);
        return promotions.isPromotionsApplicable(productPromotion, currentDate);
    }

    public boolean isOrderProductQuantity(int orderProductQuantity, int requiredBuyQuantity) {
        return orderProductQuantity > 0 && orderProductQuantity > requiredBuyQuantity;
    }

    public boolean isEligibleForFreePromotion(int orderProductQuantity,
                                              int productQuantity,
                                              int requiredBuyQuantity,
                                              int promotionFreeQuantity) {
        return orderProductQuantity < requiredBuyQuantity + promotionFreeQuantity
                && orderProductQuantity >= requiredBuyQuantity
                && productQuantity != orderProductQuantity;
    }

    public BigDecimal applyMembershipDiscount() {
        BigDecimal bigDecimal = BigDecimal.valueOf(membershipResult * 0.30);
        return bigDecimal.min(BigDecimal.valueOf(8000));
    }

    public int getTotalResult() {
        return totalResult;
    }

    public int getDiscountResult() {
        return discountResult;
    }

    public Map<String, Integer> getFreePromotionProducts() {
        return freePromotionProducts;
    }

}
