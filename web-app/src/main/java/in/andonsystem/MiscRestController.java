package in.andonsystem;

import in.andonsystem.util.ConfigUtility;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.v1.dto.StyleCO;
import in.andonsystem.v1.entity.Designation;
import in.andonsystem.v1.service.DesignationService;
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
import java.util.*;

/**
 * Created by razamd on 3/31/2017.
 */
@RestController
@RequestMapping(ApiUrls.ROOT_URL_MISCELLANEOUS)
public class MiscRestController {
    private final Logger logger = LoggerFactory.getLogger(MiscRestController.class);

    @Autowired UserService userService;

    @Autowired DesignationService designationService;

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

    @PostMapping(ApiUrls.URL_STYLE_CHANGE_OVER)
    public ResponseEntity<?> styleChangeOver(@RequestBody StyleCO styleCO) {
        //First Floor desgnIds : 1,6,7,9,43
        //Second Floor desgnIds : 2,5,8,10,43,8
        List<Long> desgnList = new ArrayList<>();
        desgnList.add(1L);
        desgnList.add(6L);
        desgnList.add(7L);
        desgnList.add(9L);
        desgnList.add(43L);
        desgnList.add(2L);
        desgnList.add(5L);
        desgnList.add(8L);
        desgnList.add(10L);

        List<Designation> designations = designationService.findAll(desgnList);
        StringBuilder builder = new StringBuilder();
        designations.forEach(designation -> {
            designation.getUsers().forEach(user -> {
                if (user.getActive()) {
                    builder.append(user.getMobile()).append(",");
                }
            });
        });
        if (builder.length()> 0) builder.setLength(builder.length()-1);
        if (builder.length() > 0) {
            String message = "Line: " + styleCO.getLine() + " Changeover from " + styleCO.getFrom() + " to " + styleCO.getTo() + " Remarks: " + styleCO.getRemarks() + " Submitted by: " + styleCO.getSubmitBy();

            logger.info("Sending style change over message to: {},\n message: {}", builder.toString(),message);
            MiscUtil.sendSMS(builder.toString(), message);
        }
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("message", "Style Change Over message sent successfully");

        return ResponseEntity.ok(resp);
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
