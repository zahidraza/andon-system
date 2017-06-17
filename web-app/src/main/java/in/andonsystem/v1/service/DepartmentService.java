package in.andonsystem.v1.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import in.andonsystem.v1.restcontroller.DepartmentRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
public class DepartmentService {
    private final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    public String[] getDepartments(){
        logger.debug("getDepartments()");
        return ConfigUtility.getInstance().getConfigProperty(Constants.DEPARTMENTS).split(";");
    }
}
