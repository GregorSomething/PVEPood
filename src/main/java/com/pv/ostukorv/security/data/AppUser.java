package com.pv.ostukorv.security.data;

import com.pv.ostukorv.security.data.AppRoles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
public class AppUser implements UserDetails {

    @Transient
    private Set<GrantedAuthority> grantedAuthorities;
    @Id
    private String username;
    private String clientPass;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    @Transient
    private ArrayList<AppRoles> roles;
    private ArrayList<String> sRoles;
    private String imageUrl;
    private String displayName;

    public AppUser(ArrayList<AppRoles> roles, String username,
                   String clientPass, String imageUrl, String displayName) {
        this.roles = roles;
        this.username = username;
        this.clientPass = clientPass;
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
        this.imageUrl = imageUrl;
        this.displayName = displayName;
        this.grantedAuthorities = new HashSet<>();
        this.sRoles = new ArrayList<>();
        for (AppRoles role : roles) {
            this.sRoles.add(role.name());
            this.grantedAuthorities.addAll(role.getGrantedAuthority());
        }
    }

    public AppUser() {}

    @Transient @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }
    @Transient @Override public String getPassword() {
        return clientPass;
    }
    @Override public String getUsername() {
        return username;
    }
    @Override public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }
    @Override public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }
    @Override public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }
    @Override public boolean isEnabled() {
        return isEnabled;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    @Transient public ArrayList<AppRoles> getRoles() {
        return roles;
    }
    public ArrayList<String> getsRoles() {
        return sRoles;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setClientPass(String clientPass) {
        this.clientPass = clientPass;
    }
    public void setAuthorities(Set<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }
    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }
    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
    public void setRoles(ArrayList<AppRoles> roles) {
        this.roles = roles;
        this.grantedAuthorities.clear();
        this.sRoles.clear();
        for (AppRoles role : roles) {
            this.sRoles.add(role.name());
            this.grantedAuthorities.addAll(role.getGrantedAuthority());
        }
    }
    public void setsRoles(ArrayList<String> sRoles) {
        this.sRoles = sRoles;
        this.roles.clear();
        this.grantedAuthorities.clear();
        for (String s : sRoles) {
            roles.add(AppRoles.valueOf(s));
        }
        for (AppRoles role : roles) {
            this.grantedAuthorities.addAll(role.getGrantedAuthority());
        }
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @PostLoad
    public void init() {
        this.roles = new ArrayList<>();
        this.grantedAuthorities = new HashSet<>();
        for (String s : sRoles) {
            roles.add(AppRoles.valueOf(s));
        }
        for (AppRoles role : roles) {
            this.grantedAuthorities.addAll(role.getGrantedAuthority());
        }
    }
}
