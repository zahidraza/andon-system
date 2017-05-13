package in.andonsystem.v1.service;


import in.andonsystem.v1.entity.Problem;
import in.andonsystem.v1.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@Service(value = "pService")
public class ProblemService {
  @Autowired
  private ProblemRepository problemRepository;


   public List<Problem> findAll(){

       return problemRepository.findAll();
   }



}
