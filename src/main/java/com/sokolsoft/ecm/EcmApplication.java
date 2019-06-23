package com.sokolsoft.ecm;

import com.sokolsoft.ecm.core.model.Contragent;
import com.sokolsoft.ecm.core.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
public class EcmApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EcmApplication.class, args);


        context.getBean(DemoData.class).uploadData();
    }

    @Configuration
    @EnableWebSecurity
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {

                        @Override
                        public void commence(HttpServletRequest request, HttpServletResponse response,
                                             AuthenticationException authException) throws IOException, ServletException {
                            if (authException != null) {
//                                authException.printStackTrace();
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().print("Unauthorizated....");
                            }
                        }
                    })
                  .and()
                    .csrf().disable()
                    .httpBasic()
                  .and()
                    .authorizeRequests()
                    .antMatchers("/document/**", "currentUser", "/document**", "/createDocument/**", "/config**", "/folders/**").hasRole("USER")
                    .antMatchers("/authentication", "/login", "/logout")
                    .permitAll()
                  .and()
                    .authorizeRequests()
                    .antMatchers("/**").authenticated()
                  .and()
                    .formLogin()
                    .loginProcessingUrl("/authentication")
                    .successHandler(new AjaxAuthenticationSuccessHandler())
                    .failureHandler(new AjaxAuthenticationFailureHandler())
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .permitAll()
//                  .and().requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
//                    .anyRequest().permitAll()
                  .and().logout();
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth)
                throws Exception {

            auth.inMemoryAuthentication()
                    .withUser("admin")
                    .password("{noop}admin")
                    .roles("USER");
        }

    }

    public static class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
        public AjaxAuthenticationSuccessHandler() {
        }

        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
            response.setStatus(200);
        }
    }
    public static class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
        public static final String UNAUTHORIZED_MESSAGE = "Authentication failed";

        public AjaxAuthenticationFailureHandler() {
        }

        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
            response.sendError(401, "Authentication failed");
        }
    }

    @Configuration
    protected static class RepositoryRest implements RepositoryRestConfigurer {
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
            config.exposeIdsFor(User.class);
            config.exposeIdsFor(Contragent.class);
        }
    }

//    @Configuration
//    public class ActuatorSecurity extends WebSecurityConfigurerAdapter {
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
//                    .anyRequest().permitAll();
//        }
//
//    }
}
