package store.controller;

import java.time.LocalDate;
import java.util.List;
import store.domain.OrderProduct;
import store.domain.PaymentSystem;
import store.domain.Products;
import store.domain.Promotions;
import store.util.FileUtil;
import store.util.RetryUtil;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private final InputView inputView;
    private final OutputView outputView;

    public StoreController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void start() {
        Products products = getProducts();
        printProducts(products);

        // 이 아래부터 코드 정리
        OrderProduct orderProduct = getOrderProduct(products);
        Promotions promotions = getPromotions();
        PaymentSystem paymentSystem = new PaymentSystem(products, orderProduct, promotions);

        List<String> orderProductNames = orderProduct.getOrderProductNames();
        LocalDate currentDate = LocalDate.now();

        for (String orderProductName : orderProductNames) {
            int remainOrderProductQuantity = orderProduct.getOrderProductQuantity(orderProductName);

            // 만약 프로모션 적용이면...
            if (paymentSystem.isPromotionsApplicable(orderProductName, currentDate)) {
                int orderProductQuantity = orderProduct.getOrderProductQuantity(orderProductName);
                String productPromotion = products.findApplicablePromotion(orderProductName);
                Integer requiredBuyQuantity = promotions.getPromotionBuyRequirement(productPromotion);
                Integer promotionFreeQuantity = promotions.getPromotionFreeQuantity(productPromotion);

                // 만약 무료로 더 받을 거면...
                if (paymentSystem.isEligibleForFreePromotion(orderProductQuantity,
                        requiredBuyQuantity,
                        promotionFreeQuantity)
                        &&
                        RetryUtil.freePromotionChoice(inputView, outputView, orderProductName, promotionFreeQuantity)) {
                    paymentSystem.freePromotionPayment(orderProductName,
                            remainOrderProductQuantity,
                            promotionFreeQuantity);
                    continue;
                }

                remainOrderProductQuantity = paymentSystem.applyPromotionDiscount(orderProductName);

                // 프로모션 적용된 상품이 모두 다 나갔으면... 프로모션 없는 상품
                if (paymentSystem.isOrderProductQuantity(remainOrderProductQuantity)
                        && RetryUtil.confirmNonPromotionalPurchase(inputView,
                        outputView,
                        orderProductName,
                        remainOrderProductQuantity)) {
                    paymentSystem.basicPayment(orderProductName, remainOrderProductQuantity);
                }
            }

            if (!paymentSystem.isPromotionsApplicable(orderProductName, currentDate)) {
                paymentSystem.basicPayment(orderProductName, remainOrderProductQuantity);
            }

        }

        // 멤버십 할인
        if (RetryUtil.membershipDiscountChoice(inputView, outputView)) {
            paymentSystem.applyMembershipDiscount();
        }

        // 영수증 출력

    }

    private Products getProducts() {
        return FileUtil.loadProductsFromFile();
    }

    private void printProducts(Products products) {
        outputView.printIntroduction();
        outputView.printProducts(products.toProductsDto());
    }

    private OrderProduct getOrderProduct(Products products) {
        return RetryUtil.orderProduct(inputView, outputView, products);
    }

    private Promotions getPromotions() {
        return FileUtil.loadPromotionsFromFile();
    }

}
