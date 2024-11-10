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
import store.dto.OrderProductDto;
import store.util.FileUtil;
import store.util.ParseUtil;
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

        if (paymentSystem.isEligibleForFreePromotion(orderQuantity, requiredBuyQuantity, freeQuantity)) {
            String userFreePromotionChoice =
                    RetryUtil.freePromotionChoice(inputView, outputView, productName, freeQuantity);
            if (userFreePromotionChoice.equalsIgnoreCase("Y")) {
                paymentSystem.YFreePromotionPayment(productName, remainingQuantity, freeQuantity);
            }

            if (userFreePromotionChoice.equalsIgnoreCase("N")) {
                paymentSystem.NFreePromotionPayment(productName, remainingQuantity);
            }

        }

        if (!paymentSystem.isEligibleForFreePromotion(orderQuantity, requiredBuyQuantity, freeQuantity)) {
            applyPromotionDiscount(products, paymentSystem, productName);
        }
    }

    private void applyPromotionDiscount(Products products, PaymentSystem paymentSystem, String productName) {
        int orderProductQuantity = ParseUtil.parseInt(products.getProductQuantity(productName));
        int updatedQuantity = paymentSystem.applyPromotionPayment(productName);

        if (paymentSystem.isOrderProductQuantity(updatedQuantity)) {
            String userConfirmNonPromotionalPurchase =
                    RetryUtil.confirmNonPromotionalPurchase(inputView, outputView, productName, updatedQuantity);
            if (userConfirmNonPromotionalPurchase.equalsIgnoreCase("Y")) {
                paymentSystem.YBasicPayment(productName, updatedQuantity);
            }

            if (userConfirmNonPromotionalPurchase.equalsIgnoreCase("N")) {
                paymentSystem.NBasicPayment(productName, orderProductQuantity);
            }
        }

    }

    private void handleNonPromotionalProduct(PaymentSystem paymentSystem, String productName, int remainingQuantity) {
        paymentSystem.nonPromotionPayment(productName, remainingQuantity);
    }

    private int applyMembershipDiscountIfEligible(PaymentSystem paymentSystem) {
        String userMembershipDiscountChoice = RetryUtil.membershipDiscountChoice(inputView, outputView);
        if (userMembershipDiscountChoice.equalsIgnoreCase("Y")) {
            return paymentSystem.applyMembershipDiscount();
        }
        return 0;
    }

    private void printReceipt(OrderProduct orderProduct,
                              Products products,
                              PaymentSystem paymentSystem,
                              int membershipDiscount) {
        List<OrderProductDto> orderProductDetails = ReceiptIssuer.getOrderProductDetails(orderProduct, products);
        outputView.printReceipt(
                ReceiptIssuer.getOrderProductDetails(orderProduct, products),
                ReceiptIssuer.getPromotionDetails(paymentSystem),
                paymentSystem.getTotalResult(),
                paymentSystem.getTotalResult() - paymentSystem.getDiscountResult(),
                membershipDiscount,
                paymentSystem.getDiscountResult(),
                ReceiptIssuer.getTotalQuantity(orderProductDetails));
    }

}
