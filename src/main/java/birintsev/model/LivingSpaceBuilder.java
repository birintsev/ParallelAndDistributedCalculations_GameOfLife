package birintsev.model;

public class LivingSpaceBuilder {

    private boolean[][] space;

    public LivingSpaceBuilder(LivingSpace initialSpace) {
        basedOn(initialSpace);
    }

    public LivingSpaceBuilder(int rows, int cols) {
        this.space = new boolean[rows][cols];
    }

    public LivingSpaceBuilder with(int row, int col, boolean isAlive) {
        if (space == null) {
            throw new IllegalStateException("Dimension is not specified");
        }
        space[row][col] = isAlive;
        return this;
    }

    public LivingSpace build() {
        if (space == null) {
            throw new IllegalStateException("Nothing to build");
        }
        LivingSpace livingSpace = new LivingSpace(space);
        this.space = null;
        return livingSpace;
    }

    public LivingSpaceBuilder basedOn(LivingSpace livingSpace) {
        this.space = convert(livingSpace);
        return this;
    }

    private static boolean[][] convert(LivingSpace from) {
        boolean[][] space = new boolean[from.rows()][from.cols()];
        for (int i = 0; i < space.length; i++) {
            for (int j = 0; j < space[i].length; j++) {
                space[i][j] = from.isAlive(i, j);
            }
        }
        return space;
    }

    public boolean isAlive(int row, int col) {
        if (space == null) {
            throw new IllegalStateException("Dimension is not specified");
        }
        return space[row][col];
    }

    public int rows() {
        if (space == null) {
            throw new IllegalStateException("Dimension is not specified");
        }
        return space.length;
    }

    public int cols() {
        if (space == null) {
            throw new IllegalStateException("Dimension is not specified");
        }
        return space[0].length;
    }

    public boolean isEmpty() {
        return space == null;
    }
}
