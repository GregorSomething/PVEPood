package com.pv.ostukorv.security.data;

import com.google.common.collect.Sets;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.pv.ostukorv.security.data.AppRoles.AppUserPermission.*;

public enum AppRoles {
    // Roles
    USER(Sets.newHashSet(CART_USE)),
    ADMIN(Sets.newHashSet(AppUserPermission.values()));

    private final Set<AppUserPermission> permissions;

    AppRoles(Set<AppUserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthority() {
        Set<SimpleGrantedAuthority> perms = this.permissions.stream().map(p ->
                new SimpleGrantedAuthority(p.getPermission())).collect(Collectors.toSet());
        perms.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return perms;
    }

    public enum AppUserPermission {

        // Permissions
        PROD_UPDATE("prod:update"),
        PROD_ADD("prod:add"),
        PROD_DELETE("prod:delete"),
        CART_USE("cart:use");

        private final String perm;
        AppUserPermission(String perm) {
            this.perm = perm;
        }

        public String getPermission() {
            return perm;
        }
    }
}
