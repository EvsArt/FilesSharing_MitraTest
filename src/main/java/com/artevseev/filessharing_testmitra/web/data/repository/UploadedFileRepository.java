package com.artevseev.filessharing_testmitra.web.data.repository;

import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends CrudRepository<UploadedFile, Long> {
    UploadedFile findByName(String name);

    List<UploadedFile> findAllByUser(User user);

    List<UploadedFile> findAllByOrderByName();

    boolean existsByName(String name);

    List<UploadedFile> findAll();
}
