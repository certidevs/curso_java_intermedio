package com.certidevs.controller;

import com.certidevs.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
    // Metodo para obtener formulario vacío para crear un nuevo producto
    @GetMapping("products/create")
    public String getFormToCreate(Model model) {
        // producto vacío para enlazar sus atributos con los input del formulario
        model.addAttribute("product", new Product());
        // si quisieramos un selector de fabricantes, ....
        // model.addAttribute("manufactures", manufacturerRepository.findAll());
        return "product-form";
    }

    // getFormToUpdate
    // Metodo para obtener formulario relleno para editar un producto existente
    @GetMapping("products/edit/{id}")
    public String getFormToUpdate(@PathVariable Long id, Model model) {
        // productRepository.findById
        var product = Product.builder().id(id).name("ordenador").price(990.23).quantity(2).build();
        model.addAttribute("product", product);
        return "product-form";
    }

    // save
    @PostMapping("products")
    public String save(@ModelAttribute Product product) {
        // productRepository.save(product)
        log.info("Producto a guardar {}", product);
        return "redirect:/products";
    }

    @GetMapping("products/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model) {
        log.info("Producto a borrar {}", id);
        // productRepository.deleteById(id)
        return "redirect:/products";
    }


}
