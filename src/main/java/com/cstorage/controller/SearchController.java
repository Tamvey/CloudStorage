package com.cstorage.controller;

import com.cstorage.dto.query.QueryDTO;
import com.cstorage.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


public class SearchController {

    @Autowired
    DirService dirService;

    @PostMapping("/home/folder/search")
    public void search(@ModelAttribute("query") QueryDTO query) {

    }
}
