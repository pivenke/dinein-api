package it.dinein.api.dineinapi.service;

import it.dinein.api.dineinapi.model.Hotelier;
import it.dinein.api.dineinapi.model.User;
import it.dinein.api.dineinapi.repository.HotelierRepository;
import it.dinein.api.dineinapi.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final HotelierRepository hotelierRepository;

    public CustomUserDetailsService(UserRepository userRepository, HotelierRepository hotelierRepository) {
        this.userRepository = userRepository;
        this.hotelierRepository = hotelierRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getUsername(),
                    user.getPassword(),
                    getAuthorities("USER"));
        }

        Hotelier hotelier = hotelierRepository.findHotelierByRestaurantName(username);
        if (hotelier != null) {
            return new org.springframework.security.core.userdetails.User(hotelier.getRestaurantName(),
                    hotelier.getPassword(),
                    getAuthorities("HOTELIER"));
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
    }
}

