package com.delgiudice.javafxcheckoutdemo;

// Klasa reprezentująca produkt z bazy danych
public class ProductTemplate {

    private final String productName, productCode, iconPath;

    private final long productPrice;

    private final int productWeight;

    private final boolean priceByWeight;

    ProductType productType;

    public String getProductName() {
        return productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public double getDisplayProductPrice() {return productPrice / 100.0;}

    public int getProductWeight() {
        return productWeight;
    }

    public boolean isPriceByWeight() {
        return priceByWeight;
    }

    public ProductType getProductType() {
        return productType;
    }

    public String getIconPath() {
        return iconPath;
    }

    public ProductTemplate(String code, String name, long price, int weight, ProductType type) {
        productCode = code;
        productName = name;
        productPrice = price;
        productWeight = weight;
        priceByWeight = productWeight == -1;
        productType = type;
        iconPath = "/default.png";
    }
    public ProductTemplate(String code, String name, long price, int weight, ProductType type, String icon) {
        productCode = code;
        productName = name;
        productPrice = price;
        productWeight = weight;
        priceByWeight = productWeight == -1;
        productType = type;
        iconPath = icon;
    }

}
