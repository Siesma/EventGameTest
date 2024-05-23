package rendering;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class IsoWindow {
    private long window;
    private static final Vector2i windowSize = new Vector2i(800, 600);
    private static final Vector2d camera = new Vector2d(0, 0);
    private static final float CAMERA_SPEED = 5.0f;
    private Vector2f cursor;

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
        int n = 30;
        int m = 30;
        int tileWidth = 32;
        int tileHeight = 16;

        for (int x = 0; x < n; x++) {
            for (int y = 0; y < m; y++) {
                float screenX = (x - y) * (tileWidth / 2.0f) + windowSize.x / 2.0f;
                float screenY = (x + y) * (tileHeight / 2.0f) + windowSize.y / 2.0f;
                renderTile(screenX, screenY, tileWidth, tileHeight);
            }
        }
    }

    private void renderTile(float screenX, float screenY, int tileWidth, int tileHeight) {
        glPushMatrix();
        glTranslatef(screenX, screenY, 0);

        float mag = new Vector2f(screenX, screenY).distance(cursor);
        glColor3f(1.0f, map(mag, 0, 400, 0, 1), 1.0f);

        glBegin(GL_QUADS);
        glVertex2f(0, 0);
        glVertex2f(tileWidth / 2.0f, tileHeight / 2.0f);
        glVertex2f(tileWidth, 0);
        glVertex2f(tileWidth / 2.0f, -tileHeight / 2.0f);
        glEnd();

        glPopMatrix();
    }

    private static float map(float value, float istart, float istop, float ostart, float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
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
