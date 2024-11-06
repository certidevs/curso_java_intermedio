package com.certidevs;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        var context = SpringApplication.run(Main.class, args);
        var productRepository = context.getBean(ProductRepository.class);
        var productsNumber = productRepository.count();
        if (productsNumber == 0) {
            var product = Product.builder().name("Teclado").price(40d).quantity(2).build();
            productRepository.save(product);
        }
    }

}
