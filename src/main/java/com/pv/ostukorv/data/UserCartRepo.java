package com.pv.ostukorv.data;

import com.pv.ostukorv.model.UserCart;
import org.springframework.data.repository.CrudRepository;

public interface UserCartRepo extends CrudRepository<UserCart, String> {
}
