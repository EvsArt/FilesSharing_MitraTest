package com.artevseev.filessharing_testmitra.web.controller;

import com.artevseev.filessharing_testmitra.configuration.DataManager;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UploadedFileRepository;
import com.artevseev.filessharing_testmitra.web.data.service.UploadedFileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@RequestMapping("/file")
@Slf4j
// String id is id_link(from table)
public class FileController {

    @Value("${upload.path}")
    private String uploadPath;

    private final UploadedFileRepository uploadedFileRepository;
    private final RoleRepository roleRepository;
    private final DataManager dataManager;
    private final UploadedFileService uploadedFileService;

    public FileController(UploadedFileRepository uploadedFileRepository,
                          RoleRepository roleRepository,
                          DataManager dataManager,
                          UploadedFileService uploadedFileService) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.roleRepository = roleRepository;
        this.dataManager = dataManager;
        this.uploadedFileService = uploadedFileService;
    }

    @GetMapping("/{id}")
    public String getFile(@PathVariable String id,
                          Model model,
                          @AuthenticationPrincipal User user) {

        model.addAttribute("fileIsExist", false);
        long fileId = Integer.parseInt(id.split("_")[0]);
        int link = Integer.parseInt(id.split("_")[1]);
        Optional<UploadedFile> file = uploadedFileRepository.findById(fileId);
        if (file.isPresent() && file.get().getLink() == link) {

            model.addAttribute("fileIsExist", true);
            model.addAttribute("file", file.get());
            model.addAttribute("isOwner", user != null && (file.get().getUser().equals(user) ||
                    user.getRole().equals(roleRepository.findRoleByName("ADMIN"))));

        } else {
            log.warn("There was an attempt to get file with wrong link by {}", user.getLogin());
        }

        return "filePage";
    }

    @GetMapping("/{id}/download")
    @ResponseBody
    public void downloadFile(@PathVariable String id,
                             HttpServletResponse response,
                             @AuthenticationPrincipal User user) {

        long fileId = Integer.parseInt(id.split("_")[0]);
        int link = Integer.parseInt(id.split("_")[1]);
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);
        if (uploadedFile.isPresent() && uploadedFile.get().getLink() == link) {
            uploadedFileService.download(response, uploadedFile.get(), user);
        }
    }


    @GetMapping("/{id}/delete")
    public String deleteFile(@PathVariable("id") String id,
                             @AuthenticationPrincipal User user) {

        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(Long.parseLong(id.split("_")[0]));
        uploadedFile.ifPresent(file -> {
            if (dataManager.deleteFile(id, user))
                log.info("The file {} was deleted by {}", uploadedFile.get().getName(), user.getLogin());
        });

        return "redirect:/home";

    }


}
