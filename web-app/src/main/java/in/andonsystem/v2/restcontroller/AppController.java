package in.andonsystem.v2.restcontroller;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppController {
    
    @GetMapping("/")
    public ResponseEntity<?> root(){
        ResourceSupport resources = new ResourceSupport();
        ControllerLinkBuilder linkBuilder = linkTo(AppController.class);
        resources.add(linkBuilder.slash("v2").slash("users").withRel("users"));
        resources.add(linkBuilder.slash("v2").slash("buyers").withRel("buyers"));
        resources.add(linkBuilder.slash("v2").slash("issues").withRel("issues"));
        resources.add(linkBuilder.slash("v2").slash("misc").withRel("misc"));
        resources.add(linkBuilder.slash("v2").slash("problems").withRel("problems"));
        resources.add(linkBuilder.slash("v2").slash("teams").withRel("teams"));

        resources.add(linkBuilder.slash("v1").slash("sections").withRel("sections"));
        resources.add(linkBuilder.slash("v1").slash("departments").withRel("departments"));
        resources.add(linkBuilder.slash("v1").slash("problems").withRel("problems"));
        resources.add(linkBuilder.slash("v1").slash("issues").withRel("issues"));
        return ResponseEntity.ok(resources);
    }
}
