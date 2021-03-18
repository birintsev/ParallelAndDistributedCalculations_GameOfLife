package birintsev.controllers;

import birintsev.LivingSpaceRandomizer;
import birintsev.dto.LivingSpaceDTO;
import birintsev.model.LivingSpace;
import birintsev.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

abstract class AbstractGameOfLifeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        AbstractGameOfLifeController.class
    );

    protected static final String START_GAME_VIEW_NAME
        = "index";

    protected static final String LIVING_SPACE_VIEW_OBJECT_NAME
        = "livingSpace";

    protected static final String NEXT_STEP_URL_VIEW_OBJECT_NAME
        = "nextStepUrl";

    protected static final String CANCEL_URL_VIEW_OBJECT_NAME
        = "cancelUrl";

    protected final ConversionService conversionService;

    protected final UserService userService;

    protected final LivingSpaceRandomizer livingSpaceRandomizer;

    public AbstractGameOfLifeController(
        ConversionService conversionService,
        UserService userService,
        LivingSpaceRandomizer livingSpaceRandomizer
    ) {
        this.conversionService = conversionService;
        this.userService = userService;
        this.livingSpaceRandomizer = livingSpaceRandomizer;
    }

    protected ModelAndView _startDefault(
        int cellsInRow,
        URL nextStepUrl,
        URL cancelUrl,
        HttpSession userSession,
        boolean newGameRequestParam
    ) throws ExecutionException, InterruptedException {
        LivingSpaceDTO livingSpaceDTO;
        ModelAndView modelAndView = new ModelAndView(START_GAME_VIEW_NAME);
        boolean effectivelyNewGame =
            newGameRequestParam || shouldNewGameBeStarted(userSession);
        if (effectivelyNewGame) {
            _startNewGame(
                userSession,
                cellsInRow
            );
        }
        livingSpaceDTO = conversionService.convert(
            userService.getCurrent(userSession).get(),
            LivingSpaceDTO.class
        );
        modelAndView.addObject(
            LIVING_SPACE_VIEW_OBJECT_NAME,
            livingSpaceDTO
        );
        modelAndView.addObject(
            NEXT_STEP_URL_VIEW_OBJECT_NAME,
            nextStepUrl
        );
        modelAndView.addObject(
            CANCEL_URL_VIEW_OBJECT_NAME,
            cancelUrl
        );
        return modelAndView;
    }

    private void _startNewGame(
        HttpSession userSession,
        int cellsInRow
    ) {
        userService.startNewGame(
            userSession,
            () -> livingSpaceRandomizer.mockRandomWrapped(cellsInRow)
        );
    }

    protected Future<LivingSpace> _nextStep(
        HttpSession userSession
    ) throws ExecutionException, InterruptedException {
        return userService.nextStep(
            userSession
        );
    }

    private boolean shouldNewGameBeStarted(HttpSession userSession) {
        return userService.getCurrent(userSession) == null;
    }

    /**
     * Default handling behavior
     *
     * @return  if calculation finishes successfully
     *          (i.e. no exception occurred),
     *          this method returns the result (converted to DTO).
     *          If any error occurs, {@code null} is returned
     * */
    protected ResponseEntity<LivingSpaceDTO> _nextStepDefault(
        HttpSession userSession
    ) throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(
            conversionService.convert(
                _nextStep(
                    userSession
                ).get(),
                LivingSpaceDTO.class
            ),
            HttpStatus.OK
        );
    }

    /**
     * A default implementation of "/cancel" endpoint request handling
     * */
    protected ResponseEntity<String> _cancelDefault(HttpSession userSession) {
        ResponseEntity<String> responseEntity;
        if (userService.cancelCurrentCalculation(userSession)) {
            responseEntity = new ResponseEntity<>(
                HttpStatus.OK
            );
        } else {
            responseEntity = new ResponseEntity<>(
                "{\"message\":\"There is nothing to cancel\"}",
                HttpStatus.PRECONDITION_REQUIRED
            );
        }
        return responseEntity;
    }
}
