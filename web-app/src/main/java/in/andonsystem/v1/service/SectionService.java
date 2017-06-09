package in.andonsystem.v1.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import org.springframework.stereotype.Component;
/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@Component
public class SectionService {
    public String[] getSectios(){

       return  ConfigUtility.getInstance().getConfigProperty(Constants.SECTIONS).split(";");
    }

}
