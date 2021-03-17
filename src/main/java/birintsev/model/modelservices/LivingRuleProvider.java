package birintsev.model.modelservices;

import birintsev.model.LivingSpace;

public interface LivingRuleProvider {

    boolean willCellBeAlive(
        int row,
        int col,
        LivingSpace livingSpace
    );
}
