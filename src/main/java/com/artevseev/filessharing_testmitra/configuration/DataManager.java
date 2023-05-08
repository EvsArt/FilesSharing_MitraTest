package com.artevseev.filessharing_testmitra.configuration;

import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UploadedFileRepository;
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

    public DataManager(UploadedFileRepository uploadedFileRepository,
                       RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.uploadedFileRepository = uploadedFileRepository;
    }

    @Transactional
    public UploadedFile saveFile(UploadedFile file) throws IOException {
        file.writeFile(path, uploadedFileRepository);
        return uploadedFileRepository.save(file);
    }

    @Transactional
    public boolean deleteFile(String idFromLink, UploadedFile file, User user){
        return file.deleteFile(path, idFromLink, user, uploadedFileRepository, roleRepository);
    }


}
