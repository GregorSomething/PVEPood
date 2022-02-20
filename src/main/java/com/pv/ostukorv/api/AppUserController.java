package com.pv.ostukorv.api;

import com.pv.ostukorv.security.data.AppRoles;
import com.pv.ostukorv.security.data.AppUser;
import com.pv.ostukorv.security.data.AppUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api/user")
public class AppUserController {

    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
    PasswordEncoder passwordEncoder;


    @GetMapping("/get")
    @PreAuthorize("hasAuthority('cart:use')")
    public @ResponseBody AppUser getUser() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        AppUser appUser = appUserRepo.findById(username).orElseThrow();
        appUser.setAuthorities(null);
        appUser.setClientPass(null);
        return appUser;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void registerUser(@RequestBody MultiValueMap<String, String> formData) {
        // All fields are mandatory
        if (formData.getFirst("username") == null || formData.getFirst("password") == null ||
                formData.getFirst("displayName") == null || formData.getFirst("imageUrl") == null)
            throw new AuthenticationServiceException("Registration data is not valid");

        // Regex checks and enforce minimum and maximum lengths
        if (Pattern.compile("[a-zA-Z0-9_äöõüÄÖÕÜ]{5,100}$").matcher(formData.getFirst("username")).matches()
                && Pattern.compile("^[a-zA-Z0-9_'!\"#%&/()=+\\-*äöõüÄÖÕÜ]{7,100}$").matcher(formData.getFirst("password")).matches()
                && Pattern.compile("^[a-zA-Z0-9_ äöõüÄÖÕÜ]{1,100}$").matcher(formData.getFirst("displayName")).matches()) {

            // Avoid accounts with duplicate username. displayName-s can repeat.
            if (appUserRepo.existsById(formData.getFirst("username"))) {
                throw new AuthenticationServiceException("User with that name already exists");
            }

            ArrayList<AppRoles> roles = new ArrayList<>();

            // Allow one account named 'admin' to have ADMIN rights
            if (formData.getFirst("username").equals("admin")) roles.add(AppRoles.ADMIN);
            // All other accounts have USER rights
            else roles.add(AppRoles.USER);

            // Create & add the account
            AppUser appUser = new AppUser(roles, formData.getFirst("username"),
                    passwordEncoder.encode(formData.getFirst("password")),
                    formData.getFirst("imageUrl"), formData.getFirst("displayName"));
            appUserRepo.save(appUser);
        } else {
            throw new AuthenticationServiceException("Registration data is not valid");
        }
    }

}
