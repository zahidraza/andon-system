package in.andonsystem.v2.assembler;

import in.andonsystem.v2.dto.UserDto;
import in.andonsystem.v2.restcontroller.UserRestController;
import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.Link;
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
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(UserRestController.class).getUser(userDto.getId())).withSelfRel());
        links.add(linkTo(methodOn(UserRestController.class).searchByName(null)).withRel("user.search.name"));
        links.add(linkTo(methodOn(UserRestController.class).searchByEmail(null)).withRel("user.search.email"));
        return new Resource<>(userDto, links);
    }

    @Override
    public List<Resource> toResources(Iterable<? extends UserDto> users) {
        List<Resource> resources = new ArrayList<>();
        for(UserDto user : users) {
            resources.add(toResource(user));
        }
        return resources;
    }
}
