package com.artevseev.filessharing_testmitra.api.service;

import com.artevseev.filessharing_testmitra.api.model.FileForApi;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;

@Service
@Slf4j
public class FileForApiServiceImpl implements FileForApiService {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${host.name}")
    private String hostName;

    public String writeFile(FileForApi file, String userName) {
        try {
            byte[] bytes = file.getFile();

            File uploadPathFile = new File(uploadPath + "/" + userName);
            uploadPathFile.mkdir();
            uploadPathFile = new File(uploadPathFile, file.getFileName());
            uploadPathFile.createNewFile();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(uploadPathFile));


            stream.write(bytes);
            stream.close();
            return uploadPathFile.getName();


        } catch (Exception e) {
            e.printStackTrace();
            log.error("There was an error with uploading file: {}", e.getMessage());
            return null;
        }
    }

    public UploadedFile toUploadedFile(FileForApi file, User user) {

        UploadedFile result = new UploadedFile();
        result.setName(file.getFileName());
        result.setSize(file.getFile().length);
        result.setUser(user);
        result.setByteFile(file.getFile());
        result.setCreationDate(new Date());
        result.setLink(result.hashCode());
        return result;

    }

    public FileForApi getNewFileForApi(UploadedFile uploadedFile) throws IOException {
        FileForApi fileForApi = new FileForApi();
        fileForApi.setFileName(uploadedFile.getName());
        fileForApi.setLink(hostName + "/api/file/" + uploadedFile.getId() + "_" + uploadedFile.getLink());
        fileForApi.setOwner(uploadedFile.getUser().getLogin());

        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(uploadPath + "/" + uploadedFile.getUser().getLogin() + "/" + fileForApi.getFileName()))) {
            fileForApi.setFile(stream.readAllBytes());
        } catch (FileNotFoundException e) {
            fileForApi.setFileName("Файл не найден!");
            fileForApi.setFile(null);
        } catch (IOException e) {
            fileForApi.setFileName("Произошла ошибка при чтении файла!");
            fileForApi.setFile(null);
            throw new IOException();
        }

        return fileForApi;
    }


}
