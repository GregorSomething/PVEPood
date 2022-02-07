package com.pv.ostukorv.model;

import javax.persistence.*;
import java.util.Map;

@Entity
public class UserCart {
    @Id
    private String username;
    @ElementCollection
    @MapKeyColumn(name="itemId")
    @Column(name="quantity")
    @CollectionTable(name="db_items", joinColumns=@JoinColumn(name="username"))
    private Map<Integer, Integer> dbItems; // Id, quantity

    public Map<Integer, Integer> getDbItems() {
        return dbItems;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDbItems(Map<Integer, Integer> dbItems) {
        this.dbItems = dbItems;
    }
    public void addDbItem(int id, int quantity) {
        if (this.dbItems.containsKey(id)) {
            this.dbItems.replace(id, this.dbItems.get(id) + quantity);
        } else {
            this.dbItems.put(id, quantity);
        }
    }
    public void removeDbItems(int id) {
        this.dbItems.remove(id);
    }
    public void setDbItem(int id, int quantity) {
        if (this.dbItems.containsKey(id)) {
            this.dbItems.replace(id, quantity);
        } else {
            this.dbItems.put(id, quantity);
        }
    }
}
