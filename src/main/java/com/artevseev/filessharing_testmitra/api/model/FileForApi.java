package com.artevseev.filessharing_testmitra.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class FileForApi {

    String fileName;

    String link;    // Normal link to the file

    byte[] file;

    String owner;

    public FileForApi(String fileName, byte[] file) {
        this.fileName = fileName;
        this.file = file;
    }

}
