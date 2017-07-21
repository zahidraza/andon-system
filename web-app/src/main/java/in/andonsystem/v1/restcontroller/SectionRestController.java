package in.andonsystem.v1.restcontroller;
import in.andonsystem.v1.service.SectionService;
import in.andonsystem.v1.ApiUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@RestController


@RequestMapping(ApiUrls.ROOT_URL_SECTIONS)
public class SectionRestController {
    private final Logger logger = LoggerFactory.getLogger(DepartmentRestController.class);

    @Autowired SectionService sectionService;

    @GetMapping
    public ResponseEntity<?> getSections(){
        logger.debug("getSections");
        String[] sections = sectionService.getSections();
        return new ResponseEntity<>(sections, HttpStatus.OK);
    }
}
