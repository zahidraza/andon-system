package in.andonsystem.v2.restcontroller;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import in.andonsystem.v2.dto.RestError;
import in.andonsystem.v2.service.UserService;
import in.andonsystem.v2.ApiUrls;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by razamd on 3/31/2017.
 */
@RestController
@RequestMapping(ApiUrls.ROOT_URL_MISCELLANEOUS)
public class MiscRestController {
    private final Logger logger = LoggerFactory.getLogger(MiscRestController.class);

    @Autowired UserService userService;

    @GetMapping(ApiUrls.URL_MISCELLANEOUS_CONFIG)
    public ResponseEntity<?> getAppConfig(@RequestParam(value = "version") String version){
        String lastSync = ConfigUtility.getInstance().getConfigProperty(Constants.APP_LAST_SYNC);
        DateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        Date appLastSync = null;
        try {
            appLastSync = sdf.parse(lastSync);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Object> response = new HashedMap();
        response.put("appSync", appLastSync.getTime());
        response.put("update",checkAppUpdate(version));
        return ResponseEntity.ok(response);
    }

    @GetMapping(ApiUrls.URL_CURRENT_TIME)
    public ResponseEntity<?> getCurrentTime(){
        Map<String, Object> response = new HashedMap();
        response.put("currentTime", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PutMapping(ApiUrls.URL_CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestParam("email") String email,
                                            @RequestParam(value = "oldPassword") String oldPassword,
                                            @RequestParam("newPassword") String newPassword){
        logger.debug("Change password: email = {}", email);
        if (userService.findByEmail(email) == null){
            return new ResponseEntity<Object>(new RestError(404,404,"User with email id = " + email + " not found.", "", ""), HttpStatus.NOT_FOUND);
        }
        Map<String, Object> response = new HashedMap();
        boolean res = userService.changePassword(email, oldPassword, newPassword);
        if(res){
            response.put("status", "SUCCESS");
        }else {
            response.put("status", "FAIL");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping(ApiUrls.URL_FORGOT_PASSWORD_SEND_OTP)
    public ResponseEntity<?> forgotPasswordSendOTP(@RequestParam("email") String email){
        logger.debug("forgotPasswordSendOTP: email = {}", email);
        if (userService.findByEmail(email) == null){
            return new ResponseEntity<Object>(new RestError(404,404,"User with email id = " + email + " not found.", "", ""), HttpStatus.NOT_FOUND);
        }
        Map<String, Object> response = new HashedMap();
        userService.sendOTP(email);
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    @PutMapping(ApiUrls.URL_FORGOT_PASSWORD_VERIFY_OTP)
    public ResponseEntity<?> forgotPasswordVerifyOTP(@RequestParam("email") String email,
                                                     @RequestParam("otp") String otp){
        logger.debug("forgotPasswordVerifyOTP: email = {}, otp = {}", email, otp);
        if (userService.findByEmail(email) == null){
            return new ResponseEntity<Object>(new RestError(404,404,"User with email id = " + email + " not found.", "", ""), HttpStatus.NOT_FOUND);
        }
        Map<String, Object> response = new HashedMap();
        boolean res = userService.verifyOtp(email, otp);
        if(res){
            response.put("status", "SUCCESS");
            response.put("message","OTP verified.");
        }else {
            response.put("status", "FAIL");
            response.put("message","Incorrect OTP");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping(ApiUrls.URL_FORGOT_PASSWORD_CHANGE_PASSWORD)
    public ResponseEntity<?> changeForgotPassword(@RequestParam("email") String email,
                                                  @RequestParam(value = "otp") String otp,
                                                  @RequestParam("newPassword") String newPassword){
        logger.debug("changeForgotPassword: email = {}, otp = {}", email, otp);

        if (userService.findByEmail(email) == null){
            return new ResponseEntity<Object>(new RestError(404,404,"User with email id = " + email + " not found.", "", ""), HttpStatus.NOT_FOUND);
        }
        Map<String, Object> response = new HashedMap();
        boolean res = userService.changeForgotPassword(email, otp, newPassword);
        if(res){
            response.put("status", "SUCCESS");
        }else {
            response.put("status", "FAIL");
        }
        return ResponseEntity.ok(response);
    }


    private Boolean checkAppUpdate(String version) {
        ConfigUtility configUtility = ConfigUtility.getInstance();
        String ver = configUtility.getConfigProperty(Constants.APP_VERSION);
        String[] v1 = ver.split("-");

        int currVer = (100 * Integer.parseInt(v1[0])) + (10 * Integer.parseInt(v1[1])) + Integer.parseInt(v1[2]);

        String[] v2 = version.split("-");
        int oldVer = (100 * Integer.parseInt(v2[0])) + (10 * Integer.parseInt(v2[1])) + Integer.parseInt(v2[2]);

        if (currVer > oldVer) {
            return true;
        } else {
            return false;
        }
    }
}
