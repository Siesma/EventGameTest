package sound;

import other.math.Vector2D;

import java.util.ArrayList;

public enum StartPositions {
    GLIDER("(7,8)(8,9)(9,9)(9,8)(9,7)", new Vector2D(0, 0)),
    TABLE("(0,0)(0,1)(1,0)(2,0)(3,0)(3,1)", new Vector2D(5,7)),

    OSCILLATOR("(1,1)(1,2)(1,3)", new Vector2D(0,0)),
    LOAFER("(0,1)(0,2)(0,5)(0,7)(0,8)(1,0)(1,3)(1,6)(1,7)(2,1)(2,3)(3,2)(4,8)(5,6)(5,7)(5,8)(6,5)(7,6)(8,7)(8,8)", new Vector2D(4,4));


    private final String content;
    private final Vector2D offsetToCenter;
    StartPositions(String content, Vector2D offsetToCenter) {
        this.content = content;
        this.offsetToCenter = offsetToCenter;
    }

    public Vector2D getOffsetToCenter() {
        return offsetToCenter;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<Vector2D> getCoordinatePairs() {
        String[] cells = getContent().split("\\)\\(");
        ArrayList<Vector2D> vector2DS = new ArrayList<>();
        for (String cell : cells) {
            String[] xy = cell.replaceAll("[\\(\\)]", "").split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            vector2DS.add(new Vector2D(x, y));
        }
        return vector2DS;
    }

    public Vector2D calculateRequiredOffset(int gridSize) {
        // Calculate the sum of x and y coordinates
        int sumX = 0;
        int sumY = 0;
        ArrayList<Vector2D> coords = getCoordinatePairs();
        for (Vector2D vector2D : coords) {
            sumX += vector2D.x();
            sumY += vector2D.y();
        }

        // Calculate the average of x and y coordinates
        double avgX = (double) sumX / coords.size();
        double avgY = (double) sumY / coords.size();

        // Calculate the x and y offsets to center around (8, 8)
        int offsetX = (int) ((gridSize / 2) - avgX);
        int offsetY = (int) ((gridSize / 2) - avgY);

        return new Vector2D(offsetX, offsetY);
    }
}
