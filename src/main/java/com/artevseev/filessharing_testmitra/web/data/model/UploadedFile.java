package com.artevseev.filessharing_testmitra.web.data.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Slf4j
public class UploadedFile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public UploadedFile(MultipartFile file, User user) {
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

}
