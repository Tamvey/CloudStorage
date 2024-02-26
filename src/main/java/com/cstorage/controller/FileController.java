package com.cstorage.controller;

import com.cstorage.dto.file.FileDTO;
import com.cstorage.service.DirService;
import com.cstorage.service.FileService;
import com.cstorage.utils.DirUtil;
import com.cstorage.utils.FileUtil;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/home/file")
public class FileController {

    @Autowired
    FileService fileService;
    @Autowired
    DirService dirService;
    @GetMapping
    public ResponseEntity<Resource> fileGet(@RequestParam("path") String path) throws IOException {
        File file = fileService.downloadFile(path);
        var encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename="+encodedFileName);
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(new FileInputStream(file.getAbsolutePath()), "UTF-8"));
    }

    @PostMapping
    public String filePost(@ModelAttribute FileDTO fileDTO,
                           @RequestParam("path") String path,
                           RedirectAttributes redirectAttributes) {
        fileService.uploadFile(fileDTO, path);
        redirectAttributes.addAttribute("path", path);
        return "redirect:/home/folder";
    }
    @DeleteMapping
    public String fileDelete(@RequestParam("path") String path,
                             RedirectAttributes redirectAttributes) {
        fileService.deleteFile(path);
        String gotPath = DirUtil.getPath(path);
        if (dirService.folderExists(gotPath))
            redirectAttributes.addAttribute("path", gotPath);
        return "redirect:/home/folder";
    }
    @PutMapping
    public String filePut(@RequestParam("path") String path,
                          @RequestParam("after") String after,
                          RedirectAttributes redirectAttributes) {
        fileService.renameFile(path, after);
        redirectAttributes.addAttribute("path", DirUtil.getPath(path));
        return "redirect:/home/folder";
    }
}
