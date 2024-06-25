package rendering;

import board.wfc.TileCell;
import board.wfc.TileGrid;
import board.wfc.tiles.Forest;
import board.wfc.tiles.Grass;
import board.wfc.tiles.Ground;
import board.wfc.tiles.Water;
import event.EventBus;
import event.EventSubscriber;
import event.events.MousePressedEvent;
import event.events.MouseReleasedEvent;
import event.events.TimerEvent;
import game.Timer;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import other.Pair;
import wfc.Cell;
import wfc.Grid;
import wfc.WaveFunctionCollapse;
import wfc.pattern.Tiles;

import java.lang.Math;
import java.nio.DoubleBuffer;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static other.math.MathHelper.screenToIso;

public class IsoWindow implements Runnable {
    private long window;
    public static Vector2i windowSize = new Vector2i(2560, 1440);
    private static final Vector2d camera = new Vector2d(0, 0);
    private static final float CAMERA_SPEED = 50.0f;
    private Vector2f cursor;

    private TileGrid tiles;

    private Vector2i tileUnderCursor;

    private FrameTime frameTimes;

    private float zoomLevel;

    public static Vector2i tileSize;
    public static Vector2i gridDimension;
    private DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
    private DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);

    private boolean running;

    public IsoWindow() {
        this(new Vector2i(100, 100), (int) Math.pow(2, 8), new Vector2i(2560, 1440));
    }

    public IsoWindow(Vector2i gridDimension_, int size_, Vector2i windowSize_) {
        gridDimension = gridDimension_;
        tileSize = new Vector2i(size_, size_ >> 1);
        windowSize = windowSize_;
        this.running = true;
    }

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(windowSize.x(), windowSize.y(), "Isometric Rendering", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (action == GLFW_PRESS) {
                EventBus.raise(
                        new MousePressedEvent(
                                new Pair<String, Integer>("Button", button),
                                new Pair<String, Integer>("Action", action),
                                new Pair<String, Vector2f>("MousePosition", cursor),
                                new Pair<String, Vector2i>("TileUnderCursor", tileUnderCursor)
                        )
                );
            } else if (action == GLFW_RELEASE) {
                EventBus.raise(
                        new MouseReleasedEvent(
                                new Pair<String, Integer>("Button", button),
                                new Pair<String, Integer>("Action", action),
                                new Pair<String, Vector2f>("MousePosition", cursor),
                                new Pair<String, Vector2i>("TileUnderCursor", tileUnderCursor)
                        )
                );
            }
        });

        EventBus.register(this);

        this.zoomLevel = 0;
        glfwSetScrollCallback(window, (window, xOffset, yOffset) -> {
            if (yOffset > 0) {
                zoomLevel++;
            } else if (yOffset < 0) {
                zoomLevel--;
            }
            zoomLevel = Math.max(Math.min(zoomLevel, 100), 5);
        });


        Tiles.initDefaultNeighbouringCandidates();
        Tiles.registerTileCandidate(
                new Forest(),
                new Ground(),
                new Grass(),
                new Water()
        );
        Tiles.initAdjacencyOfAllTiles();


        // Initialize the tile grid
        tiles = new TileGrid(Tiles.allTiles(), () -> new TileCell[gridDimension.x()][gridDimension.y()]) {
        };


        this.frameTimes = new FrameTime();
        long start = System.currentTimeMillis();

        WaveFunctionCollapse wfc = new WaveFunctionCollapse() {
        };

        int tries = 1;

        wfc.init(tiles);

        while (!wfc.collapse()) {
            tiles.init();
            wfc.init(tiles);
            tries++;
        }
        long end = System.currentTimeMillis();

        System.out.printf("It took %s ms after %s tries\n", (end - start), tries);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, windowSize.x(), windowSize.y(), 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @EventSubscriber
    public void onTimerEvent(TimerEvent event) {

    }

    private void loop() {
        while ((!glfwWindowShouldClose(window))) {

            long now = System.nanoTime();

            renderFrame();

            long then = System.nanoTime();
            frameTimes.update(now, then);
        }
    }

    private void renderFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        cursor = getCursorPos(window);
        updateCamera();

        this.tileUnderCursor = computeTileUnderCursosr();

        renderGrid();

        frameTimes.render();

        glfwSwapBuffers(window);
        glfwPollEvents();


    }

    private Vector2i computeTileUnderCursosr() {

        float worldCursorX = cursor.x + (float) camera.x;
        float worldCursorY = cursor.y + (float) camera.y;

        Vector2f tileUnderCursor = screenToIso(worldCursorX, worldCursorY, tileSize.x(), tileSize.y());
        return new Vector2i((int) tileUnderCursor.x, (int) tileUnderCursor.y);
    }

    public void stop() {
        glfwSetWindowShouldClose(window, true);
    }

    private void updateCamera() {
        double moveSpeed = CAMERA_SPEED;

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.x -= moveSpeed;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.x += moveSpeed;
        }
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.y -= moveSpeed / 2;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.y += moveSpeed / 2;
        }
    }

    private void renderGrid() {
        glPushMatrix();
        glTranslated(-camera.x, -camera.y, 0);

        for (int x = 0; x < tiles.getWidth(); x++) {
            for (int y = 0; y < tiles.getHeight(); y++) {
                Vector2i tilePosition = new Vector2i(x, y);
                TileCell tile = (TileCell) tiles.getTileSafe(x, y);
                if (isTileVisible(tilePosition)) {
                    boolean highlight = x == tileUnderCursor.x && y == tileUnderCursor.y;
                    tile.renderCell(highlight);
                }
            }
        }

        glPopMatrix();
    }


    private boolean isTileVisible(Vector2i tilePosition) {
        int screenX = (int) (((tilePosition.x - tilePosition.y) * tileSize.x / 2) - camera.x);
        int screenY = (int) (((tilePosition.x + tilePosition.y) * tileSize.y / 2) - camera.y);

        return screenX + tileSize.x >= -windowSize.x / 2 && (screenX - tileSize.x) <= windowSize.x / 2 &&
                (screenY + tileSize.y) >= -windowSize.y / 2 && (screenY - tileSize.y) <= windowSize.y / 2;
    }

    private Vector2f getCursorPos(long windowID) {
        glfwGetCursorPos(windowID, xBuffer, yBuffer);
        return new Vector2f((float) xBuffer.get(0), (float) yBuffer.get(0));
    }

    public static void main(String[] args) {
        new IsoWindow().run();
    }
}
