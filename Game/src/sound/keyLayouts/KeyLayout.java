package sound.keyLayouts;

public interface KeyLayout {

    String getLayout();

    default String getKey(int x, int y) {
        String[] keySet = getLayout().split(" ");
        return keySet[(x + 2*y) % keySet.length];
    }

}
