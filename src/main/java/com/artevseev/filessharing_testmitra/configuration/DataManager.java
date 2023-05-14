package com.artevseev.filessharing_testmitra.configuration;

import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UploadedFileRepository;
import com.artevseev.filessharing_testmitra.web.data.service.UploadedFileService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class DataManager {

    @Value("${upload.path}")
    private String path;

    private final UploadedFileRepository uploadedFileRepository;
    private final RoleRepository roleRepository;
    private final UploadedFileService uploadedFileService;

    public DataManager(UploadedFileRepository uploadedFileRepository,
                       RoleRepository roleRepository, UploadedFileService uploadedFileService) {
        this.roleRepository = roleRepository;
        this.uploadedFileRepository = uploadedFileRepository;
        this.uploadedFileService = uploadedFileService;
    }

    @Transactional
    public UploadedFile saveFile(UploadedFile file) throws IOException {
        uploadedFileService.writeFile(file);
        return uploadedFileRepository.save(file);
    }

    @Transactional
    public boolean deleteFile(String idFromLink, User user) {
        return uploadedFileService.deleteFile(idFromLink, user);
    }


}
