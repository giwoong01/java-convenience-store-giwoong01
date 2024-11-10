package store.domain;

import java.time.LocalDate;
import java.util.List;

public class Promotions {

    private final List<Promotion> promotions;

    public Promotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public boolean isPromotionsApplicable(String productPromotion, LocalDate currentDate) {
        return promotions.stream()
                .anyMatch(promotion -> promotion.isPromotionApplicable(productPromotion, currentDate));
    }

    public Integer getPromotionBuyRequirement(String productPromotion) {
        return promotions.stream()
                .filter(promotion -> promotion.isNameMatching(productPromotion))
                .map(Promotion::getBuy)
                .findFirst()
                .orElse(null);
    }

    public Integer getPromotionFreeQuantity(String productPromotion) {
        return promotions.stream()
                .filter(promotion -> promotion.isNameMatching(productPromotion))
                .map(Promotion::getGet)
                .findFirst()
                .orElse(null);
    }

    public List<Promotion> getPromotions() {
        return List.copyOf(promotions);
    }

}
