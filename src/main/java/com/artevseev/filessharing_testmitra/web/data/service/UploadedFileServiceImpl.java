package com.artevseev.filessharing_testmitra.web.data.service;

import com.artevseev.filessharing_testmitra.api.model.FileForApi;
import com.artevseev.filessharing_testmitra.api.model.SmallFile;
import com.artevseev.filessharing_testmitra.api.service.FileForApiService;
import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import com.artevseev.filessharing_testmitra.web.data.model.User;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UploadedFileRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

@Service
@Slf4j
public class UploadedFileServiceImpl implements UploadedFileService {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${host.name}")
    private String hostName;

    private final UploadedFileRepository uploadedFileRepository;
    private final RoleRepository roleRepository;
    private final FileForApiService fileForApiService;

    public UploadedFileServiceImpl(UploadedFileRepository uploadedFileRepository,
                                   RoleRepository roleRepository,
                                   FileForApiService fileForApiService) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.roleRepository = roleRepository;
        this.fileForApiService = fileForApiService;
    }

    public String writeFile(UploadedFile file) throws IOException {

        String fileName = (file.getName() == null) ? file.getFile().getName() : file.getName();
        int count = 1;
        String tmpName = fileName;
        while (uploadedFileRepository.existsByName(tmpName)) {
            tmpName = "(" + count++ + ")" + fileName;
        }
        file.setName(tmpName);

        try {
            byte[] bytes;
            if (file.getFile() != null)
                bytes = file.getFile().getBytes();
            else
                bytes = file.getByteFile();

            File uploadPathDir = new File(uploadPath + "/" + file.getUser().getLogin());
            uploadPathDir.mkdir();
            uploadPathDir = new File(uploadPathDir, file.getName());
            uploadPathDir.createNewFile();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(uploadPathDir));


            stream.write(bytes);
            stream.close();
            return uploadPathDir.getName();


        } catch (Exception e) {
            log.error("There was an error with uploading file: {}", e.getMessage());
            throw e;
        }

    }

    public boolean deleteFile(String idFromLink, User user) {

        boolean flag = false;
        long fileId = Integer.parseInt(idFromLink.split("_")[0]);
        int link = Integer.parseInt(idFromLink.split("_")[1]);
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);  // Получение запрашиваемого файла
        if (uploadedFile.isPresent() && uploadedFile.get().getLink() == link &&
                (uploadedFile.get().getUser().equals(user) || user.getRole().equals(roleRepository.findRoleByName("ADMIN")))) {

            File file = new File(uploadPath + "/" +
                    uploadedFile.get().getUser().getLogin() + "/" +
                    uploadedFile.get().getName());

            uploadedFileRepository.deleteById(fileId);
            flag = file.delete();

        } else if (uploadedFile.isPresent()) {
            log.warn("There was attempt with deleting file {} by {}", uploadedFile.get().getName(), user.getLogin());
        } else {
            log.warn("There was attempt with deleting file with id {} by {}", fileId, user.getLogin());
        }
        return flag;

    }

    public FileForApi toFileForApi(UploadedFile file) {
        try {
            return fileForApiService.getNewFileForApi(file);
        } catch (IOException e) {
            log.error("Error with reading file!");
            return null;
        }
    }

    public SmallFile toSmallFile(UploadedFile file) {
        return new SmallFile(hostName, file);
    }

    public void download(HttpServletResponse response, UploadedFile uploadedFile, User user) {
        File file = new File(uploadPath + "/" +
                uploadedFile.getUser().getLogin() + "/" +
                uploadedFile.getName());

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
            log.info("The file was downloaded {} by {}", file.getName(), (user != null) ? user.getLogin() : "unauthorizedUser");
        } catch (IOException e) {
            log.error("Error with downloading file {} by {}", file.getName(), (user != null) ? user.getLogin() : "unauthorizedUser");
            e.printStackTrace();
        }
    }

}
