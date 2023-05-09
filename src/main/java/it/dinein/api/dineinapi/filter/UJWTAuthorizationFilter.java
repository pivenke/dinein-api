package it.dinein.api.dineinapi.filter;

import it.dinein.api.dineinapi.common.constant.Security;
import it.dinein.api.dineinapi.utility.UJWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// defining the JWT request auth filter
@Component
public class UJWTAuthorizationFilter extends OncePerRequestFilter {

    // declare JWT token provider instance
    private UJWTTokenProvider UJWTTokenProvider;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    // constructor
    public UJWTAuthorizationFilter(UJWTTokenProvider UJWTTokenProvider) {
        this.UJWTTokenProvider = UJWTTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Authorization");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        // checking req method from client request
        if (request.getMethod().equalsIgnoreCase(Security.OPTIONS_HTTP_METHOD)) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            String path = request.getRequestURI().substring(request.getContextPath().length());
            if (path.startsWith("/api/v1/user")) {
                String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authorizationHeader == null || !authorizationHeader.startsWith(Security.TOKEN_PREFIX)) {
                    filterChain.doFilter(request,response);
                    return;
                }
                String token = authorizationHeader.substring(Security.TOKEN_PREFIX.length());
                String username = UJWTTokenProvider.getSubject(token);
                if (UJWTTokenProvider.isTokenValid(username,token) && SecurityContextHolder.getContext().getAuthentication() == null){
                    List<GrantedAuthority> authorities = UJWTTokenProvider.getAuthorities(token);
                    Authentication authentication = UJWTTokenProvider.getAuthentication(username,authorities,request);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                else{
                    SecurityContextHolder.clearContext();
                }
            }
        }
        filterChain.doFilter(request,response);
    }

}
