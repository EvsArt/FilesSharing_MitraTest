package com.artevseev.filessharing_testmitra.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/login")
public class LoginController {

    @ModelAttribute("error")
    public boolean wrongPassword(){
        return false;
    }

    @GetMapping
    public String loginPage(Model model, @RequestParam Map<String, String> params) {
        if(params.containsKey("error")){
            model.addAttribute("error", true);
        }
        return "loginPage";
    }

}
