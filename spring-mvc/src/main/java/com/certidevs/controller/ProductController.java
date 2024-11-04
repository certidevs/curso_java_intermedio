package com.certidevs.controller;

import com.certidevs.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    // http://localhost:8080/products/1
    // http://localhost:8080/products/2
    // http://localhost:8080/products/3
    @GetMapping("products/{id}")
    public String findById(@PathVariable Long id, Model model) {
        // Optional<Product> productOpt = productRepository.findById(id);
        var product = Product.builder().id(id).name("producto").price(33d).build();
        model.addAttribute("product", product);
        // opinionRepository.findByProductId()
        return "product-detail";
    }

    // getFormToCreate
    // http://localhost:8080/products/create
    @GetMapping("products/create")
    public String getFormToCreate(Model model) {
        // producto vacío para enlazar sus atributos con los input del formulario
        model.addAttribute("product", new Product());
        // si quisieramos un selector de fabricantes, ....
        // model.addAttribute("manufactures", manufacturerRepository.findAll());
        return "product-form";
    }

    // getFormToUpdate

    // save
    @PostMapping("products")
    public String save(@ModelAttribute Product product) {
        // productRepository.save(product)
        System.out.println(product);
        return "redirect:/products";
    }

    // POST products

    // deleteById

    // deleteAll


}
