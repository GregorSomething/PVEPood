package com.pv.ostukorv.api;

import com.pv.ostukorv.data.ShopItemRepo;
import com.pv.ostukorv.data.UserCartRepo;
import com.pv.ostukorv.model.ShopItem;
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
import java.util.Optional;

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
            if (quantity <= 0) {
                cart.removeDbItems(id);
            } else {
                cart.setDbItem(id, quantity);
            }
            userCartRepo.save(cart);
        } else {
            UserCart cart = new UserCart();
            cart.setUsername(username);
            cart.setDbItems(new HashMap<>());
            if (quantity <= 0) {
                cart.removeDbItems(id);
            } else {
                cart.setDbItem(id, quantity);
            }
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

    @GetMapping("/pay")
    @PreAuthorize("hasAuthority('cart:use')")
    public void pay() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        UserCartData data;
        if (userCartRepo.existsById(username)) {
            UserCart cart = userCartRepo.findById(username).orElseThrow();
            data = new UserCartData(cart, itemRepo);
        } else {
            UserCart cart = new UserCart();
            cart.setUsername(username);
            cart.setDbItems(new HashMap<>());
            data = new UserCartData(cart, itemRepo);
        }
        data.getDbItems().forEach((id, count) -> {
            Optional<ShopItem> item = itemRepo.findById(id);
            if (item.isPresent()) {
                ShopItem item1 = item.get();
                item1.setCount(item1.getCount() - count);
                if (item1.getCount() <= 0) {
                    throw new RuntimeException("Soovitud ese on otsas! " + item1.getName());
                }
                itemRepo.save(item1);
            }
        });
    }
}
