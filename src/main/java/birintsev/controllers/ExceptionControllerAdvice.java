package birintsev.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ExceptionControllerAdvice.class
    );

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<String> handleInterruptedException(
        HttpServletRequest request,
        InterruptedException e
    ) {
        LOGGER.error(e.getMessage(), e);
        HttpHeaders headers = new HttpHeaders();
        headers.add(
            "Content-Type",
            MediaType.APPLICATION_JSON_VALUE
        );
        return new ResponseEntity<String>(
            "{\"message\": \"The calculation has been interrupted\"}",
            headers,
            HttpStatus.GONE
        );
    }

    @ExceptionHandler(ExecutionException.class)
    public ModelAndView handleExecutionException(
        ExecutionException request,
        InterruptedException e
    ) {
        LOGGER.error(e.getMessage());
        return new ModelAndView(
            "error",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


}
