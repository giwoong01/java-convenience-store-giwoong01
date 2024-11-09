package store.util;

import store.domain.OrderProduct;
import store.domain.Products;
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

}
