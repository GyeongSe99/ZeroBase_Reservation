package com.zerobase.reservation.common.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
            .antMatchers(
                    "/",
                    "/member/register",
                    "/store/show/**")
            .permitAll()
            .antMatchers(
                    "/store/addStore/**",
                    "/member/partnership/cancel",
                    "/store/storeList",
                    "/store/updateStore")
            .hasAuthority("partner") // partner만 가능
            .antMatchers("/member/partnership/join", "/reservation/{storeId}")
            .authenticated() // 로그인한 사용자만 접근 가능
            .anyRequest()
            .permitAll() // 나머지는 모든 사용자에게 접근 허용
            .and()
            .exceptionHandling()
            .accessDeniedHandler(new CustomAccessDeniedHandler())
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                    UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
           .antMatchers("/v2/api-docs", "/swagger-resources/**",
                   "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception");
    }
}
