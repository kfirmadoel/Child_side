package kfirmadoel.child_side;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * connection defines object that...
 * 
 * @author USER | 29/01/2024
 */
public class Connection {
    public static final int defaultHeight = 1080;
    public static final int defaultWidth = 1920;
    private Connections connections;
    private Socket photoSocket;
    private Socket mouseSocket;
    private Socket keyboardSocket;
    private Socket actionSocket;
    private boolean isScreenShared;
    // Attributes תכונות

    public Connection(Connections connections,String serveripadress, int port) {
        this.connections=connections;
        isScreenShared = false;
        openActionConnection(serveripadress, port);
        openKeyboardConnection(serveripadress, port);
        openMouseConnection(serveripadress, port);
        openPhotoConnection(serveripadress, port);
        // ip = actionSocket.getInetAddress().getHostAddress();
    }

    // Methoods פעולות
    public static void captureScreen(DataOutputStream dataOutputStream) {
        try {
            Thread.sleep(120);
            Robot r = new Robot();

            // Capture screenshot
            BufferedImage image = r.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

            // Convert BufferedImage to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byte[] imageData = byteArrayOutputStream.toByteArray();

            // Send the size of the image data
            dataOutputStream.writeInt(imageData.length);
            dataOutputStream.flush();
            // Send the image data
            dataOutputStream.write(imageData);
            dataOutputStream.flush();

            // System.out.println("Screenshot sent");
        } catch (InterruptedException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AWTException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("the parent closed the photo socket");
        }
    }

    private void shareScreen(DataOutputStream dataOutputStream) {
        while (photoSocket != null && !photoSocket.isClosed()) {
            if (isScreenShared) {
                captureScreen(dataOutputStream);

                try {
                    // Introduce a small delay to control the frame rate
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public void openPhotoConnection(String serveripadress, int port) {
        try {
            photoSocket = new Socket(serveripadress, port);
            System.out.println("parent connected to photo socket: " + photoSocket.getInetAddress());

            handlePhotoConnection();

        } catch (IOException e) {
            System.out.println("Parent exception: " + e.getMessage());
        }
    }

    private void handlePhotoConnection() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream out = null;
                try {
                    out = new DataOutputStream(photoSocket.getOutputStream());
                    shareScreen(out);
                } catch (IOException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        closeConnection();
                    }
                }
            }
        });
        thread.start();

    }

    public void closePhotoConnection() {
        try {
            if (photoSocket != null && !photoSocket.isClosed()) {
                photoSocket.close();
                System.out.println("Closed the photoSocket");
                photoSocket = null;
            }
        } catch (IOException ex) {
            Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openMouseConnection(String serveripadress, int port) {
        try {
            mouseSocket = new Socket(serveripadress, port);
            System.out.println("parent connected to mouse socket: " + mouseSocket.getInetAddress());

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    handleMouseConnection();
                }
            });
            thread.start();

        } catch (IOException e) {
            System.out.println("Parent exception: " + e.getMessage());
        }
    }

    private void handleMouseConnection() {
        System.out.println("handle the mouse connection");
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(mouseSocket.getInputStream());

            MouseOpetions recivedkey;
            // Get the default toolkit
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        // Get the screen size
            Dimension screenSize = toolkit.getScreenSize();
        
        // Get width and height
        int width = screenSize.width;
        int height = screenSize.height;
            while (mouseSocket != null && !mouseSocket.isClosed()) {
                recivedkey = (MouseOpetions) objectInputStream.readObject();
                System.out.println("got recivedkey of mouse");
                recivedkey.setHeight((int)(((double)(height/defaultHeight))*recivedkey.getHeight()));
                recivedkey.setWidth((int)(((double)(width/defaultWidth))*recivedkey.getWidth()));
                System.out.println(recivedkey.toString());
                executeMouseCommand(recivedkey);
            }
        } catch (IOException ex) {
            System.out.println("parent close the mouse connection");
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeMouseConnection();
        }

    }

    public void closeMouseConnection() {
        try {
            if (mouseSocket != null && !mouseSocket.isClosed()) {
                mouseSocket.close();
                System.out.println("Closed the mouseSocket");
                mouseSocket = null;
            }

        } catch (IOException ex) {
            Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openKeyboardConnection(String serveripadress, int port) {
        try {

            keyboardSocket = new Socket(serveripadress, port);
            System.out.println("parent connected to keyboard socket: " + keyboardSocket.getInetAddress());

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    hadleKeyboardConnection();
                }
            });
            thread.start();

        } catch (IOException e) {
            System.out.println("Parent exception: " + e.getMessage());
        } finally {
            closeMouseConnection();
        }
    }

    private void hadleKeyboardConnection() {
        System.out.println("handle the keyboard connection");
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(keyboardSocket.getInputStream());

            KeyboardButton recivedkey;

            while (keyboardSocket != null && !keyboardSocket.isClosed()) {
                recivedkey = (KeyboardButton) objectInputStream.readObject();
                System.out.println(recivedkey.toString());

                executeKeyboardCommand(recivedkey);
            }
        } catch (IOException ex) {
            System.out.println("parent close the keyboard connection");
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeKeyBoardConnection();
        }

    }

    public void closeKeyBoardConnection() {
        try {
            if (keyboardSocket != null && !keyboardSocket.isClosed()) {
                keyboardSocket.close();
                System.out.println("Closed the keyboardSocket");
                keyboardSocket = null;
            }

        } catch (IOException ex) {
            Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openActionConnection(String serveripadress, int port) {
        try {
            actionSocket = new Socket(serveripadress, port);
            System.out.println("parent connected to action socket: " + actionSocket.getInetAddress());

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    hadleActionConnection();
                }
            });
            thread.start();

        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            closeConnection();
        }

    }

    private void hadleActionConnection() {

        System.out.println("start to handle action connection");
        String action = null;
        DataInputStream actionInputStream = null;
        try {
            // Set up communication streams
            actionInputStream = new DataInputStream(actionSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (actionSocket != null && !actionSocket.isClosed()) {
            try {
                // Set up communication streams
                action = actionInputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                closeConnection();
            }

            System.out.println("enter the loop of the action handel");
            switch (action) {
                case "start screen share":
                    isScreenShared = true;
                    break;
                case "stop screen share":
                    isScreenShared = false;
                    break;
                case "shutdown":
                    shutdown();
                    System.out.println("shutdown");
                    break;
                // additional cases as needed
                default:
                    System.out.println("command dont support");
                    // code to be executed if none of the cases match
            }
            action = null;
        }
    }

    public void closeActionConnection() {
        try {
            if (actionSocket != null && !actionSocket.isClosed()) {
                actionSocket.close();
                System.out.println("Closed the actionSocket");
                actionSocket = null;
            }

        } catch (IOException ex) {
            Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void executeKeyboardCommand(KeyboardButton recivedkey) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Robot robot = new Robot();
                    if (recivedkey.getStatus() == KeyboardButton.buttonStatus.PRESSED) {
                        robot.keyPress(recivedkey.getKeyCode());
                        return;
                    } else if (recivedkey.getStatus() == KeyboardButton.buttonStatus.REALESED) {
                        robot.keyRelease(recivedkey.getKeyCode());
                        return;
                    }
                    // robot.keyPress(recivedkey.getKeyCode());
                    // robot.keyRelease(recivedkey.getKeyCode());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void executeMouseCommand(MouseOpetions key) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Robot robot = new Robot();
                    robot.mouseMove(key.getWidth(), key.getHeight());
                    if (key.getStatus() == MouseOpetions.mouseStatus.MOVED) {
                        return;
                    } else if (key.getStatus() == MouseOpetions.mouseStatus.PRESSED) {
                        robot.mousePress(key.getMask());
                        return;
                    } else if (key.getStatus() == MouseOpetions.mouseStatus.REALESED) {
                        robot.mouseRelease(key.getMask());
                        return;
                    }

                } catch (AWTException ex) {
                    Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    private void shutdown() {
        connections.shutdown();
    }

    private void closeConnection()
    {
        connections.closeConnection(this);
    }

}
