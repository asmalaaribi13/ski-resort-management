package tn.esprit.spring.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class security extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/actuator/prometheus").permitAll()  // Allow Prometheus endpoint
                .anyRequest().authenticated()  // Secure other endpoints
                .and().httpBasic();  // Enable HTTP Basic authentication (if necessary)
    }
}
