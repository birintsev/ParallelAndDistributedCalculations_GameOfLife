package birintsev.model;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Represents a snapshot of the space where cells born, live and die
 *
 * This class is immutable
 * */
public class LivingSpace {

    private final boolean[][] space;

    public static boolean[][] toBoolMatrix(LivingSpace livingSpace) {
        return Arrays.stream(livingSpace.space)
            .map(boolean[]::clone)
            .collect(Collectors.toList())
            .toArray(new boolean[0][0]);
    }

    /**
     * Constructs a living space with specified initial cells allocation
     * */
    public LivingSpace(boolean[][] initCells) {
        this.space = initCells.clone();
    }

    @Override
    public LivingSpace clone() {
        return new LivingSpace(space.clone());
    }

    @Override
    public String toString() {
        return "LivingSpace{" +
            "space=" + Arrays.deepToString(space) +
            '}';
    }

    /**
     * Generates a living space with randomly allocated cells
     * */
    public LivingSpace(int rows, int cols) {
        this(randomCells(rows, cols));
    }

    public LivingSpace with(int row, int col, boolean isAlive) {
        boolean[][] spaceAfter = space.clone();
        spaceAfter[row][col] = isAlive;
        return new LivingSpace(spaceAfter);
    }

    public boolean isAlive(int row, int col) {
        return space[row][col];
    }

    public int rows() {
        return space.length;
    }

    public int cols() {
        return space[0].length;
    }

    private static boolean[][] randomCells(int rows, int cols) {
        Random random = new Random(System.currentTimeMillis());
        boolean[][] randomSpace = new boolean[rows][cols];
        for (int i = 0; i < rows; i++) {
            randomSpace[i] = new boolean[cols];
            for (int j = 0; j < cols; j++) {
                randomSpace[i][j] = random.nextInt() % 9 == 0;
            }
        }
        return randomSpace;
    }
}
