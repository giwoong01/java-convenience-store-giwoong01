package store.view;

import camp.nextstep.edu.missionutils.Console;

public class InputView {

    public String inputProductNamesAndQuantity() {
        System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        return Console.readLine().trim();
    }

    public String inputFreePromotionChoice(String orderProductName, int promotionFreeQuantity) {
        System.out.printf("\n현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\n",
                orderProductName,
                promotionFreeQuantity);
        return Console.readLine().trim();
    }

    public String inputConfirmNonPromotionalPurchase(String orderProductName, int remainOrderProductQuantity) {
        System.out.printf("\n현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)\n",
                orderProductName,
                remainOrderProductQuantity);
        return Console.readLine().trim();
    }

    public String inputMembershipDiscountChoice() {
        System.out.println("\n멤버십 할인을 받으시겠습니까? (Y/N)");
        return Console.readLine().trim();
    }

    public String inputMoreProducts() {
        System.out.println("\n감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
        String input = Console.readLine().trim();
        System.out.println();
        return input;
    }

}
