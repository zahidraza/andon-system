package in.andonsystem;

import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.enums.Level;
import in.andonsystem.v2.enums.Role;
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
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@Controller
public class App extends SpringBootServletInitializer{

    private final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(App.class);
    }



    @Bean
    CommandLineRunner init(
            UserService userService) {

        return (args) -> {
            if(userService.count() == 0){
                userService.save(new UserDto("Md Jawed Akhtar", "jawed.akhtar1993@gmail.com", Role.ADMIN.name(), "8987525008", UserType.MERCHANDISING.getValue(), Level.LEVEL4.getValue()));
                userService.save(new UserDto("Md Zahid Raza", "zahid7292@gmail.com", Role.ADMIN.name(), "8987525008", UserType.SAMPLING.getValue(), Level.LEVEL4.getValue()));
                userService.save(new UserDto("Md Taufeeque Alam", "taufeeque8@gmail.com", Role.ADMIN.name(), "8987525008", UserType.FACTORY.getValue(), Level.LEVEL4.getValue()));
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

}
