package com.cstorage.controller;

import com.cstorage.dto.file.FileDTO;
import com.cstorage.dto.folder.FolderDTO;
import com.cstorage.service.ClientService;
import com.cstorage.service.DirService;
import com.cstorage.utils.DirUtil;
import com.cstorage.utils.Pair;
import io.minio.UploadObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/home/folder")
public class FolderController {

    @Autowired
    ClientService clientService;
    @Autowired
    DirService dirService;

    @GetMapping
    public String folderGet(@Nullable @RequestParam("path") String path,
                            Model model) {
        model.addAttribute("folderDto", new FolderDTO());
        model.addAttribute("fileDto", new FileDTO());
        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        if (path != null && DirUtil.validate(path)) {
            model.addAttribute("tree", DirUtil.getMapOfChildren(path));
            model.addAttribute("root", path);
            model.addAttribute("folders", dirService.listFolders(path));
            model.addAttribute("files", dirService.listObjects(path));
        } else {
            model.addAttribute("tree", List.of(new Pair("root", null)));
            model.addAttribute("root", "");
            model.addAttribute("folders", dirService.listRootFolders());
            model.addAttribute("files", dirService.listRootObjects());
        }
        return "home";
    }

    @PostMapping
    public String folderPost(@RequestParam("path") String path,
                             @ModelAttribute FolderDTO folderDTO,
                             RedirectAttributes redirectAttributes) {
        dirService.addFolder(path+folderDTO.getFolderName());
        redirectAttributes.addAttribute("path", path);
        return "redirect:/home/folder";
    }

    @DeleteMapping
    public String folderDelete(@Nullable @RequestParam("path") String path) {
        dirService.deleteFolder(path);
        return "redirect:/home/folder";
    }

    @PutMapping
    public String folderPut(@RequestParam("before") String before,
                            @RequestParam("after") String after,
                            RedirectAttributes redirectAttributes) {
        dirService.renameFolder(before, after);
        redirectAttributes.addAttribute("path", after);
        return "redirect:/home/folder";
    }
}
