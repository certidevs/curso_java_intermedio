package com.certidevs.controller;

import com.certidevs.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ProductController {

    // http://localhost:8080/hola
    @GetMapping("hola")
    public String holaMundo(Model model) {
        model.addAttribute("mensaje", "Texto escrito desde Java");
        model.addAttribute("fecha", LocalDateTime.now());
        model.addAttribute("nombres", List.of("alan", "paco", "patricia"));
        return "hola-mundo";
    }


    // http://localhost:8080/products
    @GetMapping("products")
    public String findAll(Model model) {

        // var products = productRepository.findAll()
        List<Product> products = List.of(
                new Product(1L, null, 33.12, 3),
                Product.builder().id(2L).name("silla").price(500d).quantity(1).build(),
                Product.builder().id(3L).name("Microfono").price(500d).quantity(1).build(),
                Product.builder().id(4L).name("Mesa").price(500d).quantity(1).build()
        );
        model.addAttribute("products", products);

        return "product-list";
    }

    // findById

    // getFormToCreate

    // getFormToUpdate

    // save

    // deleteById

    // deleteAll


}
