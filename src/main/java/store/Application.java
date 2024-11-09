package store;

import store.controller.StoreController;
import store.view.OutputView;

public class Application {

    public static void main(String[] args) {
        StoreController storeController = createStoreController();
        storeController.start();
    }

    private static StoreController createStoreController() {
        return new StoreController(new OutputView());
    }

}
