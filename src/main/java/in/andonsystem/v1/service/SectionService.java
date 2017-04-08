package in.andonsystem.v1.service;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;
import org.springframework.stereotype.Component;
/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@Component
public class SectionService {
    public String[] getSectios(){

       return  MiscUtil.getInstance().getConfigProperty(Constants.SECTIONS).split(";");
    }

}
