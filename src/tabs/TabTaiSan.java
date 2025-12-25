package tabs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import MainApp.QuanLyNhanVienGUI;
import dataa.DatabaseHandler;
import ui.components.*; 

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

public class TabTaiSan extends JPanel {

    private QuanLyNhanVienGUI parent;
    private JTable tableTaiSan;
    private DefaultTableModel modelTaiSan;

    private JTextField txtMaTS, txtTenTS, txtGiaTri, txtGhiChu;
    private JComboBox<String> cmbTinhTrang;
    private ModernButton btnThem, btnSua, btnXoa, btnLamMoi;

    private DecimalFormat df = new DecimalFormat("#,###");

    public TabTaiSan(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(241, 245, 249)); 
        setBorder(new EmptyBorder(15, 15, 15, 15));

        //Đảm bảo DB có đủ cột
        ensureTableExists();

        add(createInputPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        
        loadData();
    }

    private void ensureTableExists() {
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement()) {
            
            //Tạo bảng nếu chưa có
            String sql = "CREATE TABLE IF NOT EXISTS tai_san (" +
                         "ma_ts TEXT PRIMARY KEY, ten_ts TEXT, tinh_trang TEXT, gia_tri REAL, ghi_chu TEXT)";
            stmt.execute(sql);

            
            try { stmt.execute("ALTER TABLE tai_san ADD COLUMN gia_tri REAL DEFAULT 0"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE tai_san ADD COLUMN ghi_chu TEXT"); } catch (Exception e) {}
            
        } catch (Exception e) { e.printStackTrace(); }
    }

    //PANEL NHẬP LIỆU
    private JPanel createInputPanel() {
        RoundedPanel pnl = new RoundedPanel(15, Color.WHITE);
        pnl.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("QUẢN LÝ TÀI SẢN & THIẾT BỊ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        pnl.add(lblTitle, gbc);

        gbc.gridwidth = 1; 

        gbc.gridy = 1;
        gbc.gridx = 0; pnl.add(createLabel("Mã Tài sản:"), gbc);
        gbc.gridx = 1; txtMaTS = new JTextField(); setupTextField(txtMaTS); pnl.add(txtMaTS, gbc);

        gbc.gridx = 2; pnl.add(createLabel("Tên Tài sản:"), gbc);
        gbc.gridx = 3; txtTenTS = new JTextField(); setupTextField(txtTenTS); pnl.add(txtTenTS, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0; pnl.add(createLabel("Tình trạng:"), gbc);
        gbc.gridx = 1; 
        cmbTinhTrang = new JComboBox<>(new String[]{"Mới", "Đang sử dụng", "Hỏng", "Thanh lý"});
        cmbTinhTrang.setBackground(Color.WHITE);
        cmbTinhTrang.setPreferredSize(new Dimension(200, 35));
        pnl.add(cmbTinhTrang, gbc);

        gbc.gridx = 2; pnl.add(createLabel("Giá trị (VNĐ):"), gbc);
        gbc.gridx = 3; txtGiaTri = new JTextField(); setupTextField(txtGiaTri); pnl.add(txtGiaTri, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0; pnl.add(createLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; 
        txtGhiChu = new JTextField(); setupTextField(txtGhiChu); 
        pnl.add(txtGhiChu, gbc);

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        btnThem = new ModernButton("Thêm", new Color(22, 163, 74), new Color(21, 128, 61));
        btnThem.setPreferredSize(new Dimension(100, 38));
        btnSua = new ModernButton("Sửa", new Color(234, 179, 8), new Color(202, 138, 4));
        btnSua.setPreferredSize(new Dimension(100, 38));
        btnXoa = new ModernButton("Xóa", new Color(220, 38, 38), new Color(185, 28, 28));
        btnXoa.setPreferredSize(new Dimension(100, 38));
        btnLamMoi = new ModernButton("Làm mới", new Color(100, 116, 139), new Color(71, 85, 105));
        btnLamMoi.setPreferredSize(new Dimension(100, 38));

        btnPanel.add(btnThem); btnPanel.add(btnSua); btnPanel.add(btnXoa); btnPanel.add(btnLamMoi);
        pnl.add(btnPanel, gbc);

        btnThem.addActionListener(e -> themTaiSan());
        btnSua.addActionListener(e -> suaTaiSan());
        btnXoa.addActionListener(e -> xoaTaiSan());
        btnLamMoi.addActionListener(e -> clearForm());

        return pnl;
    }

    private JPanel createTablePanel() {
        String[] cols = {"Mã TS", "Tên Tài Sản", "Tình Trạng", "Giá Trị", "Ghi Chú"};
        modelTaiSan = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tableTaiSan = new JTable(modelTaiSan);
        setupTable(tableTaiSan);

        tableTaiSan.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableTaiSan.getSelectedRow();
                if (row >= 0) {
                    txtMaTS.setText(modelTaiSan.getValueAt(row, 0).toString());
                    txtMaTS.setEditable(false);
                    txtTenTS.setText(modelTaiSan.getValueAt(row, 1).toString());
                    cmbTinhTrang.setSelectedItem(modelTaiSan.getValueAt(row, 2).toString());
                    
                    String val = modelTaiSan.getValueAt(row, 3).toString().replace(",", "").replace(".", "");
                    txtGiaTri.setText(val);
                    
                    Object ghiChu = modelTaiSan.getValueAt(row, 4);
                    txtGhiChu.setText(ghiChu != null ? ghiChu.toString() : "");
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tableTaiSan);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        RoundedPanel tableContainer = new RoundedPanel(15, Color.WHITE);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableContainer.add(scroll, BorderLayout.CENTER);

        return tableContainer;
    }

    private void loadData() {
        modelTaiSan.setRowCount(0);
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tai_san")) {
            while (rs.next()) {
                double giaTri = rs.getDouble("gia_tri");
                modelTaiSan.addRow(new Object[]{
                    rs.getString("ma_ts"), rs.getString("ten_ts"), rs.getString("tinh_trang"),
                    df.format(giaTri), rs.getString("ghi_chu")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void themTaiSan() {
        if (txtMaTS.getText().isEmpty() || txtTenTS.getText().isEmpty()) {
            Toast.showError("Vui lòng nhập Mã và Tên tài sản!"); return;
        }
        String sql = "INSERT INTO tai_san (ma_ts, ten_ts, tinh_trang, gia_tri, ghi_chu) VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, txtMaTS.getText());
            p.setString(2, txtTenTS.getText());
            p.setString(3, cmbTinhTrang.getSelectedItem().toString());
            p.setDouble(4, parseMoney(txtGiaTri.getText()));
            p.setString(5, txtGhiChu.getText());
            p.executeUpdate();
            Toast.show("Thêm thành công!");
            clearForm(); loadData();
        } catch (Exception e) { 
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi thêm: " + e.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaTaiSan() {
        if (txtMaTS.getText().isEmpty()) { Toast.showError("Chọn tài sản để sửa!"); return; }
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement p = conn.prepareStatement(
                 "UPDATE tai_san SET ten_ts=?, tinh_trang=?, gia_tri=?, ghi_chu=? WHERE ma_ts=?")) {
            p.setString(1, txtTenTS.getText());
            p.setString(2, cmbTinhTrang.getSelectedItem().toString());
            p.setDouble(3, parseMoney(txtGiaTri.getText()));
            p.setString(4, txtGhiChu.getText());
            p.setString(5, txtMaTS.getText());
            p.executeUpdate();
            Toast.show("Cập nhật thành công!");
            clearForm(); loadData();
        } catch (Exception e) { Toast.showError("Lỗi: " + e.getMessage()); }
    }

    private void xoaTaiSan() {
        if (txtMaTS.getText().isEmpty()) { Toast.showError("Chọn tài sản để xóa!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Xóa tài sản này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseHandler.connect();
                 PreparedStatement p = conn.prepareStatement("DELETE FROM tai_san WHERE ma_ts=?")) {
                p.setString(1, txtMaTS.getText());
                p.executeUpdate();
                Toast.show("Đã xóa!");
                clearForm(); loadData();
            } catch (Exception e) { Toast.showError("Lỗi: " + e.getMessage()); }
        }
    }

    private void clearForm() {
        txtMaTS.setText(""); txtMaTS.setEditable(true);
        txtTenTS.setText(""); txtGiaTri.setText(""); txtGhiChu.setText("");
        cmbTinhTrang.setSelectedIndex(0); tableTaiSan.clearSelection();
    }

    private double parseMoney(String text) {
        try {
            if (text == null || text.trim().isEmpty()) return 0;
            return Double.parseDouble(text.replace(",", "").replace(".", "").trim());
        } catch (Exception e) { return 0; }
    }

    private void setupTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(200, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private void setupTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(100, 45));
    }
}