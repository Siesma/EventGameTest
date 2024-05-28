package board.wfc;

import org.joml.Vector2f;

enum AdjacencyRules {

    UP(new Vector2f(0, 1)),
    DOWN(new Vector2f(0, -1)),
    LEFT(new Vector2f(-1,0)),
    RIGHT(new Vector2f(1, 0));

    private Vector2f off;

    AdjacencyRules (Vector2f off) {
        this.off = off;
    }

    public Vector2f getOff() {
        return off;
    }
}
