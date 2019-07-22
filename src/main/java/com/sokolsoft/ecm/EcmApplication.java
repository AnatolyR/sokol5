package com.sokolsoft.ecm;

import com.sokolsoft.ecm.core.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@SpringBootApplication
public class EcmApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(EcmApplication.class, args);

//        createSecuritySchema(context.getBean(DataSource.class));

        context.getBean(DemoData.class).uploadData();
    }

    private static void createSecuritySchema(DataSource dataSource) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("/db/users.sql"));
        dataSourceInitializer.setDatabasePopulator(databasePopulator);
        dataSourceInitializer.afterPropertiesSet();
    }

    @Bean
    public WebMvcConfigurer forwardToIndex() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("forward:/index.html");
                registry.addViewController("/dictionaries").setViewName("forward:/index.html");
                registry.addViewController("/dictionaries/*").setViewName("forward:/index.html");
                registry.addViewController("/folders").setViewName("forward:/index.html");
                registry.addViewController("/folders/*").setViewName("forward:/index.html");
                registry.addViewController("/document/*").setViewName("forward:/index.html");
                registry.addViewController("/search").setViewName("forward:/index.html");
                registry.addViewController("/reports").setViewName("forward:/index.html");
                registry.addViewController("/contragent/*").setViewName("forward:/index.html");
                registry.addViewController("/user/*").setViewName("forward:/index.html");

            }
        };
    }

    @Bean
    @Autowired
    public JdbcUserDetailsManager detailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Configuration
    @EnableWebSecurity
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {
        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        DataSource dataSource;

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
                    .antMatchers("/api/document/**", "/api/currentUser", "/api/document**", "/api/createDocument/**", "/api/config**", "/api/folders/**").hasRole("USER")
                    .antMatchers("/api/authentication", "/api/login",
                            "/api/logout", "/css/*", "/js/*", "/favicon.ico",
                            "/index.html", "/")
                    .permitAll()
                  .and()
                    .authorizeRequests()
                    .antMatchers("/**").authenticated()
                  .and()
                    .formLogin()
                    .loginProcessingUrl("/api/authentication")
                    .successHandler(new AjaxAuthenticationSuccessHandler())
                    .failureHandler(new AjaxAuthenticationFailureHandler())
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .permitAll()
//                  .and().requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
//                    .anyRequest().permitAll()
                  .and().logout().logoutUrl("/api/logout");
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth)
                throws Exception {

            auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder);
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
            config.exposeIdsFor(DocumentKind.class);
            config.exposeIdsFor(DeliveryMethod.class);
            config.exposeIdsFor(Attach.class);
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
