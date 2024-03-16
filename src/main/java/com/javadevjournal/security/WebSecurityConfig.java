package com.javadevjournal.security;

import com.javadevjournal.exceptions.ExceptionAPIHandler;
import com.javadevjournal.exceptions.NoAuthorityException;
import com.javadevjournal.jpa.enums.RoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.jaas.AbstractJaasAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private AbstractJaasAuthenticationProvider jaasAuthenticationProvider;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    @Qualifier("delegatedAuthenticationEntryPoint")
    AuthenticationEntryPoint authEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jaasAuthenticationProvider);
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    public void configure(final WebSecurity webSecurity) {
        webSecurity.ignoring().antMatchers("/token/**");
        webSecurity.ignoring().antMatchers("/registration/**");
        webSecurity.ignoring().antMatchers("/auth/**");
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource()).and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    authException.getMessage()
            );
        }).and();
        http.authorizeRequests()
                //.antMatchers(HttpMethod.GET, "blse-1/api").permitAll()
                .antMatchers(HttpMethod.POST, "blse-1/auth/*").permitAll()
                .antMatchers(HttpMethod.GET, "blse-1/all").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/*").hasRole(RoleName.USER.name())
                .antMatchers(HttpMethod.GET, "/api/users/*").hasRole(RoleName.USER.name())
                .antMatchers("/api/ads/*").hasRole(RoleName.USER.name())
                .antMatchers(HttpMethod.POST, "/api/*/create").hasRole(RoleName.USER.name())
                .antMatchers(HttpMethod.GET, "/api/*/my").hasRole(RoleName.USER.name())

                .antMatchers(HttpMethod.GET, "/api/offers/offer/*").hasAnyRole(RoleName.USER.name(), RoleName.ADMIN.name())
                .antMatchers("/api/offers/admin/**").hasRole(RoleName.ADMIN.name())

                .anyRequest().authenticated();
        http.headers().frameOptions().sameOrigin();
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                //.exceptionHandling()
                //.authenticationEntryPoint(authEntryPoint);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.setAlwaysUseFullPath(true);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}