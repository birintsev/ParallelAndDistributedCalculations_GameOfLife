package birintsev.model.modelservices;

import birintsev.model.LivingSpace;
import org.springframework.stereotype.Component;

@Component
public class DefaultLivingRuleProvider implements LivingRuleProvider {

    @Override
    public boolean willCellBeAlive(
        int row,
        int col,
        LivingSpace livingSpace
    ) {
        int aliveNeighbours = countAliveNeighbours(row, col, livingSpace);
        if (aliveNeighbours == 2) {
            return livingSpace.isAlive(row, col);
        }
        if (aliveNeighbours == 3) {
            return true;
        }
        return false; // aliveNeighbours < 2 || aliveNeighbours > 3
    }

    private int countAliveNeighbours(
        int row,
        int col,
        LivingSpace livingSpace
    ) {
        final int[] rowsIndexes = new int[] {
            row - 1,    row - 1,    row - 1,
            row,   /* curr elem */  row,
            row + 1,    row + 1,    row + 1
        };
        final int[] colsIndexes = new int[] {
            col - 1,    col,         col + 1,
            col - 1, /* curr elem */ col + 1,
            col - 1,    col,         col + 1,
        };

        int aliveNeighboursCounter = 0;

        for (int i = 0; i < 8; i++) {
            if (!validIndexes(rowsIndexes[i], colsIndexes[i], livingSpace)) {
                continue;
            }
            if (livingSpace.isAlive(rowsIndexes[i], colsIndexes[i])) {
                aliveNeighboursCounter++;
            }
        }

        return aliveNeighboursCounter;
    }

    private boolean validIndexes(
        int row,
        int col,
        LivingSpace livingSpace
    ) {
        if (row < 0 || row >= livingSpace.rows()) {
            return false;
        }
        if (col < 0 || col >= livingSpace.cols()) {
            return false;
        }
        return true;
    }
}
