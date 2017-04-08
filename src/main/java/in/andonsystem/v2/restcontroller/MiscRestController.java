package in.andonsystem.v2.restcontroller;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;
import in.andonsystem.v2.util.ApiV2Urls;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by razamd on 3/31/2017.
 */
@RestController
@RequestMapping(ApiV2Urls.ROOT_URL_MISCELLANEOUS)
public class MiscRestController {

    @RequestMapping(ApiV2Urls.URL_MISCELLANEOUS_CONFIG)
    public ResponseEntity<?> getAppConfig(@RequestParam(value = "version", defaultValue = "") String version){
        Boolean initialize = Boolean.parseBoolean(MiscUtil.getInstance().getConfigProperty(Constants.APP_INITIALIZE, "false"));

        Map<String, Object> response = new HashedMap();
        response.put("initialize", initialize);
        if(!version.equals("")){
            response.put("update",checkAppUpdate(version));
        }else {
            response.put("version", MiscUtil.getInstance().getConfigProperty(Constants.APP_VERSION));
        }
        return ResponseEntity.ok(response);
    }

    private Boolean checkAppUpdate(String version) {
        MiscUtil miscUtil = MiscUtil.getInstance();
        String ver = miscUtil.getConfigProperty(Constants.APP_VERSION);
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
