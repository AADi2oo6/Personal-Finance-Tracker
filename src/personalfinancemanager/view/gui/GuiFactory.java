package personalfinancemanager.view.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * A helper class to create styled Swing components for our app.
 * This keeps our styling logic separate from our panel logic.
 */
public class GuiFactory {

    // Define our application's color palette
    public static final Color COLOR_PRIMARY = new Color(60, 90, 130);
    public static final Color COLOR_GREEN = new Color(76, 175, 80);
    public static final Color COLOR_BLUE = new Color(33, 150, 243);
    public static final Color COLOR_PURPLE = new Color(156, 39, 176);
    public static final Color COLOR_ORANGE = new Color(255, 152, 0);
    public static final Color COLOR_RED = new Color(244, 67, 54);
    public static final Color COLOR_GRAY = new Color(158, 158, 158);

    public static final Font FONT_BUTTON = new Font("Arial", Font.BOLD, 12);
    public static final Border BORDER_BUTTON = BorderFactory.createEmptyBorder(10, 15, 10, 15);
    public static final Border BORDER_PANEL = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    public static final Border BORDER_PANEL_TALL = BorderFactory.createEmptyBorder(15, 10, 15, 10);

    /**
     * Creates a styled JButton.
     * @param text The text for the button.
     * @param bgColor The background color.
     * @return A styled JButton.
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorder(BORDER_BUTTON);

        // Add a subtle hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Darken the color on hover
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Creates a styled section header.
     * @param text The text for the header.
     * @return A styled JLabel.
     */
    public static JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(COLOR_PRIMARY);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        return label;
    }
}
