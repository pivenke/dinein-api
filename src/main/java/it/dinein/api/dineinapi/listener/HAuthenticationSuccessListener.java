package it.dinein.api.dineinapi.listener;

import it.dinein.api.dineinapi.model.HotelierPrincipal;
import it.dinein.api.dineinapi.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class HAuthenticationSuccessListener {
    private LoginAttemptService loginAttemptService;

    @Autowired
    public HAuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticateSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof HotelierPrincipal){
            HotelierPrincipal user = (HotelierPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
