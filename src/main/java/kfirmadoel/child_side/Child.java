package kfirmadoel.child_side;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import org.springframework.stereotype.Component;

import java.net.NetworkInterface;

/**
 * ChildJava run program that...
 * 
 * @author USER | 29/01/2024
 */
@Component
public class Child {
    private static final String SERVERIPADRESS = "192.168.211.24";
    private static final int MAIN_PORT_OF_SERVER = 54321;
    private Socket mainActionSocket;
    private int port;
    private Connections connections;

    public Child() {
        connections = new Connections(this);
        connectToServer();
    }

    public void connectToServer() {
        try {
            Socket connectionSocket = new Socket(SERVERIPADRESS, MAIN_PORT_OF_SERVER);
            System.out.println("connected to server");
            DataInput in = new DataInputStream(connectionSocket.getInputStream());
            int port = in.readInt();
            this.port = port;
            connectionSocket.close();
            Socket socket = new Socket(SERVERIPADRESS, port);
            this.mainActionSocket = socket;
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
            byte[] mac = networkInterface.getHardwareAddress();
            System.out.print("MAC address: ");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            objectOutputStream
                    .writeObject(new ChildInfo(stringBuilder.toString(), InetAddress.getLocalHost().getHostAddress()));

            waitToCommendFromServer();
        } catch (IOException e) {
            System.out.printf("Parent exception: %s%n", e.getMessage());
        } finally {
            // closeServerConnection();
        }
    }

    private void waitToCommendFromServer() {

        String message;
        try (InputStream inputStream = mainActionSocket.getInputStream()) {
            // Wrap the input stream in a BufferedReader for convenient reading
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (mainActionSocket != null && !mainActionSocket.isClosed()) {
                message = reader.readLine();
                System.out.printf("Received message: %s%n", message);
                handleCommand(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(String message) {
        Pattern pattern = Pattern.compile("^\\(([^)]+)\\) \\(([^)]+)\\) wants to get access to your computer$");

        Matcher m = pattern.matcher(message);


        if (m.find()) {

            String name = m.group(1);
            String macAddress = m.group(2);
            System.out.println("Name: " + name);
            System.out.println("MAC Address: " + macAddress);

            openConnectionRequest(name,macAddress);
        }
        switch (message) {
            case "open new connection":
                openNewConnection();
                break;
            default:
                break;
        }
    }

    private void openConnectionRequest(String name,String macAddress) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                JFrame frame=new JFrame();
                JLabel title = new JLabel();
                JLabel message = new JLabel();
                JButton acceptButton = new JButton();
                JButton declineButton = new JButton();
        
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
                title.setFont(new Font("Arial", 1, 24)); // NOI18N
                title.setText("REQUEST TO CONNECT");
        
                message.setFont(new Font("Arial", 1, 18)); // NOI18N
                message.setText(name + " wants you to get access to your computer");
        
                acceptButton.setFont(new Font("Arial", 1, 14)); // NOI18N
                acceptButton.setForeground(new Color(51, 204, 0));
                acceptButton.setText("accept");
                acceptButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        acceptButtonActionPerformed(macAddress);
                        frame.dispose();
                    }

                    
                });
        
                declineButton.setFont(new Font("Arial", 1, 14)); // NOI18N
                declineButton.setForeground(new Color(255, 0, 0));
                declineButton.setText("decline");
                declineButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        frame.dispose();
                    }
                });
                
                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(frame);
                frame.setLayout(layout);
                layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(acceptButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(declineButton)
                        .addGap(56, 56, 56))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(message)
                        .addContainerGap(28, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(title)
                        .addGap(129, 129, 129))
                );
                layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(title)
                        .addGap(30, 30, 30)
                        .addComponent(message)
                        .addGap(88, 88, 88)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(acceptButton)
                            .addComponent(declineButton))
                        .addContainerGap(99, Short.MAX_VALUE))
                );
                frame.pack();
                frame.setVisible(true);
            }
        });
        thread.start();
    }

    private void openNewConnection() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connections.addConnection(SERVERIPADRESS, port);
            }
        });
        thread.start();
    }

    public void shutdown() {
        closeMainActionSocket();
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("shutdown -s -t 0");
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }

    private void closeMainActionSocket() {
        try {

            if (mainActionSocket != null && !mainActionSocket.isClosed()) {
                mainActionSocket.close();
                System.out.println("Closed the main Action Socket");
            }
        } catch (IOException ex) {
            Logger.getLogger(Child.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void acceptButtonActionPerformed(String macAddress) {
        try {

            // Get the output stream of the socket
            OutputStream outputStream = mainActionSocket.getOutputStream();

            // Create a PrintWriter to write string data to the output stream
            PrintWriter out = new PrintWriter(outputStream, true);

            // Write the string to the socket
            out.println("accept ("+macAddress+")");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
