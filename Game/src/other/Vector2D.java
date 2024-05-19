package other;

public record Vector2D(int x, int y) implements Comparable<Vector2D>{
    @Override
    public int compareTo(Vector2D o) {
        if(this.x == o.x && this.y == o.y) {
            return 0;
        } else if (this.x > o.x && this.y > o.y) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString () {
        return String.format("(%s, %s)", x(), y());
    }

}
