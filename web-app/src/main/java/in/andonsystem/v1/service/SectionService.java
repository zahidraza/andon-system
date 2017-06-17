package in.andonsystem.v1.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@Component
public class SectionService {
    private final Logger logger = LoggerFactory.getLogger(SectionService.class);

    public String[] getSections(){
        logger.debug("getSections()");
       return  ConfigUtility.getInstance().getConfigProperty(Constants.SECTIONS).split(";");
    }

}
