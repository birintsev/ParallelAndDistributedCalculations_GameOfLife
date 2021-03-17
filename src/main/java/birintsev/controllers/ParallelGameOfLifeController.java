package birintsev.controllers;

import birintsev.LivingSpaceRandomizer;
import birintsev.dto.LivingSpaceDTO;
import birintsev.userservices.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping(path = "/gameOfLife/parallel")
public class ParallelGameOfLifeController extends AbstractGameOfLifeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ParallelGameOfLifeController.class
    );

    private final int serverPort;

    public ParallelGameOfLifeController(
        @Qualifier("ParallelUserService")
        UserService userService,
        ConversionService conversionService,
        @Value(value = "${server.port}") int serverPort,
        LivingSpaceRandomizer livingSpaceRandomizer
    ) {
        super(conversionService, userService, livingSpaceRandomizer);
        this.serverPort = serverPort;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/start")
    public ModelAndView start(
        @RequestParam(
            name = "cellsInRow",
            required = false,
            defaultValue = "${gameoflife.defaults.livingspace.cellsInRow}"
        )
        int cellsInRow,
        @RequestParam(
            name = "newGame",
            defaultValue = "false",
            required = false
        )
        boolean newGame,
        HttpSession userSession
    ) throws MalformedURLException, ExecutionException, InterruptedException {
        return _startDefault(
            cellsInRow,
            new URL(
                "http",
                "localhost",
                serverPort,
                "/gameOfLife/parallel/nextStep"
            ),
            new URL(
                "http",
                "localhost",
                serverPort,
                "/gameOfLife/parallel/cancel"
            ),
            userSession,
            newGame
        );
    }

    @RequestMapping(
        method = RequestMethod.GET,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE,
        path = "/nextStep"
    )
    public ResponseEntity<LivingSpaceDTO> nextStep(
        HttpSession userSession
    ) throws ExecutionException, InterruptedException {
        return _nextStepDefault(
            userSession
        );
    }

    @RequestMapping(
        method = RequestMethod.GET,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE,
        path = "/cancel"
    )
    public ResponseEntity<String> cancel(HttpSession httpSession) {
        return _cancelDefault(httpSession);
    }
}
