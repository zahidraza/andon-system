package in.andonsystem.v1.service;

import in.andonsystem.v1.entity.Designation;
import in.andonsystem.v1.repository.DesignationRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@Service
public class DesignationService {

    @Autowired
    DesignationRepository designationRepository;

    public List<Designation> findAll(){
        List<Designation> list = designationRepository.findAll();
        list.forEach(designation -> Hibernate.initialize(designation.getProblems()));
        return list;
    }
}
