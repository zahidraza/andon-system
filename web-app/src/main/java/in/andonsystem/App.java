
package in.andonsystem;

import in.andonsystem.util.DbBackupUtility;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.util.Scheduler;
import in.andonsystem.v1.service.IssueService;
import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired IssueService issueService;

    @Autowired in.andonsystem.v2.service.IssueService issueService2;

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
        scheduleDbBackup();
        scheduleApp1AutoFix();
        scheduleApp2AutoFix();
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

    @GetMapping(value = "/")
    public String homePage() {
        logger.debug("home page");
        return "index";
    }

    private void scheduleDbBackup() {
        //Schedule Database backup at 11.00 PM daily
        int todayMinsAfterMidnight = MiscUtil.getMinutesSinceMidnight(new Date());
        long scheduleAt = 23*60;  // 11:00 PM
        long diff = scheduleAt - todayMinsAfterMidnight;
        long initialDelay = diff > 0? diff : (24*60) - diff;

        Scheduler.getInstance().getScheduler()
                .scheduleAtFixedRate(() -> DbBackupUtility.backup(),initialDelay,24*60, TimeUnit.MINUTES);
    }

    private void scheduleApp1AutoFix() {
        int todayMinsAfterMidnight = MiscUtil.getMinutesSinceMidnight(new Date());
        long scheduleAt = 18*60 + 15;  // 6:15 PM
        long diff = scheduleAt - todayMinsAfterMidnight;
        long initialDelay = diff > 0? diff : (24*60) - diff;
        Scheduler.getInstance().getScheduler()
                .scheduleAtFixedRate(() -> issueService.autoFixIssues(),initialDelay,24*60, TimeUnit.MINUTES);
    }

    private void scheduleApp2AutoFix() {
        int todayMinsAfterMidnight = MiscUtil.getMinutesSinceMidnight(new Date());
        long scheduleAt = 18*60 + 30;  // 6:30 PM
        long diff = scheduleAt - todayMinsAfterMidnight;
        long initialDelay = diff > 0? diff : (24*60) - diff;
        Scheduler.getInstance().getScheduler()
                .scheduleAtFixedRate(() -> issueService2.autoFixIssues(),initialDelay,24*60, TimeUnit.MINUTES);
    }


}
