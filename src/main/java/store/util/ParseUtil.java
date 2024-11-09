package store.util;

import java.util.LinkedHashMap;
import java.util.Map;
import store.domain.OrderProduct;
import store.domain.Product;
import store.domain.Products;
import store.domain.Promotion;

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

    public static OrderProduct parseOrderProduct(String input, Products products) {
        try {
            String[] inputSplit = input.replaceAll("[\\[\\]]", "").split(",");

            return OrderProduct.createOrderProduct(initializeProduct(inputSplit), products);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
        }
    }

    private static Map<String, Integer> initializeProduct(String[] inputSplit) {
        Map<String, Integer> productNamesAndQuantity = new LinkedHashMap<>();

        for (String productNameAndQuantity : inputSplit) {
            addProduct(productNamesAndQuantity, productNameAndQuantity);
        }

        return productNamesAndQuantity;
    }

    private static void addProduct(Map<String, Integer> productNamesAndQuantity, String productNameAndQuantity) {
        String[] productNameAndQuantitySplit = productNameAndQuantity.split("-");
        productNamesAndQuantity.put(productNameAndQuantitySplit[0], parseInt(productNameAndQuantitySplit[1]));
    }

    public static Promotion parsePromotionFromLine(String line) {
        String[] lineSplit = line.split(",");

        String name = lineSplit[0].trim();
        String buy = lineSplit[1].trim();
        String get = lineSplit[2].trim();
        String start_date = lineSplit[3].trim();
        String end_date = lineSplit[4].trim();

        return Promotion.createPromotion(name, buy, get, start_date, end_date);
    }

}
