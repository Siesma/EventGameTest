package sound;

import game.math.Vector2I;
import other.Pair;

import java.util.ArrayList;

public enum StartPositions {
    GLIDER("(7,8)(8,9)(9,9)(9,8)(9,7)", new Pair(0, 0)),
    LOAFER("(0,1)(0,2)(0,5)(0,7)(0,8)(1,0)(1,3)(1,6)(1,7)(2,1)(2,3)(3,2)(4,8)(5,6)(5,7)(5,8)(6,5)(7,6)(8,7)(8,8)", new Pair(4,4));


    private final String content;
    private final Pair offsetToCenter;
    StartPositions(String content, Pair offsetToCenter) {
        this.content = content;
        this.offsetToCenter = offsetToCenter;
    }

    public Pair getOffsetToCenter() {
        return offsetToCenter;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<Pair> getCoordinatePairs() {
        String[] cells = getContent().split("\\)\\(");
        ArrayList<Pair> pairs = new ArrayList<>();
        for (String cell : cells) {
            String[] xy = cell.replaceAll("[\\(\\)]", "").split(",");
            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);
            pairs.add(new Pair(x, y));
        }
        return pairs;
    }

    public Pair calculateRequiredOffset(int gridSize) {
        // Calculate the sum of x and y coordinates
        int sumX = 0;
        int sumY = 0;
        ArrayList<Pair> coords = getCoordinatePairs();
        for (Pair pair : coords) {
            sumX += pair.x();
            sumY += pair.y();
        }

        // Calculate the average of x and y coordinates
        double avgX = (double) sumX / coords.size();
        double avgY = (double) sumY / coords.size();

        // Calculate the x and y offsets to center around (8, 8)
        int offsetX = (int) ((gridSize / 2) - avgX);
        int offsetY = (int) ((gridSize / 2) - avgY);

        return new Pair(offsetX, offsetY);
    }
}
