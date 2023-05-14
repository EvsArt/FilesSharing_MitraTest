package com.artevseev.filessharing_testmitra.api.service;

import com.artevseev.filessharing_testmitra.api.model.FileForApi;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;

import java.io.IOException;

public interface FileForApiService {

    String writeFile(FileForApi file, String userName);

    UploadedFile toUploadedFile(FileForApi file, User user);

    FileForApi getNewFileForApi(UploadedFile uploadedFile) throws IOException;

}
