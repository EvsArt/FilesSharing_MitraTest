package com.artevseev.filessharing_testmitra.web.data.model;

import com.artevseev.filessharing_testmitra.api.model.FileForApi;
import com.artevseev.filessharing_testmitra.api.model.SmallFile;
import com.artevseev.filessharing_testmitra.web.data.repository.RoleRepository;
import com.artevseev.filessharing_testmitra.web.data.repository.UploadedFileRepository;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.*;
import java.util.Date;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@Slf4j
public class UploadedFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public UploadedFile(MultipartFile file, User user){
        this.name = file.getOriginalFilename();
        this.size = file.getSize();
        this.user = user;
        this.creationDate = new Date();
        this.file = file;
        this.link = this.hashCode();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private long size;

    private int link;

    @ManyToOne
    User user;

    private Date creationDate;

    @Transient
    private MultipartFile file;
    @Transient
    private byte[] byteFile;

    public String writeFile(String path, UploadedFileRepository uploadedFileRepository) throws IOException {

        String fileName = (name == null) ? file.getName() : name;
        int count = 1;
        String tmpName = fileName;
        while (uploadedFileRepository.existsByName(tmpName)){
            tmpName = "(" + count++ + ")" + fileName;
        }
        setName(tmpName);

        try {
            byte[] bytes;
            if(file != null)
                bytes = file.getBytes();
            else
                bytes = byteFile;

            File uploadPath = new File(path + "/" + user.getLogin());
            uploadPath.mkdir();
            uploadPath = new File(uploadPath, name);
            uploadPath.createNewFile();

            BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(uploadPath));


            stream.write(bytes);
            stream.close();
            return uploadPath.getName();


        }catch (Exception e){
            log.error("There was an error with uploading file: {}", e.getMessage());
            throw e;
        }

    }

    public boolean deleteFile(String uploadPath, String idFromLink, User user, UploadedFileRepository uploadedFileRepository, RoleRepository roleRepository){

        boolean flag = false;
        long fileId = Integer.parseInt(idFromLink.split("_")[0]);
        int link = Integer.parseInt(idFromLink.split("_")[1]);
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);  // Получение запрашиваемого файла
        if(uploadedFile.isPresent() && uploadedFile.get().getLink() == link &&
                (uploadedFile.get().getUser().equals(user) || user.getRole().equals(roleRepository.findRoleByName("ADMIN")))) {

            File file = new File(uploadPath + "/" +
                    uploadedFile.get().getUser().getLogin() + "/" +
                    name);

            uploadedFileRepository.deleteById(id);
            flag = file.delete();

        }else if(uploadedFile.isPresent()){
            log.warn("There was attempt with deleting file {} by {}", uploadedFile.get().getName(), user.getLogin());
        }else{
            log.warn("There was attempt with deleting file with id {} by {}", fileId, user.getLogin());
        }
        return flag;



    }

    public FileForApi toFileForApi(String path, String hostName) {
        try {
            return new FileForApi(path, hostName, this);
        } catch (IOException e) {
            log.error("Error with reading file!");
            return null;
        }
    }

    public SmallFile toSmallFile(String hostName){
        return new SmallFile(hostName, this);
    }

}
