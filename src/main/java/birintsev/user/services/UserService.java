package birintsev.user.services;

import birintsev.model.LivingSpace;
import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * Additional service-layer between controllers and model-level-services
 * */
public interface UserService {

    /**
     * Returns the next step state of a LivingSpace
     * associated with user (specified by session).
     *
     * If no LivingSpace is associated with passed user,
     * random LivingSpace will be generated (i.e. a new game will be started)
     *
     * If there is a LivingSpace in progress (being calculated),
     * this method returns it.
     * */
    Future<LivingSpace> nextStep(
        HttpSession httpSession
    ) throws ExecutionException, InterruptedException;

    /**
     * @return  {@code true} if there was a LivingSpace being calculated
     *          and the calculation has been successfully terminated
     *          {@code false} otherwise
     * */
    boolean cancelCurrentCalculation(
        HttpSession httpSession
    );

    /**
     * @return current state of user's living space
     * */
    Future<LivingSpace> getCurrent(HttpSession userSession);

    void startNewGame(
        HttpSession userSession,
        Supplier<Future<LivingSpace>> livingSpaceSupplier
    );
}
