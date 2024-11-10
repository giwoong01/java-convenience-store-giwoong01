package store.validator;

public class InputValidator {

    public static void validateYesNoInput(String input) {
        if (!"Y".equalsIgnoreCase(input) && !"N".equalsIgnoreCase(input)) {
            throw new IllegalArgumentException("[ERROR] 잘못된 입력입니다. 다시 입력해 주세요.");
        }
    }

}
