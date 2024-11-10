package store.domain;

import java.time.LocalDate;

public class Promotion {

    private final String name;
    private final int buy;
    private final int get;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(String name, int buy, int get, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Promotion createPromotion(String name, int buy, int get,
                                            LocalDate startDate,
                                            LocalDate endDate) {
        return new Promotion(
                name,
                buy,
                get,
                startDate,
                endDate);
    }

    public boolean isPromotionApplicable(String productPromotion, LocalDate currentDate) {
        return isNameMatching(productPromotion) && isDateAndQuantityValid(currentDate);
    }

    public boolean isNameMatching(String productPromotion) {
        return name.equals(productPromotion);
    }

    private boolean isDateAndQuantityValid(LocalDate currentDate) {
        return !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate);
    }

    public String getName() {
        return name;
    }

    public int getBuy() {
        return buy;
    }

    public int getGet() {
        return get;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

}
