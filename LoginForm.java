package security;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Connection connection;

    public LoginForm() {
        setTitle("Login or Register");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(10);
        passwordField = new JPasswordField(10);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        add(panel);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "demo", "thanhtung12345@@");
            System.out.println("Đã kết nối với cơ sở dữ liệu thành công");
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Không thể kết nối với cơ sở dữ liệu!");
        }

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                authenticateUser(username, password);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                registerUser(username, password);
            }
        });
    }

    private void authenticateUser(String username, String password) {
        try {
            String query = "SELECT * FROM users.login WHERE `username`=? AND `password`=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, encryptPassword(password));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(LoginForm.this, "Welcome ");
            } else {
                JOptionPane.showMessageDialog(LoginForm.this, "Sai username hoặc password");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void registerUser(String username, String password) {
        try {
            String query = "INSERT INTO users.login (`username`, `password`) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, encryptPassword(password));
            statement.executeUpdate();
            JOptionPane.showMessageDialog(LoginForm.this, "Người dùng đã đăng ký thành công!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return password;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	LoginForm app = new LoginForm();
            app.setVisible(true);
        });
    }
}