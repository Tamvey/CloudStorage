package com.cstorage.controller;

import com.cstorage.dto.file.FileDTO;
import com.cstorage.dto.query.QueryDTO;
import com.cstorage.service.ClientService;
import com.cstorage.service.DirService;
import com.cstorage.utils.DirUtil;
import com.cstorage.utils.FileUtil;
import io.minio.DownloadObjectArgs;
import io.minio.errors.*;
import org.apache.catalina.core.ApplicationPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.View;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
