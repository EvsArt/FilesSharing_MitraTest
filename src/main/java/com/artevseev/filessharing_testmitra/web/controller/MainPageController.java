package com.artevseev.filessharing_testmitra.web.controller;

import com.artevseev.filessharing_testmitra.configuration.DataManager;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UploadedFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/home")
@Slf4j
public class MainPageController {

    private final UploadedFileRepository uploadedFileRepository;
    private final RoleRepository roleRepository;
    private final DataManager dataManager;

    public MainPageController(UploadedFileRepository uploadedFileRepository, RoleRepository roleRepository, DataManager dataManager){
        this.uploadedFileRepository = uploadedFileRepository;
        this.roleRepository = roleRepository;
        this.dataManager = dataManager;
    }

    @Value("${upload.path}")
    private String path;

    @Value("${host.name}")
    private String hostName;

    @ModelAttribute("hostName")
    public String getHostName(){
        return hostName;
    }


    @GetMapping
    public String getPage(@AuthenticationPrincipal User user,
                          Model model){
        List<UploadedFile> files = uploadedFileRepository.findAllByUser(user);
        model.addAttribute("files", files);
        model.addAttribute("isAdmin", user.getRole().equals(roleRepository.findRoleByName("ADMIN")));
        return "mainPage";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file")MultipartFile file,
                             @AuthenticationPrincipal User user,
                             Model model){


        if (!file.isEmpty()) {
            UploadedFile uploadedFile = new UploadedFile(file, user);
            try {
                UploadedFile savedFile = dataManager.saveFile(uploadedFile);
                log.info("The file {} was saved by {} in browser", savedFile.getName(), user.getLogin());
            } catch (IOException e) {
                model.addAttribute("message", "Возникла внутренняя ошибка на сервере! Мы уже пытаемся её устранить!");
            }

            model.addAttribute("message", "Файл " + uploadedFile.getName() + " успешно загружен на сервер!");
            uploadedFile = uploadedFileRepository.findByName(uploadedFile.getName());
            model.addAttribute("link", uploadedFile.getId() + "_" + uploadedFile.getLink());
        }
        else {
            model.addAttribute("message", "Не удалось загрузить " + file.getOriginalFilename() + " потому что файл пустой.");
        }

        return "afterUploadPage";

    }

    @GetMapping("/showall")
    public String showAll(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("files", uploadedFileRepository.findAllByOrderByName());
        log.info("All files have been viewed by {}", user.getLogin());
        return "filesListPage";
    }

}
