package kfirmadoel.child_side;

import java.util.ArrayList;

public class Connections {

    private Child child;
    private ArrayList<Connection> connectionsList;

    public Connections(Child child) {
        this.child = child;
        connectionsList = new ArrayList<Connection>();
    }

    public void addConnection(String serveripadress, int port) {
        synchronized (connectionsList) {
            connectionsList.add(new Connection(this, serveripadress, port));
        }
    }

    public void closeAllConnections() {
        synchronized (connectionsList) {
            for (int i = 0; i < connectionsList.size(); i++) {
                connectionsList.get(i).closePhotoConnection();
                connectionsList.get(i).closeMouseConnection();
                connectionsList.get(i).closeKeyBoardConnection();
                connectionsList.get(i).closeActionConnection();
            }
        }
        connectionsList = null;
    }

    public void shutdown() {
        closeAllConnections();
        child.shutdown();
    }

    public void closeConnection(Connection connection) {
        synchronized (connectionsList) {
            for (int i = 0; i < connectionsList.size(); i++) {
                if (connectionsList.get(i).equals(connection)) {
                    connectionsList.get(i).closePhotoConnection();
                    connectionsList.get(i).closeMouseConnection();
                    connectionsList.get(i).closeKeyBoardConnection();
                    connectionsList.get(i).closeActionConnection();
                    connectionsList.remove(i);
                    return;
                }
            }
        }
    }

}
