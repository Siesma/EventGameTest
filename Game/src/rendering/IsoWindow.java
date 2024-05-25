package rendering;

import board.*;
import event.EventBus;
import event.EventSubscriber;
import event.events.MousePressedEvent;
import event.events.MouseReleasedEvent;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import other.Pair;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class IsoWindow {
    private long window;
    public static final Vector2i windowSize = new Vector2i(800, 600);
    private static final Vector2d camera = new Vector2d(0, 0);
    private static final float CAMERA_SPEED = 5.0f;
    private Vector2f cursor;

    private Board<TileState> tiles;

    private Vector2i tileUnderCursor;

    public static Vector2i tileSize = new Vector2i(32, 16);

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
        // Initialize the tile grid
        int n = 30;
        int m = 30;
        tiles = new Board<>(n, m);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                TileState tileState = new TileState(new Vector2i(i, j), 0);
                tiles.getBoard()[i][j] = new Cell<>(tileState);
                EventBus.register(tileState);
            }
        }

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


    }


    private void loop() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, windowSize.x(), windowSize.y(), 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            cursor = getCursorPos(window);
            updateCamera();
            glLoadIdentity();
            glTranslated(-camera.x, -camera.y, 0);


            float worldCursorX = cursor.x + (float) camera.x;
            float worldCursorY = cursor.y + (float) camera.y;

            tileUnderCursor = screenToIso(worldCursorX, worldCursorY, tileSize.x(), tileSize.y());

            renderGrid();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void updateCamera() {
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.x -= CAMERA_SPEED;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.x += CAMERA_SPEED;
        }
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.y -= CAMERA_SPEED;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.y += CAMERA_SPEED;
        }
    }

    private void renderGrid() {
        for (int x = 0; x < tiles.getWidth(); x++) {
            for (int y = 0; y < tiles.getHeight(); y++) {
                Cell<TileState> tile = tiles.getBoard()[x][y];
                boolean highlight = x == tileUnderCursor.x && y == tileUnderCursor.y;
                tile.getState().render(highlight, windowSize);
            }
        }
    }

    private Vector2i screenToIso(float screenX, float screenY, int tileWidth, int tileHeight) {
        screenX -= windowSize.x() / 2.0f;
        screenY -= windowSize.y() / 2.0f;

        float isoX = (screenX / (tileWidth / 2.0f) + screenY / (tileHeight / 2.0f)) / 2.0f;
        float isoY = (screenY / (tileHeight / 2.0f) - screenX / (tileWidth / 2.0f)) / 2.0f;

        int tileX = (int) Math.floor(isoX);
        int tileY = (int) Math.floor(isoY);

        return new Vector2i(tileX, tileY+1);
    }

    private Vector2f getCursorPos(long windowID) {
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(windowID, xBuffer, yBuffer);
        return new Vector2f((float) xBuffer.get(0), (float) yBuffer.get(0));
    }

    public static void main(String[] args) {
        new IsoWindow().run();
    }
}
