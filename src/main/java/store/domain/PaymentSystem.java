package store.domain;

public class PaymentSystem {

    private final Products products;
    private final OrderProduct orderProduct;
    private final Promotions promotions;

    public PaymentSystem(Products products, OrderProduct orderProduct, Promotions promotions) {
        this.products = products;
        this.orderProduct = orderProduct;
        this.promotions = promotions;
    }

}
