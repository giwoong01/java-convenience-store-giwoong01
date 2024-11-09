package store.util;

import store.domain.Product;

public class ParseUtil {

    public static int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("[ERROR] 숫자로 변환하는데 실패했습니다.");
        }
    }

    public static Product parseProductFromLine(String line) {
        String[] lineSplit = line.split(",");

        String name = lineSplit[0].trim();
        String price = lineSplit[1].trim();
        String quantity = lineSplit[2].trim();
        String promotion = lineSplit[3].trim();

        return Product.createProduct(name, price, quantity, promotion);
    }

}
