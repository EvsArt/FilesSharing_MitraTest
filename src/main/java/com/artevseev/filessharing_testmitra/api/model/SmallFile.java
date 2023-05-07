package com.artevseev.filessharing_testmitra.api.model;

import com.artevseev.filessharing_testmitra.web.data.model.UploadedFile;
import lombok.Data;


@Data
public class SmallFile {

    private String fileName;
    private String link;

    public SmallFile(String hostName, UploadedFile uploadedFile) {

        fileName = uploadedFile.getName();
        link =  hostName + "/api/file/" + uploadedFile.getId() + "_" + uploadedFile.getLink();

    }

}
