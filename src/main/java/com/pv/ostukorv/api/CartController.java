package com.pv.ostukorv.api;

import com.pv.ostukorv.data.ShopItemRepo;
import com.pv.ostukorv.data.UserCartRepo;
import com.pv.ostukorv.model.UserCart;
import com.pv.ostukorv.model.UserCartData;
import com.pv.ostukorv.security.data.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(path = "/api/cart")
public class CartController {

    @Autowired
    private UserCartRepo userCartRepo;
    @Autowired
    private ShopItemRepo itemRepo;

    @PostMapping(path = "/add/{id}/{quantity}")
    @PreAuthorize("hasAuthority('cart:use')")
    public void addToCart(@PathVariable(name = "id") int id,
                          @PathVariable(name = "quantity") int quantity) {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        if (userCartRepo.existsById(username)) {
            UserCart cart = userCartRepo.findById(username).orElseThrow();
            cart.addDbItem(id, quantity);
            userCartRepo.save(cart);
        } else {
            UserCart cart = new UserCart();
            cart.setUsername(username);
            cart.setDbItems(new HashMap<>());
            cart.setDbItem(id, quantity);
            userCartRepo.save(cart);
        }
    }

    @GetMapping("/get")
    @PreAuthorize("hasAuthority('cart:use')")
    public @ResponseBody UserCartData getCart() {
        AppUser user;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof AppUser) {
            user = (AppUser)principal;
        } else {
            throw new BadCredentialsException("Access denied");
        }
        if (userCartRepo.existsById(user.getUsername())) {
            UserCart cart = userCartRepo.findById(user.getUsername()).orElseThrow();
            UserCartData data = new UserCartData(cart, itemRepo);
            return data;
        } else {
            UserCart cart = new UserCart();
            cart.setUsername(user.getUsername());
            cart.setDbItems(new HashMap<>());
            UserCartData data = new UserCartData(cart, itemRepo);
            return data;
        }
    }
}
