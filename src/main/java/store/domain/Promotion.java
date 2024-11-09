package store.domain;

import store.util.ParseUtil;

public class Promotion {

    private final String name;
    private final int buy;
    private final int get;
    private final String startDate;
    private final String endDate;

    public Promotion(String name, int buy, int get, String startDate, String endDate) {
        this.name = name;
        this.buy = buy;
        this.get = get;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Promotion createPromotion(String name, String buy, String get, String startDate, String endDate) {
        return new Promotion(
                name,
                ParseUtil.parseInt(buy),
                ParseUtil.parseInt(get),
                startDate,
                endDate);
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

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

}
