package birintsev.model.modelservices;

import birintsev.model.LivingSpace;
import birintsev.model.LivingSpaceBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service(value = "SequentialGameOfLiveService")
@RequiredArgsConstructor
public class SequentialGameOfLiveService implements GameOfLiveService {

    private final LivingRuleProvider livingRuleProvider;

    private final ExecutorService executorService;

    @Override
    public Future<LivingSpace> nextStep(LivingSpace livingSpace) {
        return executorService.submit(
            () -> {
                LivingSpaceBuilder builder = new LivingSpaceBuilder(
                    livingSpace
                );

                for (int i = 0; i < builder.rows(); i++) {
                    for (int j = 0; j < builder.cols(); j++) {
                        builder.with(
                            i,
                            j,
                            livingRuleProvider.willCellBeAlive(
                                i,
                                j,
                                livingSpace
                            )
                        );
                    }
                }

                return builder.build();
            }
        );
    }
}
