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
        
        JLabel lblBackupInfo = new JLabel("<html><center>Sao chép toàn bộ dữ liệu hiện tại<br/>ra file dự phòng an toàn.</center></html>");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; pnlBackup.add(btnBackup, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(10,0,0,0); pnlBackup.add(lblBackupInfo, gbc);

        JPanel pnlRestore = new JPanel(new GridBagLayout());
        JButton btnRestore = new JButton("Phục hồi Dữ liệu (Restore)");
        btnRestore.setIcon(UIManager.getIcon("FileView.computerIcon"));
        btnRestore.setBackground(new Color(204, 0, 0));
        btnRestore.setForeground(Color.WHITE);
        btnRestore.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel lblRestoreInfo = new JLabel("<html><center>Khôi phục dữ liệu từ file đã lưu.<br/>(Cảnh báo: Dữ liệu hiện tại sẽ mất)</center></html>");
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0,0,0,0); pnlRestore.add(btnRestore, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(10,0,0,0); pnlRestore.add(lblRestoreInfo, gbc);

        pnlData.add(pnlBackup);
        pnlData.add(pnlRestore);

        //ĐỔI MẬT KHẨU
        JPanel pnlSecurity = new JPanel(new BorderLayout());
        pnlSecurity.setBorder(BorderFactory.createTitledBorder(title: "Bảo mật & Tài khoản"));

        JPanel formPass = new JPanel(new GridBagLayout());
        GridBagConstraints gbcPass = new GridBagConstraints();
        gbcPass.insets = new Insets(5, 5, 5, 5);
        gbcPass.anchor = GridBagConstraints.WEST;

        gbcPass.gridx = 0; gbcPass.gridy = 0; formPass.add(new JLabel("Mật khẩu hiện tại:"), gbcPass);
        txtPassCu = new JPasswordField(20); gbcPass.gridx = 1; formPass.add(txtPassCu, gbcPass);

        gbcPass.gridx = 0; gbcPass.gridy = 1; formPass.add(new JLabel("Mật khẩu mới:"), gbcPass);
        txtPassMoi = new JPasswordField(20); gbcPass.gridx = 1; formPass.add(txtPassMoi, gbcPass);

        gbcPass.gridx = 0; gbcPass.gridy = 2; formPass.add(new JLabel("Nhập lại mật khẩu mới:"), gbcPass);
        txtPassXacNhan = new JPasswordField(20); gbcPass.gridx = 1; formPass.add(txtPassXacNhan, gbcPass);

        JButton btnDoiPass = new JButton("Cập nhật Mật khẩu");
        gbcPass.gridx = 1; gbcPass.gridy = 3; gbcPass.anchor = GridBagConstraints.EAST;
        formPass.add(btnDoiPass, gbcPass);

        pnlSecurity.add(formPass, BorderLayout.CENTER);

        //THÔNG TIN HỆ THỐNG (BOTTOM)
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String osInfo = System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")";
        String javaInfo = System.getProperty("java.version");
        JLabel lblSys = new JLabel("Hệ thống: " + osInfo + " | Java: " + javaInfo);
        lblSys.setForeground(Color.GRAY);
        pnlInfo.add(lblSys);

        JPanel mainCenter = new JPanel(new BorderLayout(10, 10));
        mainCenter.add(pnlData, BorderLayout.NORTH);
        mainCenter.add(pnlSecurity, BorderLayout.CENTER);

        add(mainCenter, BorderLayout.CENTER);
        add(pnlInfo, BorderLayout.SOUTH);

        btnBackup.addActionListener(e -> xuLyBackup());
        btnRestore.addActionListener(e -> xuLyRestore());
        btnDoiPass.addActionListener(e -> xuLyDoiMatKhau());
    }
}