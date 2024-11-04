package com.certidevs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ProductController {

    // http://localhost:8080/hola
    @GetMapping("hola")
    public String holaMundo() {
        return "hola-mundo";
    }


}
