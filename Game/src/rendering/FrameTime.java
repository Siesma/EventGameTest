package rendering;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import other.DoublyLinkedList;
import other.Node;
import other.math.MathHelper;

import static rendering.IsoWindow.windowSize;

public class FrameTime {


    private DoublyLinkedList<Double> frameTimes;
    private long frameCount;


    public FrameTime() {

        this.frameTimes = new DoublyLinkedList<>(100);

    }

    public void update(long now, long then) {
        this.frameCount++;


        double timeInMilliseconds = (double) (then - now) / 1_000_000.0;
        frameTimes.append(timeInMilliseconds);

        if (frameTimes.getSize() >= frameTimes.getMaxNumElements()) {
            frameTimes.deleteHead();
        }
        frameCount++;

    }

    public void render() {
        int frameTimeXSize = 100;
        int frameTimeYSize = 20;
        float x = windowSize.x() - frameTimeXSize - 5;

        RenderHelper.rectAA(new Vector2f(x, 0), new Vector2f(x, frameTimeYSize), new Vector4f(0.2f, 0.5f, 0, 0.4f));

        GL11.glPushMatrix();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glColor3f(1, 0, 0);
        int numElements = Math.min(frameTimes.getMaxNumElements(), frameTimes.getSize());
        Node<Double> cur = frameTimes.getHead();
        for (int i = 0; i < numElements; i++) {
            float val = cur.getData().floatValue();
            float y = MathHelper.map(val, 0, frameTimes.getLargest().floatValue(), 0, frameTimeYSize);
            GL11.glVertex2f(x, y);
            x += (float) frameTimeXSize / (float) frameTimes.getSize();
            cur = cur.getNext();
        }
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void printFrameTimeReport () {
        if (frameCount % frameTimes.getMaxNumElements() == 0) {
            System.out.printf("Average framerate from the last %s frames: %.5s%n", frameTimes.getMaxNumElements(), 1000 / frameTimes.getAverage());
        }
    }

}
