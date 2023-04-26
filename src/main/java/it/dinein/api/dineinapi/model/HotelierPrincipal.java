package it.dinein.api.dineinapi.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

// this class is to match our user with spring builtin security user
public class HotelierPrincipal implements UserDetails {
    // declare domain user instance
    private Hotelier hotelier;

    public HotelierPrincipal(Hotelier hotelier) {
        this.hotelier = hotelier;
    }

    // matching each user authority with GrantedAuthority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(this.hotelier.getAuthorities()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.hotelier.getPassword();
    }

    @Override
    public String getUsername() {
        return this.hotelier.getRestaurantName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.hotelier.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.hotelier.isActive();
    }
}

