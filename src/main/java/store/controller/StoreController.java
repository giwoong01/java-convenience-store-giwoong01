package store.controller;

import store.domain.OrderProduct;
import store.domain.Products;
import store.util.FileUtil;
import store.util.RetryUtil;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {

    private final InputView inputView;
    private final OutputView outputView;

    public StoreController(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void start() {
        Products products = getProducts();
        printProducts(products);

        OrderProduct orderProduct = RetryUtil.orderProduct(inputView, outputView, products);

    }

    private Products getProducts() {
        return FileUtil.loadProductsFromFile();
    }

    private void printProducts(Products products) {
        outputView.printIntroduction();
        outputView.printProducts(products.toProductsDto());
    }

}
