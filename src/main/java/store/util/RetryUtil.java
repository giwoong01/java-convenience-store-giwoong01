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
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());
            return orderProduct(inputView, outputView, products);
        }
    }

    public static String freePromotionChoice(InputView inputView,
                                             OutputView outputView,
                                             String orderProductName) {
        try {
            String input = inputView.inputFreePromotionChoice(orderProductName);
            InputValidator.validateYesNoInput(input);
            return input;
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());
            return freePromotionChoice(inputView, outputView, orderProductName);
        }
    }

    public static String confirmNonPromotionalPurchase(InputView inputView,
                                                       OutputView outputView,
                                                       String orderProductName,
                                                       int remainOrderProductQuantity) {
        try {
            String input = inputView.inputConfirmNonPromotionalPurchase(orderProductName, remainOrderProductQuantity);
            InputValidator.validateYesNoInput(input);
            return input;
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());
            return confirmNonPromotionalPurchase(inputView, outputView, orderProductName, remainOrderProductQuantity);
        }
    }

    public static String membershipDiscountChoice(InputView inputView,
                                                  OutputView outputView) {
        try {
            String input = inputView.inputMembershipDiscountChoice();
            InputValidator.validateYesNoInput(input);
            return input;
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
