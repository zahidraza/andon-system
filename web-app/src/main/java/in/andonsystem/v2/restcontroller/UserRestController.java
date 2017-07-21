package in.andonsystem.v2.restcontroller;

import in.andonsystem.v2.assembler.UserAssembler;
import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.dto.UserDtoPatch;
import in.andonsystem.v2.service.UserService;
import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import in.andonsystem.v2.ApiUrls;
import org.apache.commons.collections.map.HashedMap;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping(ApiUrls.ROOT_URL_USERS)
public class UserRestController{
    private final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    
    @Autowired UserService userService;  //Service which will do all data retrieval/manipulation work

    @Autowired UserAssembler userAssembler;

    @Autowired Mapper mapper;
    
    @GetMapping
    public ResponseEntity<?> listAllUsers(@RequestParam(value = "after", defaultValue = "0") Long after) {
        logger.debug("listAllUsers(): after = {}", after);
        List<UserDto> list = userService.findAllAfter(after);
        Map<String, Object> response = new HashedMap();
        response.put("users",userAssembler.toResources(list));
        response.put("userSync", System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
  
    @GetMapping(ApiUrls.URL_USERS_USER)
    public ResponseEntity<?> getUser(@PathVariable("userId") long id) {
        logger.debug("getUser(): id = {}",id);
        UserDto user = userService.findOne(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userAssembler.toResource(user), HttpStatus.OK);
    }

    @GetMapping(ApiUrls.URL_USERS_USER_SEARCH_BY_NAME)
    public ResponseEntity<?> searchByName(@RequestParam("name") String name){
        UserDto user = userService.findByUsername(name);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userAssembler.toResource(user), HttpStatus.OK);
    }

    @GetMapping(ApiUrls.URL_USERS_USER_SEARCH_BY_EMAIL)
    public ResponseEntity<?> searchByEmail(@RequestParam("email") String email){
        UserDto user = userService.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userAssembler.toResource(user), HttpStatus.OK);
    }
   
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto user) {
        logger.debug("createUser()");
        user = userService.save(user);
        Link selfLink = linkTo(UserRestController.class).slash(user.getId()).withSelfRel();
        return ResponseEntity.created(URI.create(selfLink.getHref())).body(user);
    }
 
    @PutMapping(ApiUrls.URL_USERS_USER)
    public ResponseEntity<?> updateUser(@PathVariable("userId") long id, @Validated @RequestBody UserDto user) {
        logger.debug("updateUser(): id = {}",id);
        if (!userService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setId(id);  
        user = userService.update(user);
        return new ResponseEntity<>(userAssembler.toResource(user), HttpStatus.OK);
    }

    @PatchMapping(ApiUrls.URL_USERS_USER)
    public ResponseEntity<?> patchUser(@PathVariable("userId") long id,@Validated @RequestBody UserDtoPatch user) {
        logger.debug("patchUser(): id = {}",id);
        if (!userService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        UserDto userDto = mapper.map(user, UserDto.class);
        userDto.setId(id);
        userDto = userService.update(userDto);
        return ResponseEntity.ok(userDto);
    }
  
    @DeleteMapping(ApiUrls.URL_USERS_USER)
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") long id) {
        logger.debug("deleteUser(): id = {}",id);
        if (!userService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
