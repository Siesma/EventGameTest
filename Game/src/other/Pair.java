package other;

public record Pair(int x, int y) implements Comparable<Pair>{
    @Override
    public int compareTo(Pair o) {
        if(this.x == o.x && this.y == o.y) {
            return 0;
        } else if (this.x > o.x && this.y > o.y) {
            return 1;
        } else {
            return -1;
        }
    }
}
