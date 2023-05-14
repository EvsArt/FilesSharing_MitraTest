package com.artevseev.filessharing_testmitra.web.data.service;

import com.artevseev.filessharing_testmitra.api.model.FileForApi;
import com.artevseev.filessharing_testmitra.api.model.SmallFile;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface UploadedFileService {
    String writeFile(UploadedFile file) throws IOException;

    boolean deleteFile(String idFromLink, User user);

    FileForApi toFileForApi(UploadedFile file);

    SmallFile toSmallFile(UploadedFile file);

    void download(HttpServletResponse response, UploadedFile uploadedFile, User user);

}
