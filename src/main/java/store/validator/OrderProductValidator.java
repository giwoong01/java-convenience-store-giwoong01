package store.validator;

import java.util.List;

public class OrderProductValidator {

    public static void validateProductExists(String orderProductName, List<String> productName) {
        if (!productName.contains(orderProductName)) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
    }

    public static void validateProductQuantity(int orderProductQuantity, int productQuantity) {
        if (orderProductQuantity > productQuantity) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    public static void validatePositiveStockQuantity(int orderProductQuantity) {
        if (orderProductQuantity < 0) {
            throw new IllegalArgumentException("[ERROR] 재고 수량은 음수가 될 수 없습니다. 다시 입력해 주세요.");
        }
    }

}
