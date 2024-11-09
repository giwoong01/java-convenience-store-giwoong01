package store.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import store.domain.Product;
import store.domain.Products;

public class FileUtil {

    public static Products loadProductsFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/products.md"))) {
            return new Products(readProducts(br));
        } catch (IOException e) {
            throw new RuntimeException("[ERROR] 파일을 읽는데 실패했습니다.");
        }
    }

    private static List<Product> readProducts(BufferedReader br) throws IOException {
        return br.lines()
                .skip(1)
                .map(ParseUtil::parseProductFromLine)
                .toList();
    }

}
