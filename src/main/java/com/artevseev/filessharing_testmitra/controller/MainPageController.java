package com.artevseev.filessharing_testmitra.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

@Controller
@RequestMapping("/home")
public class MainPageController {

    @Value("${upload.path}")
    private String path;

    @GetMapping
    public String getPage(){
        return "mainPage";
    }

    @PostMapping("/upload")
    public @ResponseBody String uploadFile(@RequestParam("file")MultipartFile file){

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(path + file.getOriginalFilename()));
                // TODO(Добавить создание папки пользователя)
                System.out.println(file.getOriginalFilename());
                stream.write(bytes);
                stream.close();
                // TODO(Запись в БД) (Следующий шаг)
                // TODO(Проверка на существование файла)
                return file.getOriginalFilename() + " успешно загружен!";
            } catch (Exception e) {
                // TODO(Добавить лог)
                e.printStackTrace();
                return "Внутренняя ошибка сервера! Извините, она скоро будет устранена(";
            }
        } else {
            return "Не удалось загрузить " + file.getOriginalFilename() + " потому что файл пустой.";
        }

    }

}
