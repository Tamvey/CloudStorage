package com.cstorage.controller;


import com.cstorage.service.DirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/home/search")
public class SearchController {

    @Autowired
    DirService dirService;

    @GetMapping
    public String searchGet(@RequestParam("entry") String entry,
                            Model model) {
        model.addAttribute("files", dirService.allFileEntries(entry));
        model.addAttribute("folders", dirService.allDirEntries(entry));
        return "search";
    }
}
