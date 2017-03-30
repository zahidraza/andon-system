package in.andonsystem.v2.service;

import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.respository.BuyerRepository;
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

    private final BuyerRepository buyerRepository;

    @Autowired
    public BuyerService(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    public Buyer findOne(Long id){
        return buyerRepository.findOne(id);
    }

    public List<Buyer> findAll(){
        return buyerRepository.findAll();
    }

    public boolean exists(Long id){
        return buyerRepository.exists(id);
    }

    @Transactional
    public Buyer save(Buyer buyer){
        return buyerRepository.save(buyer);
    }
}
