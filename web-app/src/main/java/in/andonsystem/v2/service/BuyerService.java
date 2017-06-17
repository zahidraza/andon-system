package in.andonsystem.v2.service;

import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.respository.BuyerRepository;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by razamd on 3/30/2017.
 */
@Service
@Transactional(readOnly = true)
public class BuyerService {
    private final Logger logger = LoggerFactory.getLogger(BuyerService.class);
    private final BuyerRepository buyerRepository;

    @Autowired
    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    public Buyer findOne(Long id){
        logger.debug("findOne: id = {}", id);
        return buyerRepository.findOne(id);
    }

    public List<Buyer> findAll(){
        logger.debug("findAll()");
        List<Buyer> buyers = buyerRepository.findAll();
        buyers.forEach(buyer -> Hibernate.initialize(buyer.getUsers()));
        return buyers;
    }

    public boolean exists(Long id){
        return buyerRepository.exists(id);
    }

    @Transactional
    public Buyer save(Buyer buyer){
        logger.debug("saveBuyer()");
        return buyerRepository.save(buyer);
    }
}
