package Jade;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    // Constructor for the mouse listener
    private MouseListener()
    {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    // Get mouse listener
    public static MouseListener get()
    {
        if (MouseListener.instance == null)
        {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    /*******************************************
     * Beginning of Callbacks for Mouse Inputs *
     *******************************************/
    public static void mousePosCallback(long window, double posX, double posY)
    {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = posX;
        get().yPos = posY;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods)
    {

        if (action == GLFW_PRESS)  // Check action to see if button is pressed
        {
            if (button < get().mouseButtonPressed.length)
            {
                get().mouseButtonPressed[button] = true;
            }
        }
        else if (action == GLFW_RELEASE) // Check action to see if button is released
        {
            if (button < get().mouseButtonPressed.length)
            {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset)
    {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() // Create end frame
    {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    /**********************************************
     * Beginning of get() methods for mouseInputs *
     **********************************************/
    public static float getX()
    {
        return (float)get().xPos;
    }

    public static float getY()
    {
        return (float)get().yPos;
    }

    public static float getDx()
    {
        return (float)(get().lastX - get().xPos);
    }

    public static float getDy()
    {
        return (float)(get().lastY - get().yPos);
    }

    public static float getScrollX()
    {
        return (float)get().scrollX;
    }

    public static float getScrollY()
    {
        return (float)get().scrollY;
    }

    /**************************************
     * Beginning of checks for mouseInputs *
     **************************************/
    public static boolean isDragging()
    {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button)
    {
        if (button < get().mouseButtonPressed.length)
        {
            return get().mouseButtonPressed[button];
        }
        else
        {
            return false;
        }

    }
}
