package birintsev.userservices;

import birintsev.LivingSpaceRandomizer;
import birintsev.model.LivingSpace;
import birintsev.model.modelservices.GameOfLiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DefaultUserService implements UserService {

    private static final String NEXT_STEP_CALCULATION =
        "NEXT_STEP_CALCULATION";

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DefaultUserService.class
    );

    private final GameOfLiveService gameOfLiveService;

    private final LivingSpaceRandomizer livingSpaceRandomizer;

    public DefaultUserService(
        GameOfLiveService gameOfLiveService,
        LivingSpaceRandomizer livingSpaceRandomizer
    ) {
        this.gameOfLiveService = gameOfLiveService;
        this.livingSpaceRandomizer = livingSpaceRandomizer;
    }

    @Override
    public boolean cancelCurrentCalculation(HttpSession httpSession) {
        Future<LivingSpace> livingSpaceFuture = getCurrentlyCalculated(
            httpSession
        );
        boolean isRequestInProcess = livingSpaceFuture != null;
        if (!isRequestInProcess) {
            return false;
        }
        livingSpaceFuture.cancel(true);
        setCurrentlyCalculated(null, httpSession);
        return true;
    }

    @Override
    public Future<LivingSpace> nextStep(
        HttpSession httpSession
    ) throws ExecutionException, InterruptedException {
        Future<LivingSpace> prevStep = Optional.ofNullable(
            getCurrentlyCalculated(
                httpSession
            )
        ).orElseGet(
            livingSpaceRandomizer::mockRandomWrappedWithDefaultSize
        );
        Future<LivingSpace> nextStep = gameOfLiveService.nextStep(
            prevStep.get()
        );
        setCurrentlyCalculated(nextStep, httpSession);
        return nextStep;
    }

    @Override
    public Future<LivingSpace> getCurrent(
        HttpSession userSession
    ) {
        return getCurrentlyCalculated(userSession);
    }

    @Override
    public Future<LivingSpace> setCurrent(
        HttpSession userSession,
        Future<LivingSpace> newCurrent
    ) {
        Future<LivingSpace> previous = getCurrent(userSession);
        setCurrentlyCalculated(newCurrent, userSession);
        return previous;
    }

    private void setCurrentlyCalculated(
        Future<LivingSpace> livingSpaceFuture,
        HttpSession userSession
    ) {
        userSession.setAttribute(NEXT_STEP_CALCULATION, livingSpaceFuture);
    }

    private Future<LivingSpace> getCurrentlyCalculated(
        HttpSession httpSession
    ) {
        return (Future<LivingSpace>)
            httpSession.getAttribute(NEXT_STEP_CALCULATION);
    }
}
