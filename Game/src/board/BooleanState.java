package board;

public class BooleanState implements CellState<Boolean> {

    private Boolean state;
    public BooleanState(Boolean state) {
        this.state = state;
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
