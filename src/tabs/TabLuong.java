package tabs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import MainApp.QuanLyNhanVienGUI;
import dataa.DatabaseHandler;
import ui.components.*;

import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;

public class TabLuong extends JPanel {

    private QuanLyNhanVienGUI parent;
    private JTable tableLuong;
    private DefaultTableModel modelLuong;
    private JTextField txtTimKiem;
    private ModernButton btnTinhLuong, btnXuatExcel, btnRefresh;
    private JLabel lblTongChiPhi;

    // Định dạng tiền tệ
    private DecimalFormat df = new DecimalFormat("#,### VNĐ");

    // Hằng số tính toán
    private final double LUONG_CO_BAN_MAC_DINH = 5000000;
    private final double HE_SO_PHAT = 500000;       // 500k / 1 điểm phạt
    private final double HE_SO_THUONG_DA = 5000000; // 5 triệu / 1 điểm độ phức tạp

    public TabLuong(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        //Đảm bảo có đủ cột trong DB
        ensureColumnsExist();

        add(createToolbar(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        //Tải dữ liệu
        refreshLuongTable();
    }

    private void ensureColumnsExist() {
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement()) {
            try { stmt.execute("ALTER TABLE nhan_vien ADD COLUMN luong_co_ban REAL DEFAULT 2000000"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE nhan_vien ADD COLUMN phu_cap REAL DEFAULT 0"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE nhan_vien ADD COLUMN diem_vi_pham INTEGER DEFAULT 0"); } catch (Exception e) {}
            try { stmt.execute("ALTER TABLE nhan_vien ADD COLUMN diem_thuong_du_an INTEGER DEFAULT 0"); } catch (Exception e) {}
        } catch (Exception e) { e.printStackTrace(); }
    }

    //TOOLBAR
    private JPanel createToolbar() {
        RoundedPanel pnl = new RoundedPanel(15, Color.WHITE);
        pnl.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 20));
        pnl.setPreferredSize(new Dimension(1000, 80));

        JLabel lblTitle = new JLabel("BẢNG LƯƠNG NHÂN VIÊN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        pnl.add(lblTitle);

        pnl.add(Box.createHorizontalStrut(30));

        pnl.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(15);
        setupTextField(txtTimKiem);
        txtTimKiem.addActionListener(e -> refreshLuongTable());
        pnl.add(txtTimKiem);

        btnRefresh = new ModernButton("Tải lại", new Color(100, 116, 139), new Color(71, 85, 105));
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnRefresh.addActionListener(e -> refreshLuongTable());
        pnl.add(btnRefresh);

        btnXuatExcel = new ModernButton("Xuất Báo Cáo", new Color(22, 163, 74), new Color(21, 128, 61));
        btnXuatExcel.setPreferredSize(new Dimension(130, 35));
        btnXuatExcel.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tính năng đang phát triển!"));
        pnl.add(btnXuatExcel);

        return pnl;
    }

    //TABLE
    private JPanel createTablePanel() {
        String[] cols = {"Mã NV", "Họ Tên", "Lương CB", "Phụ Cấp", "Thưởng Dự Án (+)", "Phạt (-)", "THỰC LĨNH"};
        
        modelLuong = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tableLuong = new JTable(modelLuong);
        setupTable(tableLuong);

        //Căn phải cho các cột tiền tệ
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        for(int i=2; i<=6; i++) {
            tableLuong.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }

        //Tô đậm cột Thực Lĩnh
        tableLuong.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setForeground(new Color(22, 163, 74)); // Màu xanh lá
                setHorizontalAlignment(JLabel.RIGHT);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tableLuong);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel pnl = new RoundedPanel(15, Color.WHITE);
        pnl.setLayout(new BorderLayout());
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnl.add(scroll, BorderLayout.CENTER);

        return pnl;
    }

    //FOOTER
    private JPanel createFooterPanel() {
        RoundedPanel pnl = new RoundedPanel(15, Color.WHITE);
        pnl.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnl.setPreferredSize(new Dimension(1000, 60));

        lblTongChiPhi = new JLabel("Tổng chi phí lương tháng này: 0 VNĐ");
        lblTongChiPhi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongChiPhi.setForeground(new Color(185, 28, 28));

        pnl.add(lblTongChiPhi);
        return pnl;
    }

    //LOGIC TÍNH TOÁN
    public void refreshLuongTable() {
        modelLuong.setRowCount(0);
        String keyword = txtTimKiem.getText().trim();
        double totalSalaryFund = 0;

        String sql = "SELECT * FROM nhan_vien WHERE ho_ten LIKE '%" + keyword + "%' OR ma_nv LIKE '%" + keyword + "%'";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maNV = rs.getString("ma_nv");
                String hoTen = rs.getString("ho_ten");
                
                double luongCB = rs.getDouble("luong_co_ban");
                if(luongCB == 0) luongCB = LUONG_CO_BAN_MAC_DINH;
                
                double phuCap = rs.getDouble("phu_cap");
                int diemPhat = rs.getInt("diem_vi_pham");
                int diemThuongDA = rs.getInt("diem_thuong_du_an");
                int thamnienn=rs.getInt("tham_nien");

                double tienPhat = diemPhat * HE_SO_PHAT;
                double tienThuongDA = diemThuongDA * HE_SO_THUONG_DA;
                //Tính lương cb bằng cách lấy 2 triệu nhân với (2.5+ thâm niên)
                luongCB = luongCB * (2.5 + thamnienn);
                double thucLinh = luongCB + phuCap + tienThuongDA - tienPhat;
                if (thucLinh < 0) thucLinh = 0;

                totalSalaryFund += thucLinh;

                modelLuong.addRow(new Object[]{
                    maNV,
                    hoTen,
                    df.format(luongCB),
                    df.format(phuCap),
                    df.format(tienThuongDA),
                    df.format(tienPhat),
                    df.format(thucLinh)
                });
            }
            
            lblTongChiPhi.setText("Tổng chi phí lương tháng này: " + df.format(totalSalaryFund));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(200, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            BorderFactory.createEmptyBorder(0, 8, 0, 8)
        ));
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