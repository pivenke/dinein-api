package it.dinein.api.dineinapi.common.enumeration;


import static it.dinein.api.dineinapi.common.constant.Authority.*;

public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HOTELIER(HOTELIER_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities){
        this.authorities = authorities;
    }

    public String[] getAuthorities(){
        return authorities;
    }
}
