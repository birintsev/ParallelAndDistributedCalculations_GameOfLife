package birintsev.model.services;

import birintsev.model.LivingSpace;
import birintsev.model.LivingSpaceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service(value = "ParallelRowsGameOfLifeService")
public class ParallelRowsGameOfLifeService implements GameOfLiveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ParallelRowsGameOfLifeService.class
    );

    private final ExecutorService executorService;

    private final LivingRuleProvider livingRuleProvider;

    public ParallelRowsGameOfLifeService(
        ExecutorService executorService,
        LivingRuleProvider livingRuleProvider
    ) {
        this.executorService = executorService;
        this.livingRuleProvider = livingRuleProvider;
    }

    @Override
    public Future<LivingSpace> nextStep(LivingSpace livingSpace)
        throws ExecutionException, InterruptedException
    {
        LivingSpaceBuilder builder = new LivingSpaceBuilder(livingSpace);
        Set<Future<?>> taskSet = new HashSet<>();

        for (int i = 0; i < builder.rows(); i++) {
            ProcessRowTask task = new ProcessRowTask(
                livingSpace,
                builder,
                i,
                livingRuleProvider
            );
            taskSet.add(
                executorService.submit(
                    task
                )
            );
        }

        return new ParallelLivingSpaceFuture(taskSet, builder);
    }

    private static class ProcessRowTask extends Thread {

        private final LivingSpace prevLivingSpace;

        private volatile LivingSpaceBuilder builder;

        private final int rowToProcess;

        private final LivingRuleProvider livingRuleProvider;

        public ProcessRowTask(
            LivingSpace prevLivingSpace,
            LivingSpaceBuilder builder,
            int rowToProcess,
            LivingRuleProvider livingRuleProvider
        ) {
            this.prevLivingSpace = prevLivingSpace;
            this.builder = builder;
            this.rowToProcess = rowToProcess;
            this.livingRuleProvider = livingRuleProvider;
        }

        @Override
        public void run() {
            for (
                int columnIndex = 0;
                columnIndex < prevLivingSpace.cols();
                columnIndex++
            ) {
                builder.with(
                    rowToProcess,
                    columnIndex,
                    livingRuleProvider.willCellBeAlive(
                        rowToProcess,
                        columnIndex,
                        prevLivingSpace
                    )
                );
            }
        }
    }

    private static class ParallelLivingSpaceFuture
    implements Future<LivingSpace>, Serializable {

        private final Collection<Future<?>> taskSet;

        private final LivingSpaceBuilder builder;

        private LivingSpace nextStep;

        private ParallelLivingSpaceFuture(
            Collection<Future<?>> taskSet,
            LivingSpaceBuilder builder
        ) {
            this.taskSet = taskSet;
            this.builder = builder;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            boolean cancelled = true;
            for (Future<?> future : taskSet) {
                cancelled = cancelled
                    && future.cancel(mayInterruptIfRunning);
            }
            return cancelled;
        }

        @Override
        public boolean isCancelled() {
            return taskSet.stream().allMatch(Future::isCancelled);
        }

        @Override
        public boolean isDone() {
            return taskSet.stream().allMatch(Future::isDone);
        }

        @Override
        public LivingSpace get()
            throws InterruptedException, ExecutionException
        {
            for (Future<?> future : taskSet) {
                future.get();
            }
            if (nextStep == null) {
                nextStep = builder.build();
            }
            return nextStep;
        }

        @Override
        public LivingSpace get(long timeout, TimeUnit unit)
            throws InterruptedException,
            ExecutionException, TimeoutException
        {
            for (Future<?> future : taskSet) {
                future.get(timeout, unit);
            }
            if (nextStep == null) {
                nextStep = builder.build();
            }
            return nextStep;
        }
    }
}
