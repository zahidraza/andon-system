package in.andonsystem.v1.restcontroller;

import in.andonsystem.v1.entity.Designation;
import in.andonsystem.v1.service.DesignationService;
import in.andonsystem.v1.ApiUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@RestController
@RequestMapping(ApiUrls.Root_URLS_DESIGNATIONS)
public class DesignationRestController {
    private final Logger logger = LoggerFactory.getLogger(DesignationRestController.class);

    @Autowired
    DesignationService designationService;

    @GetMapping
    public ResponseEntity<?> getDesignations() {
        logger.debug("getDesignations()");
        List<Designation> designations = designationService.findAll();
        return ResponseEntity.ok(designations);
    }
}
