package com.artevseev.filessharing_testmitra.api.controller;

import com.artevseev.filessharing_testmitra.api.model.FileForApi;
import com.artevseev.filessharing_testmitra.api.model.SmallFile;
import com.artevseev.filessharing_testmitra.configuration.DataManager;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.UploadedFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/file", produces = "application/json")
@CrossOrigin(origins = "http://localhost:8080")
@Slf4j
// String id is id_link(from table)
public class FileControllerApi {

    @Value("${upload.path}")
    private String path;

    @Value("${host.name}")
    private String hostName;

    private final UploadedFileRepository uploadedFileRepository;
    private final DataManager dataManager;

    @Autowired
    public FileControllerApi(UploadedFileRepository uploadedFileRepository,
                             DataManager dataManager) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.dataManager = dataManager;
    }
    @GetMapping
    public Iterable<SmallFile> allFiles(@AuthenticationPrincipal User user) {
        log.info("Reading all files by API by {}", user.getLogin());
        return uploadedFileRepository.findAll().stream().map((x) -> x.toSmallFile(hostName)).toList();
    }

    @GetMapping("/{id}")
    public FileForApi getFile(@PathVariable String id){
        return uploadedFileRepository.findById(Long.parseLong(id.split("_")[0])).map((x) -> x.toFileForApi(path, hostName)).orElse(null);
    }


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public FileForApi saveFile(@ModelAttribute FileForApi file,
                               @AuthenticationPrincipal User user) {
        if(file != null)
            try {
                FileForApi result = dataManager.saveFile(file.toUploadedFile(user)).toFileForApi(path, hostName);
                log.info("The file {} was saved by {} by API", result.getFileName(), user.getLogin());
                return result;
            } catch (IOException e) {
                log.error("Error with saving file by API: {}", e.getMessage());
            }
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFile(@PathVariable("id") String id,
                           @AuthenticationPrincipal User user) {

        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(Long.parseLong(id.split("_")[0]));
        uploadedFile.ifPresent(file -> {
            if (dataManager.deleteFile(id, uploadedFile.get(), user))
                log.info("The file {} was deleted by {} by API", uploadedFile.get().getName(), user.getLogin());
        });

    }

}
