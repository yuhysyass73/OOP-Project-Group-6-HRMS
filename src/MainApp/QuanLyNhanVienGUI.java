import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuanLyNhanVienGUI extends JFrame {

    List<NhanVien> danhSachNV;
    List<PhongBan> danhSachPB;
    List<DuAn> danhSachDuAn;

    private TabNhanVien tabNhanVien;
    private TabPhongBan tabPhongBan;
    private TabDuAn tabDuAn;
    private TabHieuSuat tabHieuSuat;
    private TabLuong tabLuong;
    private TabBaoCao tabBaoCao;
    
    NumberFormat currencyFormatter;

    private String adminUsername = "admin";
    private String adminPassword = "admin";
    private JLabel lblXinChao; 
    
    private JLabel lblThoiGianPhien; 
    private Timer sessionTimer;      
    private long startSessionTime;   

    public QuanLyNhanVienGUI() {
        setTitle("Phần mềm Quản lý Nhân sự");
        setSize(1200, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

        danhSachNV = new ArrayList<>();
        danhSachPB = new ArrayList<>();
        danhSachDuAn = new ArrayList<>();
        loadSampleDataPB();
        loadSampleDataNV();
        loadSampleDataDuAn();
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabNhanVien = new TabNhanVien(this);
        tabPhongBan = new TabPhongBan(this);
        tabDuAn = new TabDuAn(this);
        tabLuong = new TabLuong(this);
        tabHieuSuat = new TabHieuSuat(this);
        tabBaoCao = new TabBaoCao(this);

        tabbedPane.addTab("Quản lý Nhân viên", null, tabNhanVien, "Quản lý thông tin nhân viên");
        tabbedPane.addTab("Xem theo Phòng ban", null, tabPhongBan, "Xem nhân viên theo phòng ban");
        tabbedPane.addTab("Quản lý Dự án", null, tabDuAn, "Quản lý các dự án");
        tabbedPane.addTab("Quản lý Lương", null, tabLuong, "Xem bảng lương nhân viên");
        tabbedPane.addTab("Quản lý Hiệu suất", null, tabHieuSuat, "Quản lý và đánh giá hiệu suất");
        tabbedPane.addTab("Báo cáo", null, tabBaoCao, "Báo cáo và Thống kê");

        add(tabbedPane, BorderLayout.CENTER);

        refreshAllTabs();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        header.setBackground(new Color(230, 240, 255));
        
        JLabel lblTitle = new JLabel("  HỆ THỐNG QUẢN LÝ NHÂN SỰ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.BLUE);
        header.add(lblTitle, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        lblThoiGianPhien = new JLabel("Phiên: 00:00:00  |  ");
        lblThoiGianPhien.setFont(new Font("Arial", Font.BOLD, 12));
        lblThoiGianPhien.setForeground(new Color(0, 102, 204));
        
        lblXinChao = new JLabel("Xin chào, " + adminUsername + " | ");
        lblXinChao.setFont(new Font("Arial", Font.ITALIC, 12));
        
        JButton btnDoiMK = new JButton("Đổi mật khẩu");
        JButton btnDangXuat = new JButton("Đăng xuất");
        
        btnDoiMK.setFocusable(false);
        btnDangXuat.setFocusable(false);
        btnDangXuat.setBackground(new Color(255, 200, 200));

        btnDoiMK.addActionListener(e -> hienThiDoiMatKhau());
        btnDangXuat.addActionListener(e -> xuLyDangXuat());

        rightPanel.add(lblThoiGianPhien);
        rightPanel.add(lblXinChao);
        rightPanel.add(btnDoiMK);
        rightPanel.add(btnDangXuat);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }

    private void batDauDemGioLamViec() {
        startSessionTime = System.currentTimeMillis();
        
        if (sessionTimer != null) {
            sessionTimer.stop();
        }

        sessionTimer = new Timer(1000, e -> {
            long now = System.currentTimeMillis();
            long duration = now - startSessionTime;

            long seconds = (duration / 1000) % 60;
            long minutes = (duration / (1000 * 60)) % 60;
            long hours = (duration / (1000 * 60 * 60));

            String timeStr = String.format("Phiên: %02d:%02d:%02d  |  ", hours, minutes, seconds);
            lblThoiGianPhien.setText(timeStr);
        });
        
        sessionTimer.start();
    }

    public void hienThiManHinhDangNhap() {
        JDialog loginDialog = new JDialog(this, "Đăng nhập Hệ thống", true);
        loginDialog.setSize(350, 200);
        loginDialog.setLayout(new GridBagLayout());
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        loginDialog.add(new JLabel("Tài khoản:"), gbc);
        gbc.gridx = 1;
        JTextField txtUser = new JTextField(15);
        loginDialog.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        loginDialog.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        JPasswordField txtPass = new JPasswordField(15);
        loginDialog.add(txtPass, gbc);

        JPanel btnPanel = new JPanel();
        JButton btnLogin = new JButton("Đăng nhập");
        JButton btnExit = new JButton("Thoát");
        
        loginDialog.getRootPane().setDefaultButton(btnLogin);

        btnLogin.addActionListener(e -> {
            String user = txtUser.getText();
            String pass = new String(txtPass.getPassword());

            if (user.equals(adminUsername) && pass.equals(adminPassword)) {
                batDauDemGioLamViec(); 
                loginDialog.dispose(); 
            } else {
                JOptionPane.showMessageDialog(loginDialog, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExit.addActionListener(e -> System.exit(0));

        btnPanel.add(btnLogin);
        btnPanel.add(btnExit);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loginDialog.add(btnPanel, gbc);

        loginDialog.setVisible(true);
    }

    private void xuLyDangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (sessionTimer != null) sessionTimer.stop();
            lblThoiGianPhien.setText("Phiên: 00:00:00  |  ");

            this.setVisible(false); 
            hienThiManHinhDangNhap(); 
            this.setVisible(true); 
        }
    }

    private void hienThiDoiMatKhau() {
        JDialog passDialog = new JDialog(this, "Đổi mật khẩu", true);
        passDialog.setSize(400, 250);
        passDialog.setLayout(new GridBagLayout());
        passDialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPasswordField txtOldPass = new JPasswordField(15);
        JPasswordField txtNewPass = new JPasswordField(15);
        JPasswordField txtConfirmPass = new JPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 0; passDialog.add(new JLabel("Mật khẩu cũ:"), gbc);
        gbc.gridx = 1; passDialog.add(txtOldPass, gbc);

        gbc.gridx = 0; gbc.gridy = 1; passDialog.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1; passDialog.add(txtNewPass, gbc);

        gbc.gridx = 0; gbc.gridy = 2; passDialog.add(new JLabel("Xác nhận MK mới:"), gbc);
        gbc.gridx = 1; passDialog.add(txtConfirmPass, gbc);

        JButton btnSave = new JButton("Lưu thay đổi");
        btnSave.addActionListener(e -> {
            String oldP = new String(txtOldPass.getPassword());
            String newP = new String(txtNewPass.getPassword());
            String confirmP = new String(txtConfirmPass.getPassword());

            if (!oldP.equals(adminPassword)) {
                JOptionPane.showMessageDialog(passDialog, "Mật khẩu cũ không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newP.isEmpty()) {
                JOptionPane.showMessageDialog(passDialog, "Mật khẩu mới không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newP.equals(confirmP)) {
                JOptionPane.showMessageDialog(passDialog, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            adminPassword = newP;
            JOptionPane.showMessageDialog(passDialog, "Đổi mật khẩu thành công!");
            passDialog.dispose();
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; 
        gbc.anchor = GridBagConstraints.CENTER;
        passDialog.add(btnSave, gbc);

        passDialog.setVisible(true);
    }

    
    public void refreshAllTabs() {
        refreshTableNV();
        updatePhongBanComboBox();
        locNhanVienTheoPhongBan();
        updateDuAnComboBox();
        refreshLuongTable();
        refreshBaoCaoTab();
    }

    public void refreshBaoCaoTab() { if (tabBaoCao != null) tabBaoCao.refreshBaoCao(); }
    public void refreshLuongTable() { if (tabLuong != null) tabLuong.refreshLuongTable(); }
    public void locNhanVienTheoPhongBan() { if (tabPhongBan != null) tabPhongBan.locNhanVienTheoPhongBan(); }
    public void refreshTableNV() { if (tabNhanVien != null) tabNhanVien.refreshTableNV(); }
    public void updateDuAnComboBox() { if (tabDuAn != null) tabDuAn.updateDuAnComboBox(); }
    public void updatePhongBanComboBox() {
        if (tabNhanVien != null) tabNhanVien.updatePhongBanComboBox();
        if (tabPhongBan != null) tabPhongBan.updatePhongBanComboBox();
    }


    private void loadSampleDataPB() {
        danhSachPB.add(new PhongBan("KT", "Kỹ thuật"));
        danhSachPB.add(new PhongBan("KD", "Kinh doanh"));
        danhSachPB.add(new PhongBan("NS", "Nhân sự"));
    }
    
    private void loadSampleDataNV() {
        danhSachNV.add(new NhanVien("NV001", "Nguyễn Văn A", "Kỹ thuật", "0900111222", "a.nguyen@example.com", "01/01/1990", "123456789", 5));
        danhSachNV.add(new NhanVien("NV002", "Trần Thị B", "Kinh doanh", "0900333444", "b.tran@example.com", "02/02/1992", "987654321", 3));
        danhSachNV.add(new NhanVien("NV003", "Lê Văn C", "Nhân sự", "0900555666", "c.le@example.com", "03/03/1995", "111222333", 1));
        danhSachNV.add(new NhanVien("NV004", "Phạm Văn D", "Kỹ thuật", "0900777888", "d.pham@example.com", "04/04/1998", "444555666", 2));
    }
    
    private void loadSampleDataDuAn() {
        danhSachDuAn.add(new DuAn("DA01", "Website Thương mại điện tử", 3));
        danhSachDuAn.add(new DuAn("DA02", "Hệ thống CRM nội bộ", 2));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuanLyNhanVienGUI app = new QuanLyNhanVienGUI();
            
            app.hienThiManHinhDangNhap(); 
            app.setVisible(true);
        });
    }
}