package tabs;

import javax.swing.*;

import MainApp.*;
import objects.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TabHeThong extends JPanel {

    private QuanLyNhanVienGUI parent;
    private QuanLyTaiKhoan quanLyTaiKhoan;

    
    private JPasswordField txtPassCu;
    private JPasswordField txtPassMoi;
    private JPasswordField txtPassXacNhan;

    private static final String DB_SOURCE = "quanlynhansu.db";

    public TabHeThong(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.quanLyTaiKhoan = new QuanLyTaiKhoan();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //BACKUP/RESTORE
        JPanel pnlData = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlData.setBorder(BorderFactory.createTitledBorder("Quản trị Cơ sở dữ liệu"));
        pnlData.setPreferredSize(new Dimension(0, 150));

        JPanel pnlBackup = new JPanel(new GridBagLayout());
        JButton btnBackup = new JButton("Sao lưu Dữ liệu (Backup)");
        btnBackup.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        btnBackup.setBackground(new Color(0, 102, 204));
        btnBackup.setForeground(Color.WHITE);
        btnBackup.setFont(new Font("Arial", Font.BOLD, 14));
    }
}