package in.andonsystem;

/**
 * Created by mdzahidraza on 17/06/17.
 */
public class Test {


//    private void loadIssues(){
//        Connection conn = null;
//        Statement statement = null;
//        BufferedReader br = null;
//        try {
//            conn = dataSource.getConnection();
//            statement = conn.createStatement();
//            //conn.setAutoCommit(true);
//            br = new BufferedReader(new InputStreamReader(new FileInputStream(MiscUtil.getAndonHome()+ File.separator+"issue.sql")));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String line = null;
//        String baseSql = null;
//        String sql = null;
//        Boolean result;
//        try{
//            while ((line = br.readLine()) != null){
////            for (int i = 0; i < 10 ; i++){
////                line = br.readLine();
//                if (line.contains("INSERT")) {
//                    baseSql = line;
//                }else {
//                    sql = baseSql + "\n" + line;
//                    try {
//                        result = statement.execute(sql);
//                        if (!result){
//                            System.out.println("Failed: " + line);
//                        }
//                    } catch (SQLException e) {
//                        //e.printStackTrace();
//                        System.out.println("Failed: " + line);
//                    }
//                }
//            }
//        }catch (IOException e){
//
//        }
//    }
}

/*
package in.andonsystem;

import in.andonsystem.util.DbBackupUtility;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.util.Scheduler;
import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.service.UserService;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;

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
        //scheduleDbBackup();
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
        long todayMinsAfterMidnight = (System.currentTimeMillis() % (24*60*60*1000)) / (1000*60);
        long initialDelay = (23*60) > todayMinsAfterMidnight ? (23*60) - todayMinsAfterMidnight : (24*60) - (todayMinsAfterMidnight - 23*60);
        long oneDayMins = 24*60;

        Scheduler.getInstance().getScheduler()
                .scheduleAtFixedRate(() -> DbBackupUtility.backup(),initialDelay,oneDayMins, TimeUnit.MINUTES);
    }


}

 */
