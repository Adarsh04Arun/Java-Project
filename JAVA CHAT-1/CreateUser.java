import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class CreateUser extends JFrame implements ActionListener {

    private JTextField nameField;
    private JButton createButton;
    private JButton chooseColorButton;
    private Color selectedColor;

    public CreateUser() {
        setTitle("Create New User");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), new Color(200, 200, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("User Registration", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBackground(new Color(255, 255, 255, 180));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Enter Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        chooseColorButton = new JButton("Choose Theme Color");
        chooseColorButton.setFont(new Font("Arial", Font.BOLD, 16));
        chooseColorButton.setBackground(new Color(63, 81, 181));
        chooseColorButton.setForeground(Color.WHITE);
        chooseColorButton.setFocusPainted(false);
        chooseColorButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(chooseColorButton, gbc);

        createButton = new JButton("Create User");
        createButton.setFont(new Font("Arial", Font.BOLD, 16));
        createButton.setBackground(new Color(76, 175, 80));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(76, 175, 80), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        gbc.gridy = 2;
        formPanel.add(createButton, gbc);

        createButton.addActionListener(this);
        chooseColorButton.addActionListener(this);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton) {
            String username = nameField.getText().trim();
            if (!username.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    Client client = new Client("127.0.0.1", 6001, username, selectedColor);
                    client.connectToServer();
                });
                nameField.setText("");
            }
        } else if (e.getSource() == chooseColorButton) {
            selectedColor = JColorChooser.showDialog(this, "Choose Theme Color", selectedColor);
            if (selectedColor != null) {
                getContentPane().setBackground(selectedColor);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreateUser createUser = new CreateUser();
            createUser.setVisible(true);
        });
    }
}
