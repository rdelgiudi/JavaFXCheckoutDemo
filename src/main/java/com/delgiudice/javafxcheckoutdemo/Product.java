package com.delgiudice.javafxcheckoutdemo;

//Klasa reprezentująca produkt w koszyku klienta, szczególnie przydatna do śledzenia ceny produktów na wagę
public class Product{

    private final int measuredWeight;

    private final long measuredPrice;

    private final ProductTemplate productTemplate;

    public int getMeasuredWeight() {
        return measuredWeight;
    }

    public long getMeasuredPrice() {
        return measuredPrice;
    }

    public double getDisplayMeasuredPrice() {
        return measuredPrice / 100.0;
    }

    public ProductTemplate getProductTemplate() {
        return productTemplate;
    }

    public Product(ProductTemplate template, int weight) {
        productTemplate = template;
        measuredWeight = weight;
        if (template.isPriceByWeight()) {
            float price = template.getProductPrice() * ((float) weight / 1000.0f);
            measuredPrice = Math.round(price);
        }
        else
            measuredPrice = template.getProductPrice();
    }
}
