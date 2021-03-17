package birintsev.model.modelservices;

import birintsev.model.LivingSpace;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface GameOfLiveService {

    Future<LivingSpace> nextStep(LivingSpace livingSpace)
        throws ExecutionException, InterruptedException;
}
