package personalfinancemanager.view.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import personalfinancemanager.auth.AuthManager;
import personalfinancemanager.service.FinanceService;

public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final AuthManager authManager;
    private final FinanceService financeService;

    public MainFrame() {
        this.authManager = new AuthManager();
        this.financeService = new FinanceService();

        // Initialize modern theme
        GuiFactory.initializeModernTheme();

        setTitle("Personal Finance Manager");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set modern background
        setBackground(GuiFactory.COLOR_LIGHT);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(GuiFactory.COLOR_LIGHT);

        // Create panels
        LoginPanel loginPanel = new LoginPanel(this, authManager);
        RegisterPanel registerPanel = new RegisterPanel(this, authManager);

        // Add panels to the card layout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");

        add(mainPanel);
        
        // Set icon and make visible
        setIconImage(createApplicationIcon());
        setVisible(true);
    }

    public void switchToPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public FinanceService getFinanceService() {
        return financeService;
    }

    public void addPanel(JPanel panel, String name) {
        mainPanel.add(panel, name);
    }

    /**
     * Create a simple icon for the application
     */
    private Image createApplicationIcon() {
        // Create a simple 32x32 icon
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = icon.createGraphics();
        
        // Fill background
        g2d.setColor(GuiFactory.COLOR_PRIMARY);
        g2d.fillRect(0, 0, 32, 32);
        
        // Draw a simple dollar sign
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("$", 8, 24);
        
        g2d.dispose();
        return icon;
    }
}