package in.andonsystem.v2.restcontroller;

import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.service.BuyerService;
import in.andonsystem.v2.ApiUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * Created by razamd on 3/30/2017.
 */
@RestController
@RequestMapping(ApiUrls.ROOT_URL_BUYERS)
public class BuyerRestController {
    private final Logger logger = LoggerFactory.getLogger(BuyerRestController.class);

    @Autowired
    BuyerService buyerService;

    @GetMapping
    public ResponseEntity<?> getAllBuyers(){
        logger.debug("getAllBuyers()");
        List<Buyer> buyers = buyerService.findAll();
        return new ResponseEntity<>(buyers, HttpStatus.OK);
    }

    @GetMapping(ApiUrls.URL_BUYERS_BUYER)
    public ResponseEntity<?> getBuyer(@PathVariable("buyerId") Long id){
        logger.debug("getBuyer(): buyerId = {}", id);
        if(!buyerService.exists(id)){
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(buyerService.findOne(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveBuyer(@Valid @RequestBody Buyer buyer){
        logger.debug("saveBuyers()");
        buyer = buyerService.save(buyer);
        Link selfLink = linkTo(BuyerRestController.class).slash(buyer.getId()).withSelfRel();
        return ResponseEntity.created(URI.create(selfLink.getHref())).body(buyer);
    }
}
