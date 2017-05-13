package in.andonsystem.v1.restcontroller;

import in.andonsystem.v1.entity.Designation;
import in.andonsystem.v1.service.DesignationService;
import in.andonsystem.v1.util.ApiV1Urls;
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
@RequestMapping(ApiV1Urls.Root_URLS_DESIGNATIONS)
public class DesignationRestController {
    @Autowired
    DesignationService designationService;

    @GetMapping
    public ResponseEntity<?> getDesignations(){
       List<Designation> designations= designationService.findAll();
       return ResponseEntity.ok(designations);
    }
}
