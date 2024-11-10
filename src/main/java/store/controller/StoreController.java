package store.controller;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDateTime;
import java.util.List;
import store.domain.OrderProduct;
import store.domain.PaymentSystem;
import store.domain.Products;
import store.domain.Promotions;
import store.domain.ReceiptIssuer;
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
            OrderProduct orderProduct = getOrderProduct(products);
            PaymentSystem paymentSystem = new PaymentSystem(products, orderProduct, promotions);
            processOrder(orderProduct, products, paymentSystem, promotions);
        } while (RetryUtil.moreProducts(inputView, outputView));
        Console.close();
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

    private void processOrder(OrderProduct orderProduct, Products products, PaymentSystem paymentSystem,
                              Promotions promotions) {
        List<String> orderProductNames = orderProduct.getOrderProductNames();
        LocalDateTime currentDate = DateTimes.now();

        for (String productName : orderProductNames) {
            int remainingQuantity = orderProduct.getOrderProductQuantity(productName);

            if (paymentSystem.isPromotionsApplicable(productName, currentDate)) {
                handlePromotionalProduct(orderProduct, products, promotions, paymentSystem, productName,
                        remainingQuantity);
            }

            if (!paymentSystem.isPromotionsApplicable(productName, currentDate)) {
                handleNonPromotionalProduct(paymentSystem, productName, remainingQuantity);
            }
        }

        int membershipDiscount = applyMembershipDiscountIfEligible(paymentSystem);
        printReceipt(orderProduct, products, paymentSystem, membershipDiscount);
    }

    private void handlePromotionalProduct(OrderProduct orderProduct, Products products, Promotions promotions,
                                          PaymentSystem paymentSystem,
                                          String productName, int remainingQuantity) {
        int orderQuantity = orderProduct.getOrderProductQuantity(productName);
        String promotion = products.findApplicablePromotion(productName);
        int requiredBuyQuantity = promotions.getPromotionBuyRequirement(promotion);
        int freeQuantity = promotions.getPromotionFreeQuantity(promotion);

        if (isEligibleForFreePromotion(paymentSystem, productName, orderQuantity, requiredBuyQuantity, freeQuantity)) {
            applyFreePromotion(paymentSystem, productName, remainingQuantity, freeQuantity);
        }

        if (!paymentSystem.isEligibleForFreePromotion(orderQuantity, requiredBuyQuantity, freeQuantity)) {
            applyPromotionDiscount(paymentSystem, productName);
        }
    }

    private boolean isEligibleForFreePromotion(PaymentSystem paymentSystem, String productName, int orderQuantity,
                                               int requiredBuyQuantity, int freeQuantity) {
        return paymentSystem.isEligibleForFreePromotion(orderQuantity, requiredBuyQuantity, freeQuantity) &&
                RetryUtil.freePromotionChoice(inputView, outputView, productName, freeQuantity);
    }

    private void applyFreePromotion(PaymentSystem paymentSystem, String productName, int remainingQuantity,
                                    int freeQuantity) {
        paymentSystem.freePromotionPayment(productName, remainingQuantity, freeQuantity);
    }

    private void applyPromotionDiscount(PaymentSystem paymentSystem, String productName) {
        int updatedQuantity = paymentSystem.applyPromotionDiscount(productName);

        if (shouldConfirmNonPromotionalPurchase(paymentSystem, productName, updatedQuantity)) {
            paymentSystem.basicPayment(productName, updatedQuantity);
        }
    }

    private boolean shouldConfirmNonPromotionalPurchase(PaymentSystem paymentSystem, String productName,
                                                        int updatedQuantity) {
        return paymentSystem.isOrderProductQuantity(updatedQuantity) &&
                RetryUtil.confirmNonPromotionalPurchase(inputView, outputView, productName, updatedQuantity);
    }

    private void handleNonPromotionalProduct(PaymentSystem paymentSystem, String productName, int remainingQuantity) {
        paymentSystem.basicPayment(productName, remainingQuantity);
    }

    private int applyMembershipDiscountIfEligible(PaymentSystem paymentSystem) {
        if (RetryUtil.membershipDiscountChoice(inputView, outputView)) {
            return paymentSystem.applyMembershipDiscount();
        }
        return 0;
    }

    private void printReceipt(OrderProduct orderProduct,
                              Products products,
                              PaymentSystem paymentSystem,
                              int membershipDiscount) {
        outputView.printReceipt(
                ReceiptIssuer.getOrderProductDetails(orderProduct, products),
                ReceiptIssuer.getPromotionDetails(paymentSystem),
                paymentSystem.getTotalResult(),
                paymentSystem.getTotalResult() - paymentSystem.getDiscountResult(),
                membershipDiscount,
                paymentSystem.getDiscountResult());
    }

}
