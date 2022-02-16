package com.pv.ostukorv.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pv.ostukorv.data.ShopItemRepo;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class UserCartData extends UserCart {

    private Map<ShopItem, Integer> items = new HashMap<>();
    private double price;

    public UserCartData(UserCart userCart, ShopItemRepo shopItemRepo) throws NoSuchElementException {
        this.setDbItems(userCart.getDbItems());
        this.setUsername(userCart.getUsername());
        this.getDbItems().forEach((id, quantity) -> {
            ShopItem item = shopItemRepo.findById(id).orElseThrow();
            if (item != null) {
                this.items.put(item, quantity);
                this.price += item.getPrice() * quantity;
            } else {
                this.removeDbItems(id);
            }
        });
    }

    public double getPrice() {
        return price;
    }
    public Map<ShopItem, Integer> getItems() {
        return this.items;
    }

    @JsonComponent
    public static class UserCartDataSerializer extends JsonSerializer<UserCartData> {

        @Override
        public void serialize(UserCartData userCartData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("username", userCartData.getUsername());
            jsonGenerator.writeNumberField("price", userCartData.getPrice());
            jsonGenerator.writeArrayFieldStart("items");
            userCartData.getItems().forEach((item, integer) -> {
                try {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeObjectField("item", item);
                    jsonGenerator.writeNumberField("cartCount", integer);
                    jsonGenerator.writeEndObject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }
}
