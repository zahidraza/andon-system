package in.andonsystem;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.enums.UserType;
import in.andonsystem.v2.service.UserService;
import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;

@SpringBootApplication
@Controller
@ServletComponentScan
public class App/* extends SpringBootServletInitializer*/{

    private final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(Application.class);
//    }



    @Bean
    CommandLineRunner init(
            UserService userService) {

        return (args) -> {
            if(userService.count() == 0){
                userService.save(new UserDto("Md Zahid Raza", "zahid7292@gmail.com", "ADMIN", "8987525008", UserType.MERCHANDISING.getValue()));
            }
        };
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    public Mapper dozerBeanMapper() {
        List<String> list = new ArrayList<>();
        list.add("dozer_mapping.xml");
        return new DozerBeanMapper(list);
    }

    @GetMapping(value= "/")
    public String hello() {
        logger.debug("home page");
        return "index";
    }

//    @Bean
//    public ContextListener contextListener(){
//        return new ContextListener();
//    }

//    @Bean
//    public DispatcherServlet dispatcherServlet() {
//        DispatcherServlet servlet = new DispatcherServlet();
//        ServletContext context = servlet.getServletContext();
//        context.addListener(new ContextListener());
//        return  servlet;
//    }
//
//    @Bean
//    public ServletRegistrationBean dispatcherServletRegistration() {
//        ServletRegistrationBean registrationBean = new ServletRegistrationBean(dispatcherServlet(), "/*");
//        registrationBean.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
//        return registrationBean;
//    }

    @Bean
    public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/restapi/*");
        // our rest resources will be available in the path /rest/*
        registration.addInitParameter("com.sun.jersey.config.property.packages", "in.andonsystem.v1");
        return registration;
    }
}
