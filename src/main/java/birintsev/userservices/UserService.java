package birintsev.userservices;

import birintsev.model.LivingSpace;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Additional service-layer between controllers and model-level-services
 * */
public interface UserService {
    /**
     * Returns the next step state of a LivingSpace
     * associated with user (specified by session).
     * If no LivingSpace is associated with passed user,
     * random LivingSpace will be generated (i.e. a new game will be started)
     *
     * */
    Future<LivingSpace> nextStep(
        HttpSession httpSession
    ) throws ExecutionException, InterruptedException;

    boolean cancelCurrentCalculation(
        HttpSession httpSession
    );

    /**
     * @return current state of user's living space
     * */
    Future<LivingSpace> getCurrent(HttpSession userSession);

    /**
     * @return  previous state of user's living space
     * */
    Future<LivingSpace> setCurrent(
        HttpSession userSession,
        Future<LivingSpace> newCurrent
    );
}
