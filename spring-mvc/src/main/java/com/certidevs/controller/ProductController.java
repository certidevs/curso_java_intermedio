package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Controller
public class ProductController {

    private final ProductRepository productRepository;

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
        model.addAttribute("products", productRepository.findAll());
        return "product-list";
    }

    // findById
    // http://localhost:8080/products/1
    // http://localhost:8080/products/2
    // http://localhost:8080/products/3
    @GetMapping("products/{id}")
    public String findById(@PathVariable Long id, Model model) {
//         Optional<Product> productOpt = productRepository.findById(id);
//         if(productOpt.isPresent())
//             model.addAttribute("product", productOpt.get());
        productRepository.findById(id).ifPresent(product -> model.addAttribute("product", product));
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
        productRepository.findById(id).ifPresent(product -> model.addAttribute("product", product));

//        return productRepository.findById(id).map(product -> {
//            model.addAttribute("product", product);
//            return "product-detail";
//        })
//                .orElse("error")
//                .orElseThrow(() -> new NoSuchElementException("producto no encontrado"))
//                ;

        return "product-form";
    }

    // save
    @PostMapping("products")
    public String save(@ModelAttribute Product product) {
        log.info("Producto a guardar {}", product);
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("products/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        log.info("Producto a borrar {}", id);
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error borrando producto", e);
        }
        return "redirect:/products";
    }


}
