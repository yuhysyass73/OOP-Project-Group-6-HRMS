package tabs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import MainApp.*;
import dataa.DatabaseHandler;
import objects.*;
import ui.components.*; 

public class TabDuAn extends JPanel
{
    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;
    private List<DuAn> danhSachDuAn;

    private JTextField txtMaDuAn, txtTenDuAn;
    private JComboBox<Integer> cmbDoPhucTap;
    private DefaultTableModel modelDuAn;
    private JTable tableDuAn;
    private JComboBox<DuAn> cmbChonDuAn;
    private JTextField txtMaNVThemVaoDuAn;
    private DefaultTableModel modelThanhVienDuAn;
    private JTable tableThanhVienDuAn;

    public TabDuAn(QuanLyNhanVienGUI parent)
    {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;
        this.danhSachDuAn = (parent.danhSachDuAn != null) ? parent.danhSachDuAn : new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(241, 245, 249)); 
        setBorder(new EmptyBorder(10, 10, 10, 10));

        //Tạo bảng và Cột thiếu
        ensureTableExists();

        //GIAO DIỆN
        RoundedPanel crudPanel = new RoundedPanel(15, Color.WHITE);
        crudPanel.setLayout(new BorderLayout(5, 5));
        crudPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topDA = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topDA.setOpaque(false);
        JLabel lbl1 = new JLabel("QUẢN LÝ DỰ ÁN:");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl1.setForeground(new Color(30, 41, 59));
        topDA.add(lbl1);

        topDA.add(new JLabel(" Mã DA:"));
        txtMaDuAn = new JTextField(8); setupTextField(txtMaDuAn); topDA.add(txtMaDuAn);
        
        topDA.add(new JLabel(" Tên DA:"));
        txtTenDuAn = new JTextField(15); setupTextField(txtTenDuAn); topDA.add(txtTenDuAn);
        
        topDA.add(new JLabel(" Độ khó:"));
        cmbDoPhucTap = new JComboBox<>(new Integer[]{1, 2, 3});
        cmbDoPhucTap.setBackground(Color.WHITE);
        topDA.add(cmbDoPhucTap);
        
        ModernButton btnThemDuAn = new ModernButton("Thêm Dự án", new Color(22, 163, 74), new Color(21, 128, 61));
        btnThemDuAn.setPreferredSize(new Dimension(110, 32));
        btnThemDuAn.addActionListener(e -> themDuAn());
        topDA.add(btnThemDuAn);
        
        crudPanel.add(topDA, BorderLayout.NORTH);
        
        String[] columnsDuAn = {"Mã DA", "Tên Dự án", "Độ phức tạp"};
        modelDuAn = new DefaultTableModel(columnsDuAn, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableDuAn = new JTable(modelDuAn);
        setupTable(tableDuAn);
        
        JScrollPane scrollDA = new JScrollPane(tableDuAn);
        scrollDA.getViewport().setBackground(Color.WHITE);
        scrollDA.setPreferredSize(new Dimension(0, 200)); 
        crudPanel.add(scrollDA, BorderLayout.CENTER);
        
        RoundedPanel memberPanel = new RoundedPanel(15, Color.WHITE);
        memberPanel.setLayout(new BorderLayout(5, 5));
        memberPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topMem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topMem.setOpaque(false);
        JLabel lbl2 = new JLabel("THÀNH VIÊN DỰ ÁN:");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl2.setForeground(new Color(30, 41, 59));
        topMem.add(lbl2);
        
        topMem.add(new JLabel(" Chọn Dự án:"));
        cmbChonDuAn = new JComboBox<>();
        cmbChonDuAn.setBackground(Color.WHITE);
        cmbChonDuAn.setPreferredSize(new Dimension(180, 30));
        cmbChonDuAn.addActionListener(e -> locThanhVienTheoDuAn());
        topMem.add(cmbChonDuAn);
        
        topMem.add(Box.createHorizontalStrut(20));
        
        topMem.add(new JLabel("Mã NV thêm:"));
        txtMaNVThemVaoDuAn = new JTextField(10); setupTextField(txtMaNVThemVaoDuAn);
        topMem.add(txtMaNVThemVaoDuAn);
        
        ModernButton btnThemNV = new ModernButton("Thêm NV vào DA", new Color(37, 99, 235), new Color(29, 78, 216));
        btnThemNV.setPreferredSize(new Dimension(130, 32));
        btnThemNV.addActionListener(e -> themNhanVienVaoDuAn());
        topMem.add(btnThemNV);

        memberPanel.add(topMem, BorderLayout.NORTH);

        String[] columnsThanhVien = {"Mã NV", "Họ Tên", "Phòng ban"};
        modelThanhVienDuAn = new DefaultTableModel(columnsThanhVien, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableThanhVienDuAn = new JTable(modelThanhVienDuAn);
        setupTable(tableThanhVienDuAn);
        
        JScrollPane scrollMem = new JScrollPane(tableThanhVienDuAn);
        scrollMem.getViewport().setBackground(Color.WHITE);
        memberPanel.add(scrollMem, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, crudPanel, memberPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        
        add(splitPane, BorderLayout.CENTER);
        
        refreshTableDuAn();
        updateDuAnComboBox();
    }

    private void ensureTableExists() {
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS du_an (ma_da TEXT PRIMARY KEY, ten_da TEXT, do_phuc_tap INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS phan_cong (ma_da TEXT, ma_nv TEXT, PRIMARY KEY(ma_da, ma_nv))");
            try {
                stmt.execute("ALTER TABLE nhan_vien ADD COLUMN diem_thuong_du_an INTEGER DEFAULT 0");
            } catch (SQLException e) {}
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void themDuAn() 
    {
        String maDA = txtMaDuAn.getText().trim();
        String tenDA = txtTenDuAn.getText().trim();
        Integer doPhucTap = (Integer) cmbDoPhucTap.getSelectedItem();

        if (maDA.isEmpty() || tenDA.isEmpty()) { JOptionPane.showMessageDialog(this, "Thiếu thông tin!"); return; }

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement p = conn.prepareStatement("INSERT INTO du_an(ma_da, ten_da, do_phuc_tap) VALUES(?,?,?)")) {
            p.setString(1, maDA);
            p.setString(2, tenDA);
            p.setInt(3, doPhucTap);
            p.executeUpdate();
            
            DuAn da = new DuAn(maDA, tenDA, doPhucTap);
            danhSachDuAn.add(da);
            refreshTableDuAn();
            updateDuAnComboBox();
            
            JOptionPane.showMessageDialog(this, "Thêm dự án thành công!");
            txtMaDuAn.setText(""); txtTenDuAn.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi (có thể mã trùng): " + e.getMessage());
        }
    }

    private void themNhanVienVaoDuAn() 
    {
        DuAn selectedDA = (DuAn) cmbChonDuAn.getSelectedItem();
        String maNV = txtMaNVThemVaoDuAn.getText().trim();

        if (selectedDA == null || maNV.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa chọn DA hoặc nhập mã NV!"); return; }

        NhanVien nvFound = null;
        if(danhSachNV != null) {
            for (NhanVien nv : danhSachNV) {
                if (nv.getMaNhanVien().equals(maNV)) { nvFound = nv; break; }
            }
        }
        if (nvFound == null) { JOptionPane.showMessageDialog(this, "Mã NV không tồn tại!"); return; }

        //CHECK TRÙNG LẶP
        try (Connection conn = DatabaseHandler.connect()) {

            //Kiểm tra xem NV đã có trong dự án này chưa
            String checkSql = "SELECT COUNT(*) FROM phan_cong WHERE ma_da = ? AND ma_nv = ?";
            try (PreparedStatement pCheck = conn.prepareStatement(checkSql)) {
                pCheck.setString(1, selectedDA.getMaDuAn());
                pCheck.setString(2, maNV);
                ResultSet rs = pCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Nhân viên này ĐÃ CÓ trong dự án rồi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return; // Dừng lại, không thêm nữa
                }
            }

            //Thêm vào bảng phân công
            try (PreparedStatement p = conn.prepareStatement("INSERT INTO phan_cong(ma_da, ma_nv) VALUES(?,?)")) {
                p.setString(1, selectedDA.getMaDuAn());
                p.setString(2, maNV);
                p.executeUpdate();
            }

            //Cập nhật điểm thưởng
            int diemThuong = selectedDA.getDoPhucTap();
            nvFound.addDiemThuongDuAn(diemThuong);
             
            try (PreparedStatement p2 = conn.prepareStatement("UPDATE nhan_vien SET diem_thuong_du_an = ? WHERE ma_nv = ?")) {
                p2.setInt(1, nvFound.getDiemThuongDuAn());
                p2.setString(2, maNV);
                p2.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Thêm thành công! NV được cộng " + diemThuong + " điểm.");
            txtMaNVThemVaoDuAn.setText("");
            locThanhVienTheoDuAn();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void locThanhVienTheoDuAn() 
    {
        if (modelThanhVienDuAn == null || cmbChonDuAn == null) return;
        
        DuAn selectedDA = (DuAn) cmbChonDuAn.getSelectedItem();
        modelThanhVienDuAn.setRowCount(0);
        
        if (selectedDA == null) return;
        
        String sql = "SELECT p.ma_nv, n.ho_ten, n.phong_ban FROM phan_cong p " +
                     "LEFT JOIN nhan_vien n ON p.ma_nv = n.ma_nv " +
                     "WHERE p.ma_da = ?";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, selectedDA.getMaDuAn());
            ResultSet rs = p.executeQuery();
            while(rs.next()) {
                modelThanhVienDuAn.addRow(new Object[]{
                    rs.getString("ma_nv"),
                    rs.getString("ho_ten"),
                    rs.getString("phong_ban")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public void refreshTableDuAn() 
    {
        danhSachDuAn.clear();
        modelDuAn.setRowCount(0);
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM du_an")) {
            while(rs.next()) {
                DuAn da = new DuAn(rs.getString("ma_da"), rs.getString("ten_da"), rs.getInt("do_phuc_tap"));
                danhSachDuAn.add(da);
                modelDuAn.addRow(new Object[]{da.getMaDuAn(), da.getTenDuAn(), da.getDoPhucTap()});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void updateDuAnComboBox() 
    {
        if (cmbChonDuAn == null) return;
        cmbChonDuAn.removeAllItems();
        for (DuAn da : danhSachDuAn) {
            cmbChonDuAn.addItem(da);
        }
    }

    private void setupTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(100, 30));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
    }

    private void setupTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(100, 35));
    }
}