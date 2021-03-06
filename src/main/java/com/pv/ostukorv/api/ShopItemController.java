package com.pv.ostukorv.api;

import com.pv.ostukorv.data.ShopItemRepo;
import com.pv.ostukorv.model.ShopItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/shop")
public class ShopItemController {

    @Autowired
    private ShopItemRepo shopItemRepo;

    @GetMapping(path="/test") // TODO: This is a test thingi
    @PreAuthorize("hasAuthority('prod:add')")
    public @ResponseBody ShopItem test() {
        ShopItem item = new ShopItem();
        item.setDescription("Test");
        item.setName("Sai");
        item.setCount(1);
        item.setCategory("toit");
        item.setType("leib");
        item.setPrice(7.66);
        shopItemRepo.save(item);
        return item;
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<ShopItem> getShopItems() {
        return shopItemRepo.findAll();
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('prod:add')")
    public void addShopItem(@RequestBody ShopItem item) {
        // Auto-increment product ID
        item.setId(Math.toIntExact(shopItemRepo.count())+1);

        // Regex checks & enforce minimum values and lengths
        if (!(Pattern.compile("[a-zäöõü]+").matcher(item.getCategory()).matches()
                && Pattern.compile("[a-zA-ZäöõüÄÖÕÜ ]+").matcher(item.getName()).matches()
                && item.getName().length() > 0
                && item.getCount() >= 0
                && item.getPrice() > 0)) throw new RuntimeException("Name/Category/Type can not be empty");

        item.setType(item.getName().toLowerCase(Locale.ROOT).replace(" ", ""));
        item.setName(item.getName().trim());
        item.setDescription(item.getDescription().trim());
        item.setImgUrll(item.getImgUrl().trim());

        // Store new product
        shopItemRepo.save(item);
    }

    @PostMapping(path = "/update")
    @PreAuthorize("hasAuthority('prod:update')")
    public void updateShopItem(@RequestBody ShopItem item) {
        if (!(Pattern.compile("[a-zäöõü]+").matcher(item.getCategory()).matches()
                && Pattern.compile("[a-zA-ZäöõüÄÖÕÜ ]+").matcher(item.getName()).matches()
                && item.getName().length() > 0
                && item.getCount() >= 0
                && item.getPrice() > 0)) throw new RuntimeException("Name/Category/Type can not be empty");

        item.setType(item.getName().toLowerCase(Locale.ROOT).replace(" ", ""));
        item.setName(item.getName().trim());
        item.setDescription(item.getDescription().trim());
        item.setImgUrll(item.getImgUrl().trim());

        // Store changed product
        shopItemRepo.save(item);
    }

    @DeleteMapping(path = "/delete")
    @PreAuthorize("hasAuthority('prod:delete')")
    public void deleteShopItem(@RequestParam(name = "id") int id) {
        if (id == 0) return;
        shopItemRepo.deleteById(id);
    }

    @GetMapping(path = "/list")
    public @ResponseBody Iterable<ShopItem> getShopItemsFilterd (
            @RequestParam(name = "c", required = false, defaultValue = "") String category,
            @RequestParam(name = "t", required = false, defaultValue = "") String type,
            @RequestParam(name = "n", required = false, defaultValue = "") String name) {
        String[] cat = category.split(" ");
        String[] typ = type.split(" ");
        final String namef = name.toLowerCase(Locale.ROOT);
        ArrayList<ShopItem> items = new ArrayList<ShopItem>();
        if (!cat[0].isEmpty())
        for (String c : cat) {
            if (!typ[0].isEmpty()) {
                for (String t : typ) {
                    items.addAll(shopItemRepo.findByCategoryAndType(c, t));
                }
            } else {
                items.addAll(shopItemRepo.findByCategory(c));
            }
        } else if (!typ[0].isEmpty()) {
            for (String t : typ) {
                items.addAll(shopItemRepo.findByType(t));
            }
        } else {
            shopItemRepo.findAll().forEach(items::add);
        }
        return items.stream().filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(namef)).collect(Collectors.toSet());
    }

    @GetMapping("/categories")
    public Collection<String> getCategories() {
        return shopItemRepo.getCategory();
    }
}
