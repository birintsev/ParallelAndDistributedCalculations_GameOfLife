package birintsev;

import birintsev.model.LivingSpace;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This is just an util class
 * that prevents code duplication
 * of creating random LivingSpace instances
 * */
public interface LivingSpaceRandomizer {

    default LivingSpace mockRandom(int cellsInRow) {
        return new LivingSpace(cellsInRow, cellsInRow);
    }

    default Future<LivingSpace> mockRandomWrapped(int cellsInRow) {
        return new CompletedFuture<>(mockRandom(cellsInRow));
    }

    LivingSpace mockRandomWithDefaultSize();

    default Future<LivingSpace> mockRandomWrappedWithDefaultSize() {
        return new CompletedFuture<>(mockRandomWithDefaultSize());
    }

    /**
     * This is a class for reducing code duplication above.
     * It is expected to be used internally only.
     * */
    final class CompletedFuture<T> implements Future<T> {

        private final T item;

        private CompletedFuture(T item) {
            this.item = item;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public T get() {
            return item;
        }

        @Override
        public T get(long timeout, TimeUnit unit) {
            return get();
        }
    }
}
