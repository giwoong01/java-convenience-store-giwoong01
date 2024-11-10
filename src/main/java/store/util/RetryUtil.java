package store.util;

import store.domain.OrderProduct;
import store.domain.Products;
import store.validator.InputValidator;
import store.view.InputView;
import store.view.OutputView;

public class RetryUtil {

    public static OrderProduct orderProduct(InputView inputView, OutputView outputView, Products products) {
        try {
            return ParseUtil.parseOrderProduct(inputView.inputProductNamesAndQuantity(), products);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            outputView.printErrorMessage(e.getMessage());
            return orderProduct(inputView, outputView, products);
        }
    }

    public static boolean freePromotionChoice(InputView inputView,
                                              OutputView outputView,
                                              String orderProductName,
                                              int promotionFreeQuantity) {
        try {
            String input = inputView.inputFreePromotionChoice(orderProductName, promotionFreeQuantity);
            InputValidator.validateYesNoInput(input);
            return input.equalsIgnoreCase("Y");
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());
            return freePromotionChoice(inputView, outputView, orderProductName, promotionFreeQuantity);
        }
    }

    public static boolean confirmNonPromotionalPurchase(InputView inputView,
                                                        OutputView outputView,
                                                        String orderProductName,
                                                        int remainOrderProductQuantity) {
        try {
            String input = inputView.inputConfirmNonPromotionalPurchase(orderProductName, remainOrderProductQuantity);
            InputValidator.validateYesNoInput(input);
            return input.equalsIgnoreCase("Y");
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());
            return confirmNonPromotionalPurchase(inputView, outputView, orderProductName, remainOrderProductQuantity);
        }
    }

    public static boolean membershipDiscountChoice(InputView inputView,
                                                   OutputView outputView) {
        try {
            String input = inputView.inputMembershipDiscountChoice();
            InputValidator.validateYesNoInput(input);
            return input.equalsIgnoreCase("Y");
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());
            return membershipDiscountChoice(inputView, outputView);
        }
    }

    public static boolean moreProducts(InputView inputView, OutputView outputView) {
        try {
            String input = inputView.inputMoreProducts();
            InputValidator.validateYesNoInput(input);
            return input.equalsIgnoreCase("Y");
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());
            return moreProducts(inputView, outputView);
        }
    }

}
