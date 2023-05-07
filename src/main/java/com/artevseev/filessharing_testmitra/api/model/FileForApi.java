package com.artevseev.filessharing_testmitra.api.model;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.dialect.SybaseDialect;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Date;

@Data
@Slf4j
@NoArgsConstructor
public class FileForApi {

    String fileName;

    String link;

    byte[] file;

    String owner;

    public FileForApi(String fileName, byte[] file){
        this.fileName = fileName;
        this.file = file;
    }

    public FileForApi(String path, String hostName, UploadedFile uploadedFile) throws IOException {

        fileName = uploadedFile.getName();
        link = hostName + "/api/file/" + uploadedFile.getId() + "_" + uploadedFile.getLink();
        owner = uploadedFile.getUser().getLogin();

        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(path + "/" + uploadedFile.getUser().getLogin() + "/" + fileName))){
            file = stream.readAllBytes();
        } catch (FileNotFoundException e) {
            fileName = "Файл не найден!";
            file = null;
        } catch (IOException e) {
            fileName = "Произошла ошибка при чтении файла!";
            file = null;
            throw new IOException();
        }
    }

    public String writeFile(String path, String userName){
        try {
            byte[] bytes = file;

            File uploadPath = new File(path + "/" + userName);
            uploadPath.mkdir();
            uploadPath = new File(uploadPath, fileName);
            uploadPath.createNewFile();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(uploadPath));


            stream.write(bytes);
            stream.close();
            return uploadPath.getName();


        }catch (Exception e){
            e.printStackTrace();
            log.error("There was an error with uploading file: {}", e.getMessage());
            return null;
        }
    }

    public UploadedFile toUploadedFile(User user){

        UploadedFile result = new UploadedFile();
        result.setName(fileName);
        result.setSize(file.length);
        result.setUser(user);
        result.setByteFile(file);
        result.setCreationDate(new Date());
        result.setLink(result.hashCode());
        return result;

    }

}
