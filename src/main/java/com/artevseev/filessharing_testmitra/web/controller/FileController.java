package com.artevseev.filessharing_testmitra.web.controller;

import com.artevseev.filessharing_testmitra.configuration.DataManager;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UploadedFileRepository;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    public FileController(UploadedFileRepository uploadedFileRepository, RoleRepository roleRepository, DataManager dataManager){
        this.uploadedFileRepository = uploadedFileRepository;
        this.roleRepository = roleRepository;
        this.dataManager = dataManager;
    }

    @GetMapping("/{id}")
    public String getFile(@PathVariable String id,
                          Model model,
                          @AuthenticationPrincipal User user){
        model.addAttribute("fileIsExist", false);
        long fileId = Integer.parseInt(id.split("_")[0]);
        int link = Integer.parseInt(id.split("_")[1]);
        Optional<UploadedFile> file = uploadedFileRepository.findById(fileId);
        if(file.isPresent() && file.get().getLink() == link){
            model.addAttribute("fileIsExist", true);
            model.addAttribute("file", file.get());
            model.addAttribute("isOwner", file.get().getUser().equals(user) ||
                    user.getRole().equals(roleRepository.findRoleByName("ADMIN")));
        }

        return "filePage";
    }

    @GetMapping("/{id}/download")
    @ResponseBody
    public void downloadFile(@PathVariable String id, HttpServletResponse response, @AuthenticationPrincipal User user){

        long fileId = Integer.parseInt(id.split("_")[0]);
        int link = Integer.parseInt(id.split("_")[1]);
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);
        if(uploadedFile.isPresent() && uploadedFile.get().getLink() == link){
            File file = new File(uploadPath + "/" +
                    uploadedFile.get().getUser().getLogin() + "/" +
                    uploadedFile.get().getName());

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
            response.setHeader(headerKey, headerValue);

            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(file);
                try {
                    int c;
                    while ((c = inputStream.read()) != -1) {
                        response.getWriter().write(c);
                    }
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    response.getWriter().close();
                }
                log.info("The file was downloaded {} by {}", file.getName(), user.getLogin());
            } catch (IOException e) {
                log.error("Error with downloading file {} by {}", file.getName(), user.getLogin());
                e.printStackTrace();
            }
        }
    }


    @GetMapping("/{id}/delete")
    public String deleteFile(@PathVariable("id") String id,
                             @AuthenticationPrincipal User user){

        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(Long.parseLong(id.split("_")[0]));
        uploadedFile.ifPresent(file -> {
            if(dataManager.deleteFile(id, file, user))
                log.info("The file {} was deleted by {}", uploadedFile.get().getName(), user.getLogin());
        });

        return "redirect:/home";

    }


}
