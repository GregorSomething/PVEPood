package com.pv.ostukorv.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @RequestMapping(path="/login")
    public String getLoginView() {
        return "login.html";
    }

    @RequestMapping(path = "/register")
    public String getRegisterView() {
        return "register.html";
    }
}
