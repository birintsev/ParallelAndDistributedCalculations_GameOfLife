package birintsev;

import birintsev.dto.LivingSpaceDTO;
import birintsev.model.LivingSpace;
import birintsev.model.modelservices.GameOfLiveService;
import birintsev.userservices.DefaultUserService;
import birintsev.userservices.UserService;
import birintsev.view.GameOfLiveApplication;
import javafx.application.Application;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableSpringHttpSession
public class Lab5Application implements ApplicationRunner, WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Lab5Application.class, args);
    }

    public Lab5Application(@Value(value = "${server.port}") int serverPort) {
        System.setProperty("server.port", String.valueOf(serverPort));
    }

    @Override
    public void run(ApplicationArguments args) {
        Application.launch(GameOfLiveApplication.class, args.getSourceArgs());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(
            new Converter<LivingSpaceDTO, LivingSpace>() {
                @Override
                public LivingSpace convert(LivingSpaceDTO source) {
                    return new LivingSpace(source.getLivingSpace());
                }
            }
        );
        registry.addConverter(
            new Converter<LivingSpace, LivingSpaceDTO>() {
                @Override
                public LivingSpaceDTO convert(LivingSpace source) {
                    return new LivingSpaceDTO(LivingSpace.toBoolMatrix(source));
                }
            }
        );
    }

    @Bean
    public ExecutorService threadPoolExecutor(
        @Value(value = "${gameoflife.defaults.maxThreads}") int maxThreads
    ) {
        return Executors.newFixedThreadPool(
            maxThreads
        );
    }

    @Bean(name = "ParallelUserService")
    UserService parallelUserService(
        @Qualifier(value = "ParallelRowsGameOfLifeService")
        GameOfLiveService parallelRowsGameOfLifeService,
        LivingSpaceRandomizer livingSpaceRandomizer
    ) {
        return new DefaultUserService(
            parallelRowsGameOfLifeService,
            livingSpaceRandomizer
        );
    }

    @Bean(name = "SequentialUserService")
    UserService sequentialUserService(
        @Qualifier(value = "SequentialGameOfLiveService")
        GameOfLiveService sequentialGameOfLiveService,
        LivingSpaceRandomizer livingSpaceRandomizer
    ) {
        return new DefaultUserService(
            sequentialGameOfLiveService,
            livingSpaceRandomizer
        );
    }

    // todo configure sessions cleaning
    @Bean
    public MapSessionRepository sessionRepository(
        @Value("${gameoflife.defaults.sessiontimeout}")
        int defaultSessionTimeout
    ) {
        MapSessionRepository sessionRepository = new MapSessionRepository(
            new ConcurrentHashMap<>()
        );
        sessionRepository.setDefaultMaxInactiveInterval(defaultSessionTimeout);
        return sessionRepository;
    }

    @Bean
    public LivingSpaceRandomizer livingSpaceRandomizer(
        @Value(value = "${gameoflife.defaults.livingspace.cellsInRow}")
        int cellsInRowDefault
    ) {
        return () -> new LivingSpace(cellsInRowDefault, cellsInRowDefault);
    }
}
