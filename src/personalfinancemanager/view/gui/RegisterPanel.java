package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import personalfinancemanager.auth.AuthManager;
import personalfinancemanager.auth.RegistrationResult;

public class RegisterPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthManager authManager;

    public RegisterPanel(MainFrame mainFrame, AuthManager authManager) {
        this.mainFrame = mainFrame;
        this.authManager = authManager;

        setBackground(GuiFactory.COLOR_LIGHT);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create a centered card panel
        JPanel cardPanel = GuiFactory.createCardPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(400, 400));
        cardPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = GuiFactory.createTitleLabel("Create New Account");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Join Personal Finance Tracker");
        subtitleLabel.setFont(GuiFactory.FONT_SUBHEADING);
        subtitleLabel.setForeground(GuiFactory.COLOR_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createVerticalStrut(20));

        // Username field
        JLabel usernameLabel = GuiFactory.createLabel("Choose Username:");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField = GuiFactory.createTextField(25);
        usernameField.setMaximumSize(new Dimension(350, 35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(usernameLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(usernameField);
        cardPanel.add(Box.createVerticalStrut(15));

        // Password field
        JLabel passwordLabel = GuiFactory.createLabel("Choose Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = GuiFactory.createPasswordField(25);
        passwordField.setMaximumSize(new Dimension(350, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(passwordLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(20));

        // Register button
        JButton registerButton = GuiFactory.createButton("Register", GuiFactory.COLOR_SUCCESS);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(registerButton);

        // Back to login link
        cardPanel.add(Box.createVerticalStrut(15));
        JButton backToLoginButton = new JButton("Already have an account? Login");
        backToLoginButton.setFont(GuiFactory.FONT_LABEL);
        backToLoginButton.setForeground(GuiFactory.COLOR_PRIMARY);
        backToLoginButton.setContentAreaFilled(false);
        backToLoginButton.setBorderPainted(false);
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(backToLoginButton);

        // Add padding around the card
        JPanel paddingPanel = new JPanel(new GridBagLayout());
        paddingPanel.setBackground(GuiFactory.COLOR_LIGHT);
        GridBagConstraints gbcPadding = new GridBagConstraints();
        gbcPadding.gridx = 0;
        gbcPadding.gridy = 0;
        gbcPadding.insets = new Insets(50, 20, 50, 20);
        paddingPanel.add(cardPanel, gbcPadding);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(paddingPanel, gbc);

        // Action Listeners
        registerButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (username.length() < 3) {
                JOptionPane.showMessageDialog(this, "Username must be at least 3 characters long.", 
                    "Invalid Username", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", 
                    "Weak Password", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            RegistrationResult result = authManager.register(username, password);
            switch (result) {
                case SUCCESS:
                    JOptionPane.showMessageDialog(this, 
                        "Registration successful! Please log in.", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.switchToPanel("LOGIN");
                    break;
                case USER_ALREADY_EXISTS:
                    JOptionPane.showMessageDialog(this, 
                        "This username is already taken. Please choose another.", 
                        "Registration Failed", JOptionPane.WARNING_MESSAGE);
                    break;
                case DATABASE_ERROR:
                    JOptionPane.showMessageDialog(this, 
                        "Could not connect to the database. Please check your connection and configuration.", 
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        });

        backToLoginButton.addActionListener(e -> mainFrame.switchToPanel("LOGIN"));
    }
}