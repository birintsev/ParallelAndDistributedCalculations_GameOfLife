package birintsev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LivingSpaceDTO {

    @JsonProperty(required = true, value = "livingSpace")
    private boolean[][] livingSpace;

    public LivingSpaceDTO(boolean[][] livingSpace) {
        this.livingSpace = livingSpace;
    }

    public int rows() {
        return livingSpace.length;
    }

    public int cols() {
        return livingSpace[0].length;
    }

    public boolean isAlive(int row, int col) {
        return livingSpace[row][col];
    }
}
