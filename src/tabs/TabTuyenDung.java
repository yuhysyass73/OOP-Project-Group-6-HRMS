package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

import MainApp.QuanLyNhanVienGUI;
import dataa.DatabaseHandler;
import ui.components.*;
import ui.dialogs.DialogThemUngVien;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TabTuyenDung extends JPanel {

    private QuanLyNhanVienGUI parent;
    private JTable tableUV;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private ModernButton btnThem, btnSua, btnXoa, btnRefresh, btnTimKiem;

    public TabTuyenDung(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(241, 245, 249)); 
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Đảm bảo bảng Database tồn tại
        ensureTableExists();

        //TOOLBAR
        RoundedPanel pnlToolbar = new RoundedPanel(15, Color.WHITE);
        pnlToolbar.setPreferredSize(new Dimension(1000, 80));
        pnlToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 20));

        //Tìm kiếm
        pnlToolbar.add(new JLabel("Tìm hồ sơ:"));
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(250, 35));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        pnlToolbar.add(txtTimKiem);

        btnTimKiem = new ModernButton("Tìm", new Color(59, 130, 246), new Color(37, 99, 235));
        btnTimKiem.setPreferredSize(new Dimension(80, 35));
        pnlToolbar.add(btnTimKiem);

        pnlToolbar.add(Box.createHorizontalStrut(30));

        //Các nút chức năng
        btnThem = new ModernButton("+ Ứng Viên", new Color(22, 163, 74), new Color(21, 128, 61));
        btnThem.setPreferredSize(new Dimension(120, 35));
        
        btnSua = new ModernButton("Cập Nhật", new Color(234, 179, 8), new Color(202, 138, 4));
        btnSua.setPreferredSize(new Dimension(100, 35));
        
        btnXoa = new ModernButton("Xóa Hồ Sơ", new Color(220, 38, 38), new Color(185, 28, 28));
        btnXoa.setPreferredSize(new Dimension(110, 35));

        btnRefresh = new ModernButton("Tải lại", new Color(100, 116, 139), new Color(71, 85, 105));
        btnRefresh.setPreferredSize(new Dimension(90, 35));

        pnlToolbar.add(btnThem); pnlToolbar.add(btnSua); pnlToolbar.add(btnXoa); pnlToolbar.add(btnRefresh);
        add(pnlToolbar, BorderLayout.NORTH);

        //BẢNG DỮ LIỆU
        String[] columns = {"Mã UV", "Họ Tên", "Vị Trí", "SĐT", "Email", "Ngày Nộp", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableUV = new JTable(tableModel);
        tableUV.setRowHeight(35);
        tableUV.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableUV.setSelectionBackground(new Color(219, 234, 254));
        tableUV.setSelectionForeground(Color.BLACK);
        tableUV.setShowVerticalLines(false);
        tableUV.setGridColor(new Color(241, 245, 249));

        tableUV.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("Mới nhận".equals(status)) setForeground(new Color(59, 130, 246));
                else if ("Đã tuyển dụng".equals(status)) setForeground(new Color(22, 163, 74));
                else if ("Từ chối".equals(status)) setForeground(new Color(220, 38, 38));
                else setForeground(Color.BLACK);
                return c;
            }
        });

        JTableHeader header = tableUV.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(100, 45));

        JScrollPane scrollPane = new JScrollPane(tableUV);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableContainer.add(scrollPane);
        add(tableContainer, BorderLayout.CENTER);

        //LOGIC
        setupActions();
        loadDataFromDB(null);
    }

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS tuyen_dung (" +
                     "ma_uv TEXT PRIMARY KEY, " +
                     "ho_ten TEXT, " +
                     "vi_tri TEXT, " +
                     "sdt TEXT, " +
                     "email TEXT, " +
                     "ngay_nop TEXT, " +
                     "trang_thai TEXT)";
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupActions() {
        //Tìm kiếm
        ActionListener searchAction = e -> loadDataFromDB(txtTimKiem.getText().trim());
        btnTimKiem.addActionListener(searchAction);
        txtTimKiem.addActionListener(searchAction);

        //Thêm
        btnThem.addActionListener(e -> {
            DialogThemUngVien dialog = new DialogThemUngVien(parent, null, null, null, null, null, null);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String maUV = generateNextMaUV();
                String ngayNop = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                
                String sql = "INSERT INTO tuyen_dung(ma_uv, ho_ten, vi_tri, sdt, email, ngay_nop, trang_thai) VALUES(?,?,?,?,?,?,?)";
                try (Connection conn = DatabaseHandler.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, maUV);
                    pstmt.setString(2, dialog.getHoTen());
                    pstmt.setString(3, dialog.getViTri());
                    pstmt.setString(4, dialog.getSDT());
                    pstmt.setString(5, dialog.getEmail());
                    pstmt.setString(6, ngayNop);
                    pstmt.setString(7, dialog.getTrangThai());
                    pstmt.executeUpdate();
                    
                    Toast.show("Đã thêm ứng viên: " + dialog.getHoTen());
                    parent.ghiNhatKy("Tuyển dụng", "Thêm hồ sơ: " + maUV);
                    loadDataFromDB(null);
                } catch (Exception ex) {
                    Toast.showError("Lỗi: " + ex.getMessage());
                }
            }
        });

        //Sửa
        btnSua.addActionListener(e -> {
            int row = tableUV.getSelectedRow();
            if (row < 0) {
                Toast.showError("Chọn ứng viên cần sửa!");
                return;
            }
            //Lấy dữ liệu từ bảng để điền vào Dialog
            String maUV = (String) tableModel.getValueAt(row, 0);
            String hoTen = (String) tableModel.getValueAt(row, 1);
            String viTri = (String) tableModel.getValueAt(row, 2);
            String sdt = (String) tableModel.getValueAt(row, 3);
            String email = (String) tableModel.getValueAt(row, 4);
            String trangThai = (String) tableModel.getValueAt(row, 6);

            DialogThemUngVien dialog = new DialogThemUngVien(parent, maUV, hoTen, viTri, email, sdt, trangThai);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String sql = "UPDATE tuyen_dung SET ho_ten=?, vi_tri=?, sdt=?, email=?, trang_thai=? WHERE ma_uv=?";
                try (Connection conn = DatabaseHandler.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, dialog.getHoTen());
                    pstmt.setString(2, dialog.getViTri());
                    pstmt.setString(3, dialog.getSDT());
                    pstmt.setString(4, dialog.getEmail());
                    pstmt.setString(5, dialog.getTrangThai());
                    pstmt.setString(6, maUV);
                    pstmt.executeUpdate();
                    
                    Toast.show("Cập nhật thành công!");
                    parent.ghiNhatKy("Tuyển dụng", "Cập nhật hồ sơ: " + maUV);
                    loadDataFromDB(null);
                } catch (Exception ex) {
                    Toast.showError("Lỗi: " + ex.getMessage());
                }
            }
        });

        //Xóa
        btnXoa.addActionListener(e -> {
            int row = tableUV.getSelectedRow();
            if (row < 0) {
                Toast.showError("Chọn ứng viên để xóa!");
                return;
            }
            String maUV = (String) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Xóa hồ sơ " + maUV + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseHandler.connect();
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM tuyen_dung WHERE ma_uv=?")) {
                    pstmt.setString(1, maUV);
                    pstmt.executeUpdate();
                    
                    Toast.show("Đã xóa hồ sơ!");
                    parent.ghiNhatKy("Tuyển dụng", "Xóa hồ sơ: " + maUV);
                    loadDataFromDB(null);
                } catch (Exception ex) {
                    Toast.showError("Lỗi xóa: " + ex.getMessage());
                }
            }
        });

        //Refresh
        btnRefresh.addActionListener(e -> {
            txtTimKiem.setText("");
            loadDataFromDB(null);
            Toast.show("Đã làm mới danh sách!");
        });
    }

    //Load dữ liệu từ DB lên bảng
    private void loadDataFromDB(String keyword) {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM tuyen_dung";
        if (keyword != null && !keyword.isEmpty()) {
            sql += " WHERE ho_ten LIKE '%" + keyword + "%' OR vi_tri LIKE '%" + keyword + "%'";
        }
        
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("ma_uv"),
                    rs.getString("ho_ten"),
                    rs.getString("vi_tri"),
                    rs.getString("sdt"),
                    rs.getString("email"),
                    rs.getString("ngay_nop"),
                    rs.getString("trang_thai")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Sinh mã UV tự động (UV001, UV002...)
    private String generateNextMaUV() {
        int maxId = 0;
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ma_uv FROM tuyen_dung")) {
            while (rs.next()) {
                String s = rs.getString("ma_uv");
                if (s.startsWith("UV")) {
                    try {
                        int id = Integer.parseInt(s.substring(2));
                        if (id > maxId) maxId = id;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "UV" + String.format("%03d", maxId + 1);
    }
}