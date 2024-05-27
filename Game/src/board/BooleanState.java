package board;

import org.joml.Vector2i;
import sound.Settings;

public class BooleanState extends CellState<Boolean> {

    public BooleanState(Vector2i position, Boolean state) {
        super(position, state, new Vector2i(Settings.gridSize, Settings.gridSize));
    }

    @Override
    public void setState (Boolean state) {
        this.state = state;
    }

    @Override
    public Boolean getState() {
        return this.state;
    }

    @Override
    public boolean isChecked() {
        return this.state;
    }
}
