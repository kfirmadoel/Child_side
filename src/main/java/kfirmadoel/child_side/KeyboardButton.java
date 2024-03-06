package kfirmadoel.child_side;

import java.io.Serializable;

/**
 * KeyboardButton run program that...
 * @author USER | 30/01/2024
 */
public class KeyboardButton implements Serializable 
{
    private int keyCode;
    private buttonStatus status;
    public enum buttonStatus{PRESSED,REALESED,CLICKED}

    public KeyboardButton(int keyCode, buttonStatus status)
    {
        this.keyCode = keyCode;
        this.status = status;
    }

    public int getKeyCode()
    {
        return keyCode;
    }

    public buttonStatus getStatus()
    {
        return status;
    }

    @Override
    public String toString()
    {
        return "KeyboardButton{" + "keyCode=" + keyCode + ", status=" + status + '}';
    }
    
}