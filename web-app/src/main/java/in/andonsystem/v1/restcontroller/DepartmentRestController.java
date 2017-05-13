package in.andonsystem.v1.restcontroller;

import in.andonsystem.v1.service.DepartmentService;
import in.andonsystem.v1.util.ApiV1Urls;
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

@RequestMapping(ApiV1Urls.ROOT_URL_DEPARTMENTS)
public class DepartmentRestController {

    @Autowired
    DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<?> getDepartments(){
        String[] departments = departmentService.getDepartments();
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }
}
