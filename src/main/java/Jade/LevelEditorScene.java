package Jade;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15C;
import renderer.Shader;

import java.awt.event.KeyEvent;
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
                    // position             // color
                    0.5f, -0.5f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f, // Bottom Right = Red   is vertices 0
                    -0.5f, 0.5f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f, // Top Left = Green     is vertices 1
                    0.5f, 0.5f, 0.0f,       0.0f, 0.0f, 1.0f, 1.0f, // Top Right = Blue     is vertices 2
                    -0.5f, -0.5f, 0.0f,     1.0f, 0.0f, 1.0f, 1.0f, // Bottom Left = Purple is vertices 3
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

    public LevelEditorScene()
    {
    }

    @Override
    public void init()
    {
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compileAndLink();

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
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt)
    {
        // Bind shader program
        defaultShader.use();
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
