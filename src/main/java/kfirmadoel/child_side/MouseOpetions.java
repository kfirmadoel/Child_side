package kfirmadoel.child_side;


import java.io.Serializable;

/**
 * MouseOpetions defines object that...
 * @author USER | 31/01/2024
 */
public class MouseOpetions implements Serializable
{
    private int height;
    private int width;
    private mouseStatus status;
    private int mask;
    public enum mouseStatus{PRESSED,REALESED,MOVED};

    public MouseOpetions(int height, int width, mouseStatus status, int mask)
    {
        this.height = height;
        this.width = width;
        this.status = status;
        this.mask = mask;
    }
    
    
    // Attributes תכונות
    
    // Methoods פעולות

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public mouseStatus getStatus()
    {
        return status;
    }

    public int getMask()
    {
        return mask;
    }

    @Override
    public String toString()
    {
        return String.format("MouseOptions{height=%d, width=%d, status=%s, mask=%d}", height, width, status, mask);
    }


    public void setHeight(int height) {
        this.height = height;
    }


    public void setWidth(int width) {
        this.width = width;
    }

    
    
}
