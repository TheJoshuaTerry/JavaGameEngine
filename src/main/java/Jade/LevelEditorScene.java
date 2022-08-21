package Jade;

import Jade.util.Time;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import renderer.Shader;
import renderer.Texture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class LevelEditorScene extends Scene
{
    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray =
            {
                    // position                 // color                    // UV coordinates
                     100.5f,   -0.5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,     1, 0,  // Bottom Right = Red   is vertices 0
                      -0.5f,  100.5f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,     0, 1,  // Top Left = Green     is vertices 1
                     100.5f,  100.5f, 0.0f,     0.0f, 0.0f, 1.0f, 1.0f,     1, 1,  // Top Right = Blue     is vertices 2
                      -0.5f,   -0.5f, 0.0f,     1.0f, 0.0f, 1.0f, 1.0f,     0, 0   // Bottom Left = Purple is vertices 3
            };
    // IMPORTANT!!: Must be in counter-clockwise order
    private int[] elementArray =
            {
                    /*
                    *   x=1       x=2
                    *
                    *
                    *   x=3       x=0
                    */
                    2, 1, 0, // Top right triangle
                    0, 1, 3  // Bottom left triangle
            };
    private int vaoID, vboID, eboID;

    private Shader defaultShader;
    private Texture testTexture;

    public LevelEditorScene()
    {
    }

    @Override
    public void init()
    {
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();
        this.testTexture = new Texture("assets/images/testImage.jpg");

        /********************************************************************************
         *********** Generate VAO, VBO, and EBO bufferObjects and send to GPU ***********
         * vao = VertexArrayObject, vbo = VertexBufferObject, ebo = ElementBufferObject *
         ********************************************************************************/
        vaoID = glGenVertexArrays();    // Generate Vertex Array
        glBindVertexArray(vaoID);       // Bind Vertex Array

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload buffers
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);


        // Add the vertex attribute pointers
        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt)
    {
        camera.position.x -= dt * 50.f;
        camera.position.y -= dt * 20.0f;
        // Bind shader program
        defaultShader.use();

        // Upload texture to shader

        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());
        // Bind the VAO that we are using
        glBindVertexArray(vaoID);

        // Enable the Vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the triangles
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        defaultShader.detach();
    }
}
