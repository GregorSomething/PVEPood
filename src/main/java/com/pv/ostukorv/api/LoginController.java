package com.pv.ostukorv.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class LoginController {

    @GetMapping("/login")
    public String getLoginView() {
        return "login";
    }

    //@RequestMapping(path = "/register", method = RequestMethod.GET, produces = "text/html")
    public String getRegisterView() {
        return "register";
    }
}
