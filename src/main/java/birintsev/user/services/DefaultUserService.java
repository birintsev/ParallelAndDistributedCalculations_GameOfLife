package birintsev.user.services;

import birintsev.LivingSpaceRandomizer;
import birintsev.model.LivingSpace;
import birintsev.model.services.GameOfLiveService;
import birintsev.user.LivingSpaceHistory;
import birintsev.user.LivingSpaceHistoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class DefaultUserService implements UserService {

    private static final String LIVING_SPACE_HIST_SESS_ATTR_NAME =
        "LivingSpaceHistory";

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DefaultUserService.class
    );

    private final GameOfLiveService gameOfLiveService;

    private final LivingSpaceRandomizer livingSpaceRandomizer;

    private final int livingStateHistoryMaxSize;

    public DefaultUserService(
        GameOfLiveService gameOfLiveService,
        LivingSpaceRandomizer livingSpaceRandomizer,
        @Value(value = "${gameoflife.defaults.livingSpaceHistoryMaxSize}")
        int livingStateHistoryMaxSize
    ) {
        this.gameOfLiveService = gameOfLiveService;
        this.livingSpaceRandomizer = livingSpaceRandomizer;
        this.livingStateHistoryMaxSize = livingStateHistoryMaxSize;
    }

    @Override
    public boolean cancelCurrentCalculation(HttpSession httpSession) {
        Future<LivingSpace> latest = _getCurrent(httpSession);
        return latest != null && latest.cancel(true);
    }

    @Override
    public Future<LivingSpace> nextStep(
        HttpSession httpSession
    ) throws ExecutionException, InterruptedException {
        Future<LivingSpace> latest = _getCurrent(httpSession);
        if (latest == null) { // if no elements exist, start new game
            _addToStatesHistory(
                httpSession,
                livingSpaceRandomizer.mockRandomWrappedWithDefaultSize()
            );
            latest = _getCurrent(httpSession);
        }
        if (!latest.isDone()) {
            return latest;
        }
        _addToStatesHistory(
            httpSession,
            gameOfLiveService.nextStep(latest.get())
        );
        latest = _getCurrent(httpSession);
        return latest;
    }

    @Override
    public Future<LivingSpace> getCurrent(
        HttpSession userSession
    ) {
        return _getStatesHistory(userSession).getCurrent();
    }

    @Override
    public void startNewGame(
        HttpSession userSession,
        Supplier<Future<LivingSpace>> livingSpaceSupplier
    ) {
        _clearHistory(userSession);
        _addToStatesHistory(userSession, livingSpaceSupplier.get());
    }

    private LivingSpaceHistory _getStatesHistory(
        HttpSession userSession
    ) {
        return Optional.ofNullable(
            (LivingSpaceHistory)
                userSession.getAttribute(LIVING_SPACE_HIST_SESS_ATTR_NAME)
        )
        .orElseGet(() -> {
            userSession.setAttribute(
                LIVING_SPACE_HIST_SESS_ATTR_NAME,
                new LivingSpaceHistoryImpl(
                    livingStateHistoryMaxSize,
                    userSession
                )
            );
            return (LivingSpaceHistory)
                userSession.getAttribute(LIVING_SPACE_HIST_SESS_ATTR_NAME);
        });
    }

    private void _addToStatesHistory(
        HttpSession userSession,
        Future<LivingSpace> newState
    ) {
        LivingSpaceHistory history = _getStatesHistory(userSession);
        history.add(newState);
    }

    private Future<LivingSpace> _getCurrent(HttpSession userSession) {
        return _getStatesHistory(userSession).getCurrent();
    }

    private void _clearHistory(HttpSession userSession) {
        LOGGER.trace(
            "History cleared: " + _getStatesHistory(userSession).clear()
        );
    }
}
