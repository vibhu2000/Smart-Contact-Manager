package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity
public class MyConfig{

	@Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
	
	 @Bean
	    public UserDetailsService userDetailsService(){
//	        UserDetails normalUser = User
//	                .withUsername("vaibhav")
//	                .password(passwordEncoder().encode("vaibhav"))
//	                .roles("NORMAL")
//	                .build();
	//
//	        UserDetails adminUser = User
//	                .withUsername("vaibhav1")
//	                .password(passwordEncoder().encode("vaibhav"))
//	                .roles("ADMIN")
//	                .build();
	//
//	        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager(normalUser, adminUser);
//	        return inMemoryUserDetailsManager;

	        return new UserDetailServiceImpl();
	    }
	 
	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
	        httpSecurity.csrf().disable()
	                .authorizeHttpRequests()
	                .requestMatchers("/admin/**")
//	                .requestMatchers("/home/admin")
	                .hasRole("ADMIN")
	                .requestMatchers("/user/**")
	                .hasRole("USER")
	                .requestMatchers("/**")
	                .permitAll()
	                .anyRequest()
	                .authenticated()
	                .and()
	                .formLogin()
	                .loginPage("/signin")
	                .loginProcessingUrl("/dologin")
	                .defaultSuccessUrl("/user/dashboard");
//	                .failureUrl("/login-fail");       //if you need to create separate/new page for error page

	        return httpSecurity.build();
	    }
	
	
}
