import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client implements ActionListener, Runnable {
    private final String serverAddress;
    private final int serverPort;
    public final String username;
    public final Color themeColor;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private JFrame frame;
    private JPanel textArea;
    private JTextField msgField, imageField;
    private JButton sendButton, sendFileButton, sendImageButton;
    private Box vertical;

    public Client(String serverAddress, int serverPort, String username, Color themeColor) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
        this.themeColor = themeColor;

        initializeGUI(username);
    }

    private void initializeGUI(String username) {
        frame = new JFrame(username);
        textArea = new JPanel();
        msgField = new JTextField();
        imageField = new JTextField();
        sendButton = new JButton("Send Text");
        sendFileButton = new JButton("Send File");
        sendImageButton = new JButton("Send Image");
        vertical = Box.createVerticalBox();

        // Set up the frame
        frame.setLayout(new BorderLayout());
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(themeColor); // Set theme color as the background

        // Customize the text area
        textArea.setLayout(new BoxLayout(textArea, BoxLayout.Y_AXIS));
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Style input fields and buttons
        msgField.setPreferredSize(new Dimension(300, 30));
        msgField.setFont(new Font("Arial", Font.PLAIN, 16));
        msgField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        imageField.setPreferredSize(new Dimension(300, 30));
        imageField.setFont(new Font("Arial", Font.PLAIN, 16));
        imageField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        imageField.setToolTipText("Paste an image URL or file path here");

        sendButton.setBackground(new Color(76, 175, 80));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);

        sendFileButton.setBackground(new Color(33, 150, 243));
        sendFileButton.setForeground(Color.WHITE);
        sendFileButton.setFocusPainted(false);

        sendImageButton.setBackground(new Color(244, 67, 54));
        sendImageButton.setForeground(Color.WHITE);
        sendImageButton.setFocusPainted(false);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(themeColor);
        inputPanel.add(new JLabel("Message:"));
        inputPanel.add(msgField);
        inputPanel.add(sendButton);

        JPanel imagePanel = new JPanel(new FlowLayout());
        imagePanel.setBackground(themeColor);
        imagePanel.add(new JLabel("Image Path/URL:"));
        imagePanel.add(imageField);
        imagePanel.add(sendImageButton);
        imagePanel.add(sendFileButton);  // Add the sendFileButton

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(imagePanel, BorderLayout.SOUTH);

        sendButton.addActionListener(this);
        sendFileButton.addActionListener(this);
        sendImageButton.addActionListener(this);

        frame.setVisible(true);
    }

    public void connectToServer() {
        try {
            socket = new Socket(serverAddress, serverPort);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            Thread thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFileOrImage(String type, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] fileContent = fis.readAllBytes();

                    dos.writeUTF(type);
                    dos.writeUTF(file.getName());
                    dos.writeLong(fileContent.length);
                    dos.write(fileContent);

                    System.out.println(type + " Sent: " + file.getName());
                }
            } else {
                System.out.println("File does not exist: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send files through file chooser
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            sendFileOrImage("FILE", file.getAbsolutePath());
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == sendButton) {
                String message = msgField.getText();
                dos.writeUTF("TEXT");
                dos.writeUTF(username + ": " + message);
                msgField.setText("");
            } else if (e.getSource() == sendImageButton) {
                String imagePath = imageField.getText();
                sendFileOrImage("IMAGE", imagePath);
                imageField.setText("");
            } else if (e.getSource() == sendFileButton) {
                sendFile();  // Trigger the sendFile method to allow file selection
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                String receivedMessage = dis.readUTF();

                // Add timestamp to received messages
                String timestamp = new SimpleDateFormat("HH:mm").format(new Date());
                JLabel label = new JLabel("<html><b>" + receivedMessage + "</b> <span style='color:gray;'>[" + timestamp + "]</span></html>");
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                label.setOpaque(true);
                label.setBackground(new Color(220, 248, 198));
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                vertical.add(Box.createVerticalStrut(10)); // Add spacing between messages
                vertical.add(label);
                textArea.add(vertical);
                textArea.revalidate();
                textArea.repaint();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String serverAddress = "127.0.0.1"; // Localhost
            int serverPort = 6001;             // Port number of the server
            String username = "User1";         // A sample username
            Color themeColor = new Color(63, 81, 181); // Theme color for the GUI

            Client client = new Client(serverAddress, serverPort, username, themeColor);
            client.connectToServer();
        });
    }
}
