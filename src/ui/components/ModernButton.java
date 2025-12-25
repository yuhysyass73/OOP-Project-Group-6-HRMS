package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ModernButton - Nút bấm phẳng, hiện đại với hiệu ứng Hover.
 */

public class ModernButton extends JButton {
    private Color colorNormal;
    private Color colorHover;
    private Color colorPressed;
    private Color colorText;
    private int cornerRadius = 10;
    
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text) {
        this(text, new Color(59, 130, 246), new Color(37, 99, 235));
    }

    public ModernButton(String text, Color normal, Color hover) {
        super(text);
        this.colorNormal = normal;
        this.colorHover = hover;
        this.colorPressed = hover.darker();
        this.colorText = Color.WHITE;

        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        //Tắt vẽ mặc định của Swing
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        //Xử lý sự kiện chuột để làm hiệu ứng
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Xác định màu nền dựa trên trạng thái
        Color paintColor = colorNormal;
        if (isPressed) {
            paintColor = colorPressed;
        } else if (isHovered) {
            paintColor = colorHover;
        }

        //Vẽ nền nút bo góc
        g2.setColor(paintColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        //Vẽ Text ở chính giữa
        FontMetrics fm = g2.getFontMetrics();
        Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
        int textX = (getWidth() - stringBounds.width) / 2;
        int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

        g2.setColor(colorText);
        g2.setFont(getFont());
        g2.drawString(getText(), textX, textY);
    }
}