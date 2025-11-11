package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * A helper class to create styled Swing components for our app.
 * This keeps our styling logic separate from our panel logic.
 * Modern, flat design with contemporary colors and smooth interactions.
 */
public class GuiFactory {

    // Modern color palette - Contemporary and vibrant
    public static final Color COLOR_PRIMARY = new Color(41, 128, 185);          // Modern Blue
    public static final Color COLOR_PRIMARY_DARK = new Color(28, 90, 130);      // Darker Blue
    public static final Color COLOR_ACCENT = new Color(52, 152, 219);           // Light Blue
    public static final Color COLOR_SUCCESS = new Color(46, 204, 113);          // Modern Green
    public static final Color COLOR_WARNING = new Color(241, 196, 15);          // Modern Yellow
    public static final Color COLOR_DANGER = new Color(231, 76, 60);            // Modern Red
    public static final Color COLOR_INFO = new Color(52, 73, 94);               // Dark Gray-Blue
    public static final Color COLOR_LIGHT = new Color(236, 240, 241);           // Light Gray
    public static final Color COLOR_DARK = new Color(44, 62, 80);               // Dark Gray

    // Backwards compatibility - map old colors to new
    public static final Color COLOR_GREEN = COLOR_SUCCESS;
    public static final Color COLOR_BLUE = new Color(33, 150, 243);
    public static final Color COLOR_PURPLE = new Color(156, 39, 176);
    public static final Color COLOR_ORANGE = new Color(255, 152, 0);
    public static final Color COLOR_RED = COLOR_DANGER;
    public static final Color COLOR_GRAY = new Color(149, 165, 166);

    // Fonts - Modern typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 11);

    // Borders - Subtle and modern
    public static final Border BORDER_BUTTON = new EmptyBorder(10, 20, 10, 20);
    public static final Border BORDER_PANEL = new EmptyBorder(15, 15, 15, 15);
    public static final Border BORDER_PANEL_TALL = new EmptyBorder(20, 15, 20, 15);
    public static final Border BORDER_CARD = BorderFactory.createLineBorder(COLOR_LIGHT, 1);

    // Insets for consistent spacing
    public static final Insets INSETS_SMALL = new Insets(5, 5, 5, 5);
    public static final Insets INSETS_MEDIUM = new Insets(10, 10, 10, 10);
    public static final Insets INSETS_LARGE = new Insets(15, 15, 15, 15);

    /**
     * Initialize modern look and feel
     */
    public static void initializeModernTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set custom colors for various components
            UIManager.put("Button.background", COLOR_LIGHT);
            UIManager.put("Panel.background", COLOR_LIGHT);
            UIManager.put("Label.background", COLOR_LIGHT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a modern styled JButton with rounded appearance and smooth hover effect
     * @param text The text for the button.
     * @param bgColor The background color.
     * @return A modern styled JButton.
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BORDER_BUTTON);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Modern hover effect with smooth transition
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(lightenColor(bgColor, 0.8));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(darkenColor(bgColor, 1.2));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Creates a modern styled text field
     */
    public static JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(FONT_TEXT);
        textField.setBorder(BorderFactory.createLineBorder(COLOR_GRAY, 1));
        textField.setMargin(new Insets(8, 10, 8, 10));
        textField.setBackground(Color.WHITE);
        return textField;
    }

    /**
     * Creates a modern styled password field
     */
    public static JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(FONT_TEXT);
        passwordField.setBorder(BorderFactory.createLineBorder(COLOR_GRAY, 1));
        passwordField.setMargin(new Insets(8, 10, 8, 10));
        passwordField.setBackground(Color.WHITE);
        return passwordField;
    }

    /**
     * Creates a modern styled section header with underline
     */
    public static JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBHEADING);
        label.setForeground(COLOR_PRIMARY_DARK);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_PRIMARY),
                new EmptyBorder(10, 0, 8, 0)
        ));
        return label;
    }

    /**
     * Creates a panel header with modern styling
     */
    public static JLabel createPanelHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADING);
        label.setForeground(COLOR_PRIMARY_DARK);
        label.setBorder(BORDER_PANEL_TALL);
        return label;
    }

    /**
     * Creates a modern card panel
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BORDER_CARD,
                BORDER_PANEL
        ));
        return panel;
    }

    /**
     * Creates a modern label with custom styling
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(COLOR_INFO);
        return label;
    }

    /**
     * Creates a modern title label
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(COLOR_PRIMARY_DARK);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(20, 0, 20, 0));
        return label;
    }

    /**
     * Lighten a color
     */
    private static Color lightenColor(Color color, double factor) {
        int r = (int) Math.min(255, color.getRed() + (255 - color.getRed()) * (1 - factor));
        int g = (int) Math.min(255, color.getGreen() + (255 - color.getGreen()) * (1 - factor));
        int b = (int) Math.min(255, color.getBlue() + (255 - color.getBlue()) * (1 - factor));
        return new Color(r, g, b);
    }

    /**
     * Darken a color
     */
    private static Color darkenColor(Color color, double factor) {
        int r = (int) Math.max(0, color.getRed() * factor);
        int g = (int) Math.max(0, color.getGreen() * factor);
        int b = (int) Math.max(0, color.getBlue() * factor);
        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }
}
