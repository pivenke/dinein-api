package it.dinein.api.dineinapi.configuration;

import it.dinein.api.dineinapi.common.constant.Security;
import it.dinein.api.dineinapi.filter.HJWTAuthorizationFilter;
import it.dinein.api.dineinapi.filter.JWTAccessDeniedHandler;
import it.dinein.api.dineinapi.filter.JWTAuthenticationEntryPoint;
import it.dinein.api.dineinapi.filter.UJWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// this class configured all the security measures
// customized JWT token filter and endPoint restrictions added
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private UJWTAuthorizationFilter ujwtAuthorizationFilter;
    private HJWTAuthorizationFilter hjwtAuthorizationFilter;
    private JWTAccessDeniedHandler jwtAccessDeniedHandler;
    private JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private UserDetailsService userDetailsService;
    private UserDetailsService hotelierDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public WebSecurityConfiguration(
            UJWTAuthorizationFilter ujwtAuthorizationFilter,
                                 HJWTAuthorizationFilter hjwtAuthorizationFilter,
                                 JWTAccessDeniedHandler jwtAccessDeniedHandler,
                                 JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                 @Qualifier("userDetailService")UserDetailsService userDetailsService,
                                 @Qualifier("hotelierDetailService")UserDetailsService hotelierDetailsService,
                                 BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.ujwtAuthorizationFilter = ujwtAuthorizationFilter;
        this.hjwtAuthorizationFilter = hjwtAuthorizationFilter;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.hotelierDetailsService = hotelierDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hotelierDetailsService).passwordEncoder(bCryptPasswordEncoder);
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests().antMatchers(Security.PUBLIC_URLS).permitAll()
                .antMatchers(HttpHeaders.ALLOW)
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(ujwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(hjwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
