package rendering;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import other.Vector2D;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundGenerator;
import sound.StartPositions;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    // The window handle
    private long window;

    private SoundAutomata board;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        try {
            loop();
        } catch (Exception e) {
        }

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    SoundGenerator soundEngine;

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(1200, 1200, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
        this.soundEngine = new SoundGenerator();
        this.board = new SoundAutomata(Settings.gridSize, Settings.gridSize);
    }

    private void loop() throws InterruptedException {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // Set background color to white

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            if(board.isDead()) {
                //board.loadInitialState(StartPositions.values()[(int) (Math.random() * StartPositions.values().length)], false, 17);
                break;
            }
            // Render the grid
            renderGrid();
//            Thread.sleep(10000);
            glfwSwapBuffers(window); // swap the color buffers
            soundEngine.playBoard(board);
            Thread.sleep(Settings.soundNoteDuration);
            board.step();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private void renderGrid() {
        int gridSize = board.getBoard().getWidth();
        float cellSize = 2.0f / gridSize;

        // Set color for alive cells (black)
        glColor3f(0.0f, 0.0f, 0.0f);

        // Draw grid cells
        glBegin(GL_QUADS);
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                if (board.getBoard().getState(x, y).isChecked()) { // Swap x and y
                    // Calculate cell position with inverted y-coordinate
                    float xPos = (x) * cellSize - 1.0f;
                    float yPos = (y) * cellSize - 1.0f; // Invert y-coordinate
                    Vector2D check = new Vector2D(x, y);
                    if (Settings.synthesizingMethod.cellsToPlay(board).contains(check)) {
                        // Render cells to play differently (e.g., change color)
                        glColor3f(1.0f, 0.0f, 0.0f); // Red color
                    } else {
                        // Render other cells normally (black color)
                        glColor3f(0.0f, 0.0f, 0.0f);
                    }

                    // Define vertices of the cell
                    glVertex2f(xPos, yPos);
                    glVertex2f(xPos + cellSize, yPos);
                    glVertex2f(xPos + cellSize, yPos + cellSize);
                    glVertex2f(xPos, yPos + cellSize);
                }
            }
        }
        glEnd();
    }

}