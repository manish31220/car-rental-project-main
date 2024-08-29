package com.example.carrentalproject.security;

import com.example.carrentalproject.filter.CustomAuthenticationFilter;
import com.example.carrentalproject.filter.CustomAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

        private final UserDetailsService userDetailsService;
        private final PasswordEncoder passwordEncoder;
        private final long expirationTime;
        private final String secretKey;

        public SecurityConfig(UserDetailsService userDetailsService,
                              PasswordEncoder passwordEncoder,
                              @Value("${jwt.expirationTime}") long expirationTime,
                              @Value("${jwt.secretKey}") String secretKey) {
                this.userDetailsService = userDetailsService;
                this.passwordEncoder = passwordEncoder;
                this.expirationTime = expirationTime;
                this.secretKey = secretKey;
        }

        private static final String[] AUTH_WHITELIST = {
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**",
                "/v2/api-docs",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui/index.html"
        };

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
                http.csrf().disable();
                http.sessionManagement().sessionCreationPolicy(STATELESS);

                // Allow unrestricted access to Swagger endpoints
                http.authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll();

                // Define other protected endpoints
                http.authorizeRequests()
                        .antMatchers("/login").permitAll()
                        .antMatchers(HttpMethod.DELETE, "/users/**").hasAnyAuthority("ROLE_ADMIN")
                        .antMatchers(HttpMethod.PUT, "/users/**").hasAnyAuthority("ROLE_ADMIN")
                        .antMatchers(HttpMethod.POST, "/users/**").hasAnyAuthority("ROLE_MANAGER")
                        .antMatchers(HttpMethod.GET, "/users/**").hasAnyAuthority("ROLE_MANAGER")
                        .antMatchers(HttpMethod.DELETE, "/cars/**").hasAnyAuthority("ROLE_ADMIN")
                        .antMatchers(HttpMethod.PUT, "/cars/**").hasAnyAuthority("ROLE_ADMIN")
                        .antMatchers(HttpMethod.POST, "/cars/**").hasAnyAuthority("ROLE_MANAGER")
                        .antMatchers(HttpMethod.GET, "/cars/**").hasAnyAuthority("ROLE_USER")
                        .antMatchers(HttpMethod.GET, "/orders").hasAnyAuthority("ROLE_MANAGER")
                        .antMatchers(HttpMethod.POST, "/orders").hasAnyAuthority("ROLE_USER")
                        .antMatchers("/payment/**", "/delivery/**").hasAnyAuthority("ROLE_USER")
                        .antMatchers("/registration/**").permitAll()
                        .anyRequest().authenticated()
                        .and()
                        .logout().logoutSuccessUrl("/login");

                // Add custom filters
                http.addFilterBefore(new CustomAuthenticationFilter(authenticationManagerBean(), expirationTime, secretKey), UsernamePasswordAuthenticationFilter.class);
                http.addFilterBefore(new CustomAuthorizationFilter(secretKey), UsernamePasswordAuthenticationFilter.class);
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
                return super.authenticationManagerBean();
        }
}
