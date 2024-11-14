import java.net.*;
import java.util.Vector;
import java.io.*;
import java.sql.*;

public class Server implements Runnable {
    Socket socket;
    public static Vector<DataOutputStream> clients = new Vector<>();

    public Server(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            clients.add(dos);

            while (true) {
                String msgType = dis.readUTF(); // Type: "TEXT", "FILE", or "IMAGE"

                if (msgType.equals("TEXT")) {
                    String message = dis.readUTF();
                    System.out.println("Message Received: " + message);
                    broadcastMessage(message);
                } else if (msgType.equals("FILE") || msgType.equals("IMAGE")) {
                    String fileName = dis.readUTF();
                    long fileSize = dis.readLong();
                    byte[] fileContent = new byte[(int) fileSize];
                    dis.readFully(fileContent);

                    // Store metadata in the database
                    DatabaseHelper.saveFileMetadata(fileName, msgType, fileContent);

                    // Broadcast file info
                    broadcastMessage(msgType + ": " + fileName + " (" + fileSize + " bytes)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (DataOutputStream client : clients) {
            try {
                client.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(6001);

        while (true) {
            Socket socket = serverSocket.accept();
            Server server = new Server(socket);
            Thread thread = new Thread(server);
            thread.start();
        }
    }
}
