package com.delgiudice.javafxcheckoutdemo;

import java.io.*;
import java.util.*;

// Klasa obsługująca wczytywanie produktów z pliku tekstowego
public class ProductLoader {

    private final List<ProductTemplate> productTemplateList;

    public List<ProductTemplate> getProductList() {
        return productTemplateList;
    }

    // Konstruktor automatycznie zajmuje się wczytaniem bazy danych i ich sortowaniem
    public ProductLoader(String filename) throws IOException {
        productTemplateList = new ArrayList<>();
        getProductsFromFile(filename);
        sortList();
        System.out.println("[DEBUG] Full list loaded and sorted");
    }

    // Metoda wczytująca pojedynczy produkt z linii pliku tekstowego
    private void loadItem(String data) {
        String[] splitData = data.split(";");

        // Sprawdzenie poprawności danych: ilość wydobytych danych oraz długość uzyskanego kodu
        // Kod powinien być 5-cyfrowy
        if (splitData.length >= 5 && splitData.length <= 6 && splitData[0].length() == 5) {

            float price;
            int weight, productInt;

            // Próba uzyskania odpowiednich danych, przy niepowodzeniu następuje wyjątek
            try {
                price = Float.parseFloat(splitData[2]);
                weight = Integer.parseInt(splitData[3]);
                productInt = Integer.parseInt(splitData[4]);

            } catch (NumberFormatException e) {
                System.out.println("Incorrect data format, check the data and try again.");
                throw new RuntimeException(e);
            }
            price *= 100;
            long priceLong = (long)price;
            ProductType productType = ProductType.values()[productInt];
            ProductTemplate productTemplate;
            if (splitData.length == 5)
                productTemplate = new ProductTemplate(splitData[0], splitData[1], priceLong, weight, productType);
            else
                productTemplate = new ProductTemplate(splitData[0], splitData[1], priceLong, weight, productType, splitData[5]);

            // Weryfikacja istnienia duplikatów w kodzie, powinien być tylko jeden reprezentant danego kodu
            for (ProductTemplate template : productTemplateList) {
                if (Objects.equals(template.getProductCode(), productTemplate.getProductCode())) {
                    System.out.println("[DEBUG] Object with specified code already exists, aborting...");
                    return;
                }
            }

            // Przy pomyślnej weryfikacji wszystkich powyższych warunków, produkt dodawany jest do listy
            productTemplateList.add(productTemplate);
            System.out.printf("[DEBUG] ProductTemplate added: %s, code: %s, price %.2f zł", productTemplate.getProductName(),
                    productTemplate.getProductCode(), productTemplate.getDisplayProductPrice());

            if (!productTemplate.isPriceByWeight())
                System.out.printf(", weight %d g", productTemplate.getProductWeight());
            else
                System.out.printf("/kg");

            System.out.printf(", custom icon: %s%n", productTemplate.getIconPath());
        }
        else {
            System.out.println("[DEBUG] Error loading data, incorrect format.");
        }
    }

    // Metoda sortująca listę zakupów alfabetycznie względem nazwy
    private void sortList() {
        productTemplateList.sort(Comparator.comparing(ProductTemplate::getProductName));
    }

    // Metoda wczytująca plik o nazwie podanej w argumencie, dodająca każdy poprawnie odczytany produkt
    private void getProductsFromFile(String filename) throws IOException {
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) loadItem(st);
    }

}
