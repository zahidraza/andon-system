package in.andonsystem.v2.assembler;

import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.restcontroller.UserRestController;
import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Resource;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class UserAssembler extends ResourceAssemblerSupport<UserDto, Resource>{
    
    public UserAssembler(){
        super(UserRestController.class, Resource.class);
    }

    @Override
    public Resource toResource(UserDto userDto) {
        return new Resource<>(userDto, linkTo(methodOn(UserRestController.class).getUser(userDto.getId())).withSelfRel());
    }

    @Override
    public List<Resource> toResources(Iterable<? extends UserDto> users) {
        List<Resource> resources = new ArrayList<>();
        for(UserDto user : users) {
            resources.add(new Resource<>(user, linkTo(methodOn(UserRestController.class).getUser(user.getId())).withSelfRel()));
        }
        return resources;
    }
}
