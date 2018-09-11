package com.boostani.backend.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 
 * @author Loay
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String[] AUTH_WHITELIST = {
			"/v2/api-docs", "/swagger-resources/configuration/ui", "/swagger-resources",
			"/swagger-resources/configuration/security", "/swagger-ui.html", "/webjars/**", "/signin/**", "/signup/**",
			"/campain/**", "/account/**", "/affilate/**", "/category/**", "/fund/**" };

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated()
				.and().formLogin().loginPage("/login").permitAll().and().logout();
	}
}
