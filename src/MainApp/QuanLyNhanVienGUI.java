package MainApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dataa.*;
import objects.*;
import tabs.*;
import ui.components.*;

public class QuanLyNhanVienGUI extends JFrame {

    
    public List<NhanVien> danhSachNV;
    public List<PhongBan> danhSachPB;
    public List<DuAn> danhSachDuAn;
    private List<LogEntry> danhSachLog;


    private JPanel mainContainer;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private TabDashboard tabDashboard; 
    private TabNhanVien tabNhanVien;
    private TabPhongBan tabPhongBan;
    private TabDuAn tabDuAn;
    private TabHieuSuat tabHieuSuat;
    private TabLichLamViec tabLichLamViec; 
    private TabLuong tabLuong;
    private TabBaoCao tabBaoCao;
    private TabNhatKy tabNhatKy;
    private TabTuyenDung tabTuyenDung;
    private TabDaoTao tabDaoTao;
    private TabTaiSan tabTaiSan;
    private TabHeThong tabHeThong;
    private TabEmail tabEmail; 

    public NumberFormat currencyFormatter;
    private QuanLyTaiKhoan quanLyTaiKhoan; 
    private String currentUser = ""; 
    
    private Timer sessionTimer;      
    private long startSessionTime;   

    public QuanLyNhanVienGUI() {
        //KHỞI TẠO DATABASE VÀ DỮ LIỆU NỀN
        DatabaseHandler.createNewDatabase();
        
        danhSachNV = new ArrayList<>();
        danhSachPB = new ArrayList<>();
        danhSachDuAn = new ArrayList<>();
        danhSachLog = new ArrayList<>();
        
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
        quanLyTaiKhoan = new QuanLyTaiKhoan();
        
        //Load dữ liệu từ SQL lên RAM
        loadDataFromDB();
        
        ghiNhatKy("Khởi động", "Ứng dụng đã được bật lên");

        setTitle("Hệ thống Quản trị Doanh nghiệp Tổng thể (ERP) - Enterprise Edition");
        setSize(1450, 850); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        initModernLayout();

        initScreens();
        
        cardLayout.show(contentPanel, "DASHBOARD");
    }
    
    /**
     * Thiết lập cấu trúc giao diện: Menu trái + Nội dung phải
     */
    private void initModernLayout() {
        mainContainer = new JPanel(new BorderLayout());
        setContentPane(mainContainer);

        //(MENU TRÁI)
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(30, 41, 59));
        sidebarPanel.setPreferredSize(new Dimension(260, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel lblLogo = new JLabel("ERP SYSTEM");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(lblLogo);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JLabel lblSubLogo = new JLabel("Enterprise Edition");
        lblSubLogo.setForeground(new Color(148, 163, 184));
        lblSubLogo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(lblSubLogo);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        //CONTENT PANEL (NỘI DUNG PHẢI)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(241, 245, 249));

        mainContainer.add(sidebarPanel, BorderLayout.WEST);
        mainContainer.add(contentPanel, BorderLayout.CENTER);
    }

    private void initScreens() {

        tabDashboard = new TabDashboard(this); 
        tabNhanVien = new TabNhanVien(this);
        tabPhongBan = new TabPhongBan(this);
        tabDuAn = new TabDuAn(this);
        tabLuong = new TabLuong(this);
        tabHieuSuat = new TabHieuSuat(this);
        tabLichLamViec = new TabLichLamViec(this); 
        tabTuyenDung = new TabTuyenDung(this);
        tabDaoTao = new TabDaoTao(this);
        tabTaiSan = new TabTaiSan(this);
        tabBaoCao = new TabBaoCao(this);
        tabNhatKy = new TabNhatKy(this);
        tabHeThong = new TabHeThong(this);
        tabEmail = new TabEmail(this);


        contentPanel.add(tabDashboard, "DASHBOARD");
        contentPanel.add(tabNhanVien, "NHAN_VIEN");
        contentPanel.add(tabPhongBan, "PHONG_BAN");
        contentPanel.add(tabDuAn, "DU_AN");
        contentPanel.add(tabLuong, "LUONG");
        contentPanel.add(tabHieuSuat, "HIEU_SUAT");
        contentPanel.add(tabLichLamViec, "LICH_LAM_VIEC");
        contentPanel.add(tabTuyenDung, "TUYEN_DUNG");
        contentPanel.add(tabDaoTao, "DAO_TAO");
        contentPanel.add(tabTaiSan, "TAI_SAN");
        contentPanel.add(tabBaoCao, "BAO_CAO");
        contentPanel.add(tabNhatKy, "NHAT_KY");
        contentPanel.add(tabHeThong, "HE_THONG");
        contentPanel.add(tabEmail, "EMAIL");
    }


    private void addSidebarButton(String text, String screenId) {
        ModernButton btn = new ModernButton(text, new Color(30, 41, 59), new Color(51, 65, 85));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setPreferredSize(new Dimension(240, 45));
        btn.setMaximumSize(new Dimension(240, 45));
        
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, screenId);
            refreshAllTabs(); //Refresh dữ liệu mỗi khi chuyển tab
        });
        
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    }
    
    /**
     * Hàm thêm tiêu đề phân cách trong Sidebar
     */
    private void addSidebarDivider(String title) {
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(240, 20));
        
        JLabel lbl = new JLabel(title.toUpperCase());
        lbl.setForeground(new Color(100, 116, 139));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        p.add(lbl, BorderLayout.WEST);
        sidebarPanel.add(p);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    //XỬ LÝ LOGIC NGHIỆP VỤ

    public String getCurrentUser() {
        return currentUser;
    }

    private void setupTabsByRole(String role) {

        while(sidebarPanel.getComponentCount() > 4) {
            sidebarPanel.remove(4);
        }

        addSidebarButton("  Tổng quan (Dashboard)", "DASHBOARD");

        //PHÂN QUYỀN MENU
        if (role.equals("admin")) {
            addSidebarDivider("Quản trị Nhân sự");
            addSidebarButton("  Hồ sơ Nhân viên", "NHAN_VIEN");
            addSidebarButton("  Cơ cấu Phòng ban", "PHONG_BAN");
            addSidebarButton("  Tuyển dụng & Hợp đồng", "TUYEN_DUNG");
            
            addSidebarDivider("Vận hành & Công việc");
            addSidebarButton("  Lịch làm việc", "LICH_LAM_VIEC");
            addSidebarButton("  Chấm công & Hiệu suất", "HIEU_SUAT");
            addSidebarButton("  Quản lý Dự án", "DU_AN");
            
            addSidebarDivider("Tài chính & Tài sản");
            addSidebarButton("  Bảng Lương", "LUONG");
            addSidebarButton("  Quản lý Tài sản", "TAI_SAN");
            
            addSidebarDivider("Hệ thống");
            addSidebarButton("  Báo cáo Thống kê", "BAO_CAO");
            addSidebarButton("  Nhật ký Hệ thống", "NHAT_KY");
            addSidebarButton("  Gửi Email Nội bộ", "EMAIL");
            addSidebarButton("  Cấu hình & Bảo mật", "HE_THONG");
            
            //Tạo tài khoản
            ModernButton btnCreateAcc = new ModernButton("  + Tạo Tài khoản mới", new Color(22, 163, 74), new Color(21, 128, 61));
            btnCreateAcc.setHorizontalAlignment(SwingConstants.LEFT);
            btnCreateAcc.setMaximumSize(new Dimension(240, 45));
            btnCreateAcc.addActionListener(e -> hienThiManHinhTaoTaiKhoan());
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            sidebarPanel.add(btnCreateAcc);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        } 
        else if (role.equals("hr")) {
            addSidebarDivider("Nghiệp vụ HR");
            addSidebarButton("  Hồ sơ Nhân viên", "NHAN_VIEN");
            addSidebarButton("  Tuyển dụng", "TUYEN_DUNG");
            addSidebarButton("  Đào tạo Nội bộ", "DAO_TAO");
            addSidebarButton("  Lịch & Phân ca", "LICH_LAM_VIEC");
            addSidebarButton("  Chấm công", "HIEU_SUAT");
            
            addSidebarDivider("Tiện ích");
            addSidebarButton("  Phòng ban", "PHONG_BAN");
            addSidebarButton("  Gửi Email", "EMAIL");
            addSidebarButton("  Đổi mật khẩu", "HE_THONG");
        } 
        else if (role.equals("accountant")) {
            addSidebarDivider("Kế toán");
            addSidebarButton("  Quản lý Lương", "LUONG");
            addSidebarButton("  Tài sản cố định", "TAI_SAN");
            addSidebarButton("  Báo cáo Tài chính", "BAO_CAO");
            
            addSidebarDivider("Tham chiếu");
            addSidebarButton("  Dữ liệu Chấm công", "HIEU_SUAT");
            addSidebarButton("  Đổi mật khẩu", "HE_THONG");
        }
        else {
            addSidebarDivider("Cá nhân");
            addSidebarButton("  Lịch làm việc", "LICH_LAM_VIEC");
            //addSidebarButton("  Hồ sơ của tôi", "NHAN_VIEN");
            addSidebarButton("  Đổi mật khẩu", "HE_THONG");
        }
        
        //Nút Đăng xuất
        sidebarPanel.add(Box.createVerticalGlue());
        ModernButton btnLogout = new ModernButton("Đăng xuất", new Color(185, 28, 28), new Color(153, 27, 27));
        btnLogout.setMaximumSize(new Dimension(240, 45));
        btnLogout.addActionListener(e -> xuLyDangXuat());
        sidebarPanel.add(btnLogout);

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
        refreshAllTabs();
    }

    private void loadDataFromDB() {
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rsCheckPB = stmt.executeQuery("SELECT COUNT(*) FROM phong_ban");
            rsCheckPB.next();
            if (rsCheckPB.getInt(1) == 0) {
                stmt.execute("INSERT INTO phong_ban VALUES ('KT', 'Kỹ thuật')");
                stmt.execute("INSERT INTO phong_ban VALUES ('KD', 'Kinh doanh')");
                stmt.execute("INSERT INTO phong_ban VALUES ('NS', 'Nhân sự')");
            }
            rsCheckPB.close();
            ResultSet rsPB = stmt.executeQuery("SELECT * FROM phong_ban");
            while (rsPB.next()) {
                danhSachPB.add(new PhongBan(rsPB.getString("ma_pb"), rsPB.getString("ten_pb")));
            }
            rsPB.close();

            ResultSet rsCheckDA = stmt.executeQuery("SELECT COUNT(*) FROM du_an");
            rsCheckDA.next();
            if (rsCheckDA.getInt(1) == 0) {
                stmt.execute("INSERT INTO du_an (ma_da, ten_da, do_phuc_tap) VALUES ('DA01', 'Website TMĐT', 3)");
                stmt.execute("INSERT INTO du_an (ma_da, ten_da, do_phuc_tap) VALUES ('DA02', 'Hệ thống CRM nội bộ', 2)");
            }
            ResultSet rsDA = stmt.executeQuery("SELECT * FROM du_an");
            while (rsDA.next()) {
                danhSachDuAn.add(new DuAn(rsDA.getString("ma_da"), rsDA.getString("ten_da"), rsDA.getInt("do_phuc_tap")));
            }

            ResultSet rsCheckNV = stmt.executeQuery("SELECT COUNT(*) FROM nhan_vien");
            rsCheckNV.next();
            int soLuongNV = rsCheckNV.getInt(1);
            rsCheckNV.close();
            if (soLuongNV == 0) {
                DataSeeder.themNhanVienMau(conn);
            }
            ResultSet rsNV = stmt.executeQuery("SELECT * FROM nhan_vien");
            while (rsNV.next()) {
                NhanVien nv = new NhanVien(
                    rsNV.getString("ma_nv"),
                    rsNV.getString("ho_ten"),
                    rsNV.getString("phong_ban"),
                    rsNV.getString("sdt"),
                    rsNV.getString("email"),
                    rsNV.getString("ngay_sinh"),
                    rsNV.getString("cccd"),
                    rsNV.getInt("tham_nien")
                );
                nv.setDiemViPham(rsNV.getInt("diem_vi_pham"));
                nv.setDiemThuongDuAn(rsNV.getInt("diem_thuong_da"));
                danhSachNV.add(nv);
            }

            ResultSet rsPC = stmt.executeQuery("SELECT * FROM phan_cong");
            while (rsPC.next()) {
                String maDA = rsPC.getString("ma_da");
                String maNV = rsPC.getString("ma_nv");

                DuAn da = null;
                for (DuAn d : danhSachDuAn) {
                    if (d.getMaDuAn().equals(maDA)) { da = d; break; }
                }
                
                NhanVien nv = null;
                for (NhanVien n : danhSachNV) {
                    if (n.getMaNhanVien().equals(maNV)) { nv = n; break; }
                }

                if (da != null && nv != null) {
                    da.addThanhVien(nv);
                }
            }

            ResultSet rsLog = stmt.executeQuery("SELECT * FROM nhat_ky ORDER BY id DESC LIMIT 50");
            while (rsLog.next()) {
                 danhSachLog.add(new LogEntry(
                     rsLog.getString("thoi_gian"),
                     rsLog.getString("nguoi_dung"), 
                     rsLog.getString("hanh_dong"), 
                     rsLog.getString("chi_tiet")
                 ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }
    
    public void ghiNhatKy(String hanhDong, String chiTiet) {
        String user = currentUser.isEmpty() ? "Khách/System" : currentUser;
        
        LogEntry log = new LogEntry(user, hanhDong, chiTiet);
        danhSachLog.add(log);
        
        String sql = "INSERT INTO nhat_ky(thoi_gian, nguoi_dung, hanh_dong, chi_tiet) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, log.getThoiGian());
            pstmt.setString(2, user);
            pstmt.setString(3, hanhDong);
            pstmt.setString(4, chiTiet);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (tabNhatKy != null) tabNhatKy.refreshLogTable();
    }
    
    public List<LogEntry> getDanhSachLog() { return danhSachLog; }


    private void batDauDemGioLamViec() {
        startSessionTime = System.currentTimeMillis();
        if (sessionTimer != null) sessionTimer.stop();

        sessionTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        sessionTimer.start();
    }

    public void hienThiManHinhDangNhap() {
        JDialog loginDialog = new JDialog(this, "Đăng nhập Hệ thống", true);
        loginDialog.setSize(400, 280);
        loginDialog.setLayout(null);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.getContentPane().setBackground(Color.WHITE);
        loginDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        loginDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); 
            }
        });

        JLabel lblTitle = new JLabel("LOGIN ERP SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 20, 400, 30);
        loginDialog.add(lblTitle);

        JLabel lblUser = new JLabel("Tài khoản:");
        lblUser.setBounds(50, 65, 100, 20);
        JTextField txtUser = new JTextField();
        txtUser.setBounds(50, 85, 285, 30);
        loginDialog.add(lblUser);
        loginDialog.add(txtUser);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setBounds(50, 125, 100, 20);
        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(50, 145, 285, 30);
        loginDialog.add(lblPass);
        loginDialog.add(txtPass);
        
        txtPass.addActionListener(e -> {
             for (Component c : loginDialog.getContentPane().getComponents()) {
                 if (c instanceof ModernButton && ((ModernButton)c).getText().equals("ĐĂNG NHẬP")) {
                     ((ModernButton)c).doClick();
                 }
             }
        });

        ModernButton btnLogin = new ModernButton("ĐĂNG NHẬP");
        btnLogin.setBounds(50, 195, 135, 35);
        loginDialog.add(btnLogin);
        
        ModernButton btnExit = new ModernButton("THOÁT", new Color(200, 200, 200), new Color(150, 150, 150));
        btnExit.setBounds(200, 195, 135, 35);
        loginDialog.add(btnExit);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUser.getText();
                String pass = new String(txtPass.getPassword());

                String role = quanLyTaiKhoan.dangNhap(user, pass);

                if (role != null) {
                    currentUser = user; 
                    setupTabsByRole(role);
                    ghiNhatKy("Đăng nhập", "Role: " + role);
                    batDauDemGioLamViec(); 
                    loginDialog.dispose(); 
                } else {
                    JOptionPane.showMessageDialog(loginDialog, "Sai tài khoản hoặc mật khẩu!", "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnExit.addActionListener(e -> System.exit(0));
        
        loginDialog.getRootPane().setDefaultButton(btnLogin); 
        loginDialog.setVisible(true);
    }

    private void hienThiManHinhTaoTaiKhoan() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField txtNewUser = new JTextField();
        JPasswordField txtNewPass = new JPasswordField();
        
        String[] roles = {"user", "admin", "hr", "accountant"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);
        
        panel.add(new JLabel("Tên đăng nhập mới:"));
        panel.add(txtNewUser);
        
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(txtNewPass);
        
        panel.add(new JLabel("Vai trò (Role):"));
        panel.add(cmbRole);

        int result = JOptionPane.showConfirmDialog(this, panel, "Cấp Tài khoản mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newUser = txtNewUser.getText().trim();
            String newPass = new String(txtNewPass.getPassword()).trim();
            String role = (String) cmbRole.getSelectedItem();

            if (newUser.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            boolean thanhCong = quanLyTaiKhoan.themTaiKhoan(newUser, newPass, role);
            
            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Tạo tài khoản thành công!\nUser: " + newUser + "\nRole: " + role);
                ghiNhatKy("Tạo tài khoản", "Đã tạo user: " + newUser + " (Quyền: " + role + ")");
            } else {
                JOptionPane.showMessageDialog(this, "Tên tài khoản đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyDangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ghiNhatKy("Đăng xuất", "Người dùng thoát phiên làm việc");
            if (sessionTimer != null) sessionTimer.stop();
            currentUser = ""; 
            this.setVisible(false); 
            hienThiManHinhDangNhap(); 
            this.setVisible(true); 
        }
    }
    
    public void refreshAllTabs() {
        if (tabDashboard != null) tabDashboard.refreshDashboard(); 
        if (tabNhanVien != null) tabNhanVien.refreshTableNV();
        if (tabNhanVien != null) tabNhanVien.updatePhongBanComboBox();
        
        if (tabPhongBan != null) {
            tabPhongBan.updatePhongBanComboBox();
            tabPhongBan.locNhanVienTheoPhongBan();
        }
        
        if (tabDuAn != null) {
            tabDuAn.refreshTableDuAn(); 
            tabDuAn.updateDuAnComboBox();
        }
        
        if (tabLuong != null) tabLuong.refreshLuongTable();
        if (tabBaoCao != null) tabBaoCao.refreshBaoCao();
        if (tabNhatKy != null) tabNhatKy.refreshLogTable();
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
     
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                QuanLyNhanVienGUI app = new QuanLyNhanVienGUI();
                app.hienThiManHinhDangNhap(); 
                app.setVisible(true);
            }
        });
    }
}