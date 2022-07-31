package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    @Override //권한에 관련된 메서드
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        //http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/users")
                        .hasIpAddress("127.0.0.1")
                        .and()
                        .addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(
                authenticationManager(), userService, env);

        return authenticationFilter;
    }

    @Override //인증에 관련된 메서드
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /* 인증 과정
         * 1. 클라이언트로부터 username(email), pwd를 받는다.
         * 2. username(email)을 가지고 users 테이블로부터 암호화된 pwd를 가져온다
         *    (select pwd from users where email = ?)
         * 3. 클라이언트로부터 받은 pwd를 암호화 한 다음에, 조회한 암호화된 pwd와 비교한다
         */

        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
