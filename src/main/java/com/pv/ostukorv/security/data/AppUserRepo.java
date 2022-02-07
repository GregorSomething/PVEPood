package com.pv.ostukorv.security.data;

import com.pv.ostukorv.security.data.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepo extends CrudRepository<AppUser, String> {
}
