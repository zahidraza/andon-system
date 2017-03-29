package in.andonsystem.v2.restcontroller;

import in.andonsystem.v2.assembler.UserAssembler;
import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.service.UserService;
import java.net.URI;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/users")
public class UserRestController{
    
    private final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    
    @Autowired UserService userService;  //Service which will do all data retrieval/manipulation work

    @Autowired UserAssembler userAssembler;
    
    @GetMapping
    public ResponseEntity<?> listAllUsers(Pageable pageable, PagedResourcesAssembler assembler) {
        logger.debug("listAllUsers()");
        Page<UserDto> page = userService.findAllByPage(pageable);
        return new ResponseEntity<>(assembler.toResource(page, userAssembler), HttpStatus.OK);
    }
  
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        logger.debug("getUser(): id = {}",id);
        UserDto user = userService.findOne(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userAssembler.toResource(user), HttpStatus.OK);
    }
   
    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserDto user) {
        logger.debug("createUser():\n {}", user.toString());
        user = userService.save(user);
        Link selfLink = linkTo(UserRestController.class).slash(user.getId()).withSelfRel();
        return ResponseEntity.created(URI.create(selfLink.getHref())).build();
    }
 
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") long id,@Validated @RequestBody UserDto user) {
        logger.debug("updateUser(): id = {} \n {}",id,user);
        if (!userService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setId(id);  
        user = userService.update(user);
        return new ResponseEntity<>(userAssembler.toResource(user), HttpStatus.OK);
    }
  
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) {
        logger.debug("deleteUser(): id = {}",id);
        if (!userService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
