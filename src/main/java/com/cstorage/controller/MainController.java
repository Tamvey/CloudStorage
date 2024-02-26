package com.cstorage.controller;


import com.cstorage.service.ClientService;
import com.cstorage.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class MainController {
    @Autowired
    ClientService clientService;
    @Autowired
    DirService dirService;
    @GetMapping("/")
    public String base() {
        clientService.buildClient();
        return "redirect:/home/folder";
    }
}
