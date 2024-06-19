package rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Locale;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;

public class TextureLoader {

    public static HashMap<String, Integer> nameToTextureIDMap = new HashMap<>();

    static {
        String path = "Game/ressources/wfc/tiles/";
        File folder = new File(path);
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                String fileName = file.getName();
                String tileName = fileName.substring(0, fileName.indexOf('.'));
                int textureID = loadTexture(file.getAbsolutePath());
                nameToTextureIDMap.put(tileName.toLowerCase(Locale.ROOT), textureID);
            }
        }
    }


    private static int loadTexture(String path) {
        // Load image data
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 0);
        if (image == null) {
            System.err.println("Failed to load texture file " + path);
            return -1;
        }

        // Generate texture ID
        int textureID = glGenTextures();
        System.out.printf("Loaded texture: %s (width: %d, height: %d, channels: %d) as ID: \"%s\"%n", path, width.get(0), height.get(0), channels.get(0), textureID);
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Determine the image format
        int format;
        if (channels.get(0) == 3) {
            format = GL_RGB;
        } else if (channels.get(0) == 4) {
            format = GL_RGBA;
        } else {
            System.err.println("Unsupported number of channels: " + channels.get(0));
            STBImage.stbi_image_free(image);
            return -1;
        }

        // Upload the texture data
        glTexImage2D(GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format, GL_UNSIGNED_BYTE, image);

        // Generate mipmaps
        glGenerateMipmap(GL_TEXTURE_2D);

        // Free the image memory
        STBImage.stbi_image_free(image);

        return textureID;
    }

}
