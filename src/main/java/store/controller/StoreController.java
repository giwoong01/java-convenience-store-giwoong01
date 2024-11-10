package store.controller;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;
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
        Promotions promotions = getPromotions();

        do {
            printProducts(products);

            // 이 아래부터 코드 정리
            OrderProduct orderProduct = getOrderProduct(products);
            PaymentSystem paymentSystem = new PaymentSystem(products, orderProduct, promotions);

            List<String> orderProductNames = orderProduct.getOrderProductNames();
            LocalDateTime currentDate = DateTimes.now();

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
                            RetryUtil.freePromotionChoice(inputView, outputView, orderProductName,
                                    promotionFreeQuantity)) {
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
            int membershipDiscount = 0;
            if (RetryUtil.membershipDiscountChoice(inputView, outputView)) {
                membershipDiscount = paymentSystem.applyMembershipDiscount();
            }

            // 영수증 출력
            printReceipt(orderProduct,
                    products,
                    promotions,
                    paymentSystem,
                    paymentSystem.getTotalResult() - paymentSystem.getDiscountResult(),
                    membershipDiscount,
                    paymentSystem.getTotalResult(),
                    paymentSystem.getDiscountResult());

        } while (RetryUtil.moreProducts(inputView, outputView));

    }

    public static void printReceipt(OrderProduct orderProduct, Products products, Promotions promotions,
                                    PaymentSystem paymentSystem,
                                    int promotionDiscount,
                                    int membershipDiscount, int totalResult, int discountResult) {
        // 영수증 헤더
        System.out.println("\n==============W 편의점================");
        System.out.println("상품명\t\t수량\t금액");

        // 상품 출력
        List<String> orderProductNames = orderProduct.getOrderProductNames();

        for (String name : orderProductNames) {
            int orderProductQuantity = orderProduct.getOrderProductQuantity(name);
            int freePromotionQuantity = orderProduct.getFreePromotionProductQuantity(name);
            int totalQuantity = orderProductQuantity + freePromotionQuantity;
            int price = products.findApplicablePrice(name);
            System.out.printf("%s\t\t%d\t%,d\n",
                    name,
                    totalQuantity,
                    price * totalQuantity);  // 가격은 가격 * 총 수량
        }

        // 행사 할인 출력
        System.out.println("=============증\t정===============");
        for (String name : paymentSystem.getFreePromotionProducts()) {
            System.out.printf("%s\t\t%d\n", name,
                    promotions.getPromotionFreeQuantity(products.findApplicablePromotion(name)));
        }

        // 멤버십 할인 내역 출력
        System.out.println("==================================");
        System.out.printf("총구매액\t\t\t%,d\n", totalResult);
        System.out.printf("행사할인\t\t\t-%,d\n", promotionDiscount);
        System.out.printf("멤버십할인\t\t\t-%,d\n", membershipDiscount);
        System.out.printf("내실돈\t\t\t\t %,d\n", discountResult - membershipDiscount);
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
