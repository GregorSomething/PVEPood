package com.pv.ostukorv.data;

import com.pv.ostukorv.model.ShopItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface ShopItemRepo extends CrudRepository<ShopItem, Integer> {

    Collection<ShopItem> findByCategoryAndType(String category, String type);
    Collection<ShopItem> findByType(String type);
    Collection<ShopItem> findByCategory(String category);

    @Query(value = "SELECT category FROM shop_item GROUP BY category;", nativeQuery = true)
    Collection<String> getCategory();

}
