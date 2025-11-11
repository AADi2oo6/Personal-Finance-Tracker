package personalfinancemanager.view.gui;

import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import personalfinancemanager.auth.AuthManager;
import personalfinancemanager.auth.Session;
import personalfinancemanager.models.User;

public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AuthManager authManager;

    public LoginPanel(MainFrame mainFrame, AuthManager authManager) {
        this.mainFrame = mainFrame;
        this.authManager = authManager;

        setBackground(GuiFactory.COLOR_LIGHT);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create a centered card panel
        JPanel cardPanel = GuiFactory.createCardPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(400, 350));
        cardPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = GuiFactory.createTitleLabel("Personal Finance Tracker");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Login to your account");
        subtitleLabel.setFont(GuiFactory.FONT_SUBHEADING);
        subtitleLabel.setForeground(GuiFactory.COLOR_GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createVerticalStrut(20));

        // Username field
        JLabel usernameLabel = GuiFactory.createLabel("Username:");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField = GuiFactory.createTextField(25);
        usernameField.setMaximumSize(new Dimension(350, 35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(usernameLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(usernameField);
        cardPanel.add(Box.createVerticalStrut(15));

        // Password field
        JLabel passwordLabel = GuiFactory.createLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = GuiFactory.createPasswordField(25);
        passwordField.setMaximumSize(new Dimension(350, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(passwordLabel);
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(passwordField);
        cardPanel.add(Box.createVerticalStrut(20));

        // Login button
        JButton loginButton = GuiFactory.createButton("Login", GuiFactory.COLOR_PRIMARY);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(loginButton);

        // Register link
        cardPanel.add(Box.createVerticalStrut(15));
        JButton registerButton = new JButton("Don't have an account? Register");
        registerButton.setFont(GuiFactory.FONT_LABEL);
        registerButton.setForeground(GuiFactory.COLOR_PRIMARY);
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(registerButton);

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
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", 
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                User user = authManager.login(username, password);

                if (user != null) {
                    Session.setUser(user);
                    DashboardPanel dashboard = new DashboardPanel(mainFrame, mainFrame.getFinanceService());
                    mainFrame.addPanel(dashboard, "DASHBOARD");
                    mainFrame.switchToPanel("DASHBOARD");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", 
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException dbError) {
                JOptionPane.showMessageDialog(this, 
                    "Could not connect to the database. Please check your connection and configuration.", 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> mainFrame.switchToPanel("REGISTER"));
    }
}