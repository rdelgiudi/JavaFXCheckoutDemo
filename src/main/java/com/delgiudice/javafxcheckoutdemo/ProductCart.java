package com.delgiudice.javafxcheckoutdemo;

import java.util.ArrayList;
import java.util.List;

// Koszyk klienta, zawierający listę produktów w koszyku oraz ich łączną cenę i masę
public class ProductCart {

    private final ProductLoader productLoader;

    private final List<Product> cartList;

    private long totalPrice = 0;
    private int totalWeight = 0;

    public long getTotalPrice() {
        return totalPrice;
    }

    public double getDisplayTotalPrice() {
        return  totalPrice / 100.0;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public ProductCart(ProductLoader productLoader) {
        this.productLoader = productLoader;
        cartList = new ArrayList<>();
    }

    // Metoda wyszukująca produkt o danym kodzie w bazie produktów
    public ProductTemplate getProductWithCode(String code) {
        ProductTemplate productTemplate = productLoader.getProductList().stream()
                .filter(item -> item.getProductCode().equals(code))
                .findFirst()
                .orElse(null);

        return productTemplate;
    }

    // Metoda czyszcząca stan koszyka
    public void clear() {
        totalPrice = 0;
        totalWeight = 0;
        cartList.clear();
    }

    // Metoda dodająca produkt do koszyka, weryfikuje także, czy masy położonego produktu pokrywa się z oczekiwaniami
    public boolean addToCart(String code, int weight) {

        ProductTemplate productTemplate = getProductWithCode(code);

        if (productTemplate == null) {
            System.out.println("ProductTemplate not found!");
            return false;
        }

        Product product = new Product(productTemplate, weight);

        // Weryfikacja masy
        if (!productTemplate.isPriceByWeight()) {
            int product_weight = productTemplate.getProductWeight();
            if (weight > product_weight + 10 || weight < product_weight - 10) {
                System.out.println("Incorrect weight, adding product process aborted");
                return false;
            }
            totalPrice += productTemplate.getProductPrice();
        }
        else {
            totalPrice += product.getMeasuredPrice();
        }

        totalWeight += weight;
        cartList.add(product);

        System.out.printf("Product %s added to cart %n", productTemplate.getProductName());
        return true;
    }

    // Metoda usuwająca produkt z koszyka
    public void deleteFromCart(int index) {
        totalPrice -= cartList.get(index).getProductTemplate().getProductPrice();
        totalWeight -= cartList.get(index).getMeasuredWeight();
        cartList.remove(index);
    }

    public Product getFromCart(int index) {
        return cartList.get(index);
    }

    public int getCartSize() {
        return cartList.size();
    }

    public Product getLast() {
        return cartList.get(cartList.size() - 1);
    }

}
