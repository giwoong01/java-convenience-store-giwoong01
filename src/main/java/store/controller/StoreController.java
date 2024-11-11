package store.controller;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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

            boolean isPromotionApplicable = paymentSystem.isPromotionsApplicable(productName, currentDate);

            if (isPromotionApplicable) {
                String applicablePromotion = products.findApplicablePromotion(productName);
                String productQuantity = products.getProductQuantity(productName);

                if (!Objects.equals(applicablePromotion, "") && productQuantity.equals("재고 없음")) {
                    isPromotionApplicable = false;
                } else {
                    handlePromotionalProduct(orderProduct, products, promotions, paymentSystem, productName);
                }
            }

            if (!isPromotionApplicable) {
                handleNonPromotionalProduct(paymentSystem, productName, remainingQuantity);
            }
        }

        BigDecimal membershipDiscount = applyMembershipDiscountIfEligible(paymentSystem);
        printReceipt(orderProduct, products, paymentSystem, membershipDiscount);
    }


    private void handlePromotionalProduct(OrderProduct orderProduct, Products products, Promotions promotions,
                                          PaymentSystem paymentSystem,
                                          String productName) {
        String promotion = products.findApplicablePromotion(productName);
        int requiredBuyQuantity = promotions.getPromotionBuyRequirement(promotion);
        int freeQuantity = promotions.getPromotionFreeQuantity(promotion);
        int productQuantity = ParseUtil.parseInt(products.getProductQuantity(productName));

        int updatedQuantity = paymentSystem.applyPromotionPayment(productName);

        if (paymentSystem.isEligibleForFreePromotion(updatedQuantity, productQuantity, requiredBuyQuantity,
                freeQuantity)) {
            String userFreePromotionChoice =
                    RetryUtil.freePromotionChoice(inputView, outputView, productName);
            if (userFreePromotionChoice.equals("Y")) {
                paymentSystem.YFreePromotionPayment(productName, updatedQuantity, freeQuantity);
            }

            if (userFreePromotionChoice.equals("N")) {
                paymentSystem.NFreePromotionPayment(productName, updatedQuantity);
            }
        }

        if (!paymentSystem.isEligibleForFreePromotion(updatedQuantity, productQuantity, requiredBuyQuantity,
                freeQuantity)) {
            applyPromotionDiscount(orderProduct, paymentSystem, productName, updatedQuantity, requiredBuyQuantity);
        }
    }

    private void applyPromotionDiscount(OrderProduct orderProduct, PaymentSystem paymentSystem, String productName,
                                        int updatedQuantity, int requiredBuyQuantity) {
        int orderProductQuantity = orderProduct.getOrderProductQuantity(productName);

        if (paymentSystem.isOrderProductQuantity(updatedQuantity, requiredBuyQuantity)) {
            String userConfirmNonPromotionalPurchase =
                    RetryUtil.confirmNonPromotionalPurchase(inputView, outputView, productName, updatedQuantity);
            if (userConfirmNonPromotionalPurchase.equals("Y")) {
                paymentSystem.YBasicPayment(productName, updatedQuantity);
            }

            if (userConfirmNonPromotionalPurchase.equals("N")) {
                paymentSystem.NBasicPayment(productName, orderProductQuantity, updatedQuantity);
            }
        }

        if (!paymentSystem.isOrderProductQuantity(updatedQuantity, requiredBuyQuantity)) {
            paymentSystem.basicPayment(productName, updatedQuantity);
        }

    }

    private void handleNonPromotionalProduct(PaymentSystem paymentSystem, String productName, int remainingQuantity) {
        paymentSystem.nonPromotionPayment(productName, remainingQuantity);
    }

    private BigDecimal applyMembershipDiscountIfEligible(PaymentSystem paymentSystem) {
        String userMembershipDiscountChoice = RetryUtil.membershipDiscountChoice(inputView, outputView);
        if (userMembershipDiscountChoice.equals("Y")) {
            return paymentSystem.applyMembershipDiscount();
        }
        return BigDecimal.ZERO;
    }

    private void printReceipt(OrderProduct orderProduct,
                              Products products,
                              PaymentSystem paymentSystem,
                              BigDecimal membershipDiscount) {
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
