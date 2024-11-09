package store.controller;

import store.domain.OrderProduct;
import store.domain.Products;
import store.util.FileUtil;
import store.view.OutputView;

public class StoreController {

    private final OutputView outputView;

    public StoreController(OutputView outputView) {
        this.outputView = outputView;
    }

    public void start() {
        Products products = getProducts();
        printProducts(products);

        OrderProduct orderProduct = RetryUtil.orderProduct(inputView, outputView, products);

    }

    private void printProducts() {
        ProductsDto productsDto = FileUtil.loadProductsFromFile().toProductsDto();

    private void printProducts(Products products) {
        outputView.printIntroduction();
        outputView.printProducts(products.toProductsDto());
    }

}
