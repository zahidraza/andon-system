package in.andonsystem.dozerconverter;

import in.andonsystem.v1.entity.Problem;
import org.dozer.DozerConverter;

/**
 * Created by razamd on 3/30/2017.
 */
public class LongProblemConverter extends DozerConverter<Long, Problem> {

    public LongProblemConverter() {
        super(Long.class,Problem.class);
    }

    @Override
    public Problem convertTo(Long aLong, Problem problem) {
        if (aLong == null || aLong == 0L){
            return null;
        }
        return new Problem(aLong);
    }

    @Override
    public Long convertFrom(Problem problem, Long aLong) {
        if (problem == null) return null;
        return problem.getId();
    }
}
