package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Toast Notification System.
 * Hiển thị thông báo trượt từ góc màn hình thay vì cửa sổ bật lên khó chịu.
 */
public class Toast extends JWindow {

    private final boolean success;
    private final String message;
    private float opacity = 0.0f;
    private Timer timer;
    private int yLocation;

    public Toast(String message, boolean success) {
        this.message = message;
        this.success = success;
        
        setAlwaysOnTop(true);
        setSize(350, 60);
        setLayout(null);
        setBackground(new Color(0, 0, 0, 0)); //Nền trong suốt

        //Tính toán vị trí (Góc phải dưới màn hình)
        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        
        int x = scrSize.width - 370;
        yLocation = scrSize.height - toolHeight.bottom - 80;
        
        setLocation(x, yLocation);
        startAnimation();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        if (success) {
            g2.setColor(new Color(220, 252, 231));
        } else {
            g2.setColor(new Color(254, 226, 226));
        }
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));

        if (success) {
            g2.setColor(new Color(22, 163, 74));
        } else {
            g2.setColor(new Color(220, 38, 38));
        }
        g2.fill(new RoundRectangle2D.Double(0, 0, 10, getHeight(), 15, 15));
        g2.fillRect(5, 0, 10, getHeight());

        //Vẽ Icon
        int iconX = 25;
        int iconY = 18;
        
        if (success) {
            g2.setColor(new Color(22, 163, 74));
            g2.fillOval(iconX, iconY, 24, 24);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawLine(iconX + 6, iconY + 12, iconX + 10, iconY + 16);
            g2.drawLine(iconX + 10, iconY + 16, iconX + 18, iconY + 8);
        } else {
            g2.setColor(new Color(220, 38, 38));
            g2.fillOval(iconX, iconY, 24, 24);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawLine(iconX + 7, iconY + 7, iconX + 17, iconY + 17);
            g2.drawLine(iconX + 17, iconY + 7, iconX + 7, iconY + 17);
        }

        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.drawString(success ? "Thành công" : "Thất bại", 60, 23);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(71, 85, 105));
        g2.drawString(message, 60, 42);
        
        super.paint(g);
    }

    private void startAnimation() {
        timer = new Timer(15, new ActionListener() {
            long startTime = -1;
            boolean isFadingIn = true;
            boolean isWaiting = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime == -1) startTime = System.currentTimeMillis();
                long elapsed = System.currentTimeMillis() - startTime;

                if (isFadingIn) {
                    //Giai đoạn hiện lên (Fade In)
                    opacity += 0.05f;
                    if (opacity >= 1.0f) {
                        opacity = 1.0f;
                        isFadingIn = false;
                        isWaiting = true;
                        startTime = System.currentTimeMillis(); 
                    }
                } else if (isWaiting) {
                    //Giai đoạn chờ đọc tin (2.5s)
                    if (elapsed > 2500) {
                        isWaiting = false;
                        startTime = System.currentTimeMillis();
                    }
                } else {
                    //Giai đoạn ẩn đi (Fade Out)
                    opacity -= 0.05f;
                    if (opacity <= 0.0f) {
                        timer.stop();
                        dispose(); //Hủy cửa sổ
                    }
                }
                
                if (isFadingIn && opacity < 1.0f) {
                    setLocation(getX(), yLocation - (int)(opacity * 15));
                }

                repaint();
            }
        });
        setVisible(true);
        timer.start();
    }
    
    
    public static void show(String msg) {
        new Toast(msg, true);
    }
    
    public static void showError(String msg) {
        new Toast(msg, false);
    }
}