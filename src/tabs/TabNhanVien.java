package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import MainApp.QuanLyNhanVienGUI;
import dataa.DatabaseHandler;
import objects.NhanVien;
import objects.PhongBan; 
import ui.components.*;
import ui.dialogs.DialogThemNhanVien;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabNhanVien extends JPanel {

    private QuanLyNhanVienGUI parent;
    private JTable tableNV;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbPhongBanLoc;
    private JTextField txtTimKiem;
    private ModernButton btnThem, btnXoa, btnSua, btnTimKiem, btnRefresh;

    public TabNhanVien(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(241, 245, 249)); 
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        RoundedPanel pnlToolbar = new RoundedPanel(15, Color.WHITE);
        pnlToolbar.setPreferredSize(new Dimension(1000, 80));
        pnlToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 20));

        pnlToolbar.add(new JLabel("Lọc PB:"));
        cmbPhongBanLoc = new JComboBox<>();
        cmbPhongBanLoc.setPreferredSize(new Dimension(180, 35));
        cmbPhongBanLoc.setBackground(Color.WHITE);
        updatePhongBanComboBox(); 
        pnlToolbar.add(cmbPhongBanLoc);

        txtTimKiem = new JTextField(15);
        txtTimKiem.setPreferredSize(new Dimension(200, 35));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        pnlToolbar.add(txtTimKiem);
        
        btnTimKiem = new ModernButton("Tìm kiếm", new Color(59, 130, 246), new Color(37, 99, 235));
        btnTimKiem.setPreferredSize(new Dimension(100, 35));
        pnlToolbar.add(btnTimKiem);

        pnlToolbar.add(Box.createHorizontalStrut(30)); 

        btnThem = new ModernButton("+ Thêm Mới", new Color(22, 163, 74), new Color(21, 128, 61));
        btnThem.setPreferredSize(new Dimension(120, 35));
        
        btnSua = new ModernButton("Sửa", new Color(234, 179, 8), new Color(202, 138, 4));
        btnSua.setPreferredSize(new Dimension(80, 35));
        
        btnXoa = new ModernButton("Xóa", new Color(220, 38, 38), new Color(185, 28, 28));
        btnXoa.setPreferredSize(new Dimension(80, 35));

        btnRefresh = new ModernButton("Tải lại", new Color(100, 116, 139), new Color(71, 85, 105));
        btnRefresh.setPreferredSize(new Dimension(80, 35));

        pnlToolbar.add(btnThem);
        pnlToolbar.add(btnSua);
        pnlToolbar.add(btnXoa);
        pnlToolbar.add(btnRefresh);

        add(pnlToolbar, BorderLayout.NORTH);

        //BẢNG DỮ LIỆU
        String[] columnNames = {"Mã NV", "Họ Tên", "Phòng Ban", "Email", "SĐT", "Ngày Sinh", "Thâm Niên"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tableNV = new JTable(tableModel);
        tableNV.setRowHeight(35);
        tableNV.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableNV.setSelectionBackground(new Color(219, 234, 254)); 
        tableNV.setSelectionForeground(Color.BLACK);
        tableNV.setShowVerticalLines(false);
        tableNV.setGridColor(new Color(241, 245, 249));
        
        JTableHeader header = tableNV.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(100, 45));

        JScrollPane scrollPane = new JScrollPane(tableNV);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableContainer.add(scrollPane);

        add(tableContainer, BorderLayout.CENTER);

        //XỬ LÝ SỰ KIỆN
        setupActions();
        refreshTableNV(); 
    }

    private void setupActions() {
        cmbPhongBanLoc.addActionListener(e -> applyFilters());
        btnTimKiem.addActionListener(e -> applyFilters());
        txtTimKiem.addActionListener(e -> applyFilters());

        //CHỨC NĂNG THÊM
        btnThem.addActionListener(e -> {
            DialogThemNhanVien dialog = new DialogThemNhanVien(parent, null);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String maNV = generateSmartMaNV(parent.danhSachNV);
                String hoTen = dialog.getHoTen();
                String phongBan = dialog.getPhongBan(); 
                String sdt = dialog.getSDT();
                String email = dialog.getEmail();
                String ngaySinh = dialog.getNgaySinh();
                String cccd = dialog.getCCCD();
                
                String sql = "INSERT INTO nhan_vien(ma_nv, ho_ten, phong_ban, sdt, email, ngay_sinh, cccd, tham_nien) VALUES(?,?,?,?,?,?,?,?)";
                try (Connection conn = DatabaseHandler.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, maNV);
                    pstmt.setString(2, hoTen);
                    pstmt.setString(3, phongBan);
                    pstmt.setString(4, sdt);
                    pstmt.setString(5, email);
                    pstmt.setString(6, ngaySinh);
                    pstmt.setString(7, cccd);
                    pstmt.setInt(8, 0); 
                    pstmt.executeUpdate();
                    
                    NhanVien nvMoi = new NhanVien(maNV, hoTen, phongBan, sdt, email, ngaySinh, cccd, 0);
                    parent.danhSachNV.add(nvMoi);
                    
                    Toast.show("Đã thêm: " + hoTen);
                    parent.ghiNhatKy("Thêm nhân viên", "Tạo mới NV: " + maNV);
                    
                    cmbPhongBanLoc.setSelectedIndex(0); 
                    txtTimKiem.setText("");
                    
                    //Cập nhật toàn bộ hệ thống (Dashboard, PhongBan Count)
                    parent.refreshAllTabs(); 

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.showError("Lỗi thêm: " + ex.getMessage());
                }
            }
        });

        //CHỨC NĂNG SỬA
        btnSua.addActionListener(e -> {
            int selectedRow = tableNV.getSelectedRow();
            if (selectedRow < 0) {
                Toast.showError("Vui lòng chọn nhân viên cần sửa!");
                return;
            }
            
            String maNV = (String) tableModel.getValueAt(selectedRow, 0);
            
            NhanVien nvCanSua = parent.danhSachNV.stream()
                .filter(nv -> nv.getMaNhanVien().equals(maNV))
                .findFirst().orElse(null);
            
            if (nvCanSua == null) return;

            DialogThemNhanVien dialog = new DialogThemNhanVien(parent, nvCanSua);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String hoTenMoi = dialog.getHoTen();
                String pbMoi = dialog.getPhongBan();
                String sdtMoi = dialog.getSDT();
                String emailMoi = dialog.getEmail();
                String cccdMoi = dialog.getCCCD();
                String nsMoi = dialog.getNgaySinh();
                
                String sql = "UPDATE nhan_vien SET ho_ten=?, phong_ban=?, sdt=?, email=?, ngay_sinh=?, cccd=? WHERE ma_nv=?";
                try (Connection conn = DatabaseHandler.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setString(1, hoTenMoi);
                    pstmt.setString(2, pbMoi);
                    pstmt.setString(3, sdtMoi);
                    pstmt.setString(4, emailMoi);
                    pstmt.setString(5, nsMoi);
                    pstmt.setString(6, cccdMoi);
                    pstmt.setString(7, maNV);
                    pstmt.executeUpdate();
                    
                    nvCanSua.setHoTen(hoTenMoi);
                    nvCanSua.setPhongBan(pbMoi);
                    nvCanSua.setSdt(sdtMoi);
                    nvCanSua.setEmail(emailMoi);
                    nvCanSua.setCccd(cccdMoi);
                    nvCanSua.setNgaySinh(nsMoi);
                    
                    Toast.show("Cập nhật thành công!");
                    parent.ghiNhatKy("Sửa nhân viên", "Cập nhật NV: " + maNV);
                    
                    //Đồng bộ lại Dashboard và các tab khác
                    parent.refreshAllTabs();
                    
                } catch (Exception ex) {
                    Toast.showError("Lỗi cập nhật: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        //CHỨC NĂNG XÓA
        btnXoa.addActionListener(e -> {
            int selectedRow = tableNV.getSelectedRow();
            if (selectedRow >= 0) {
                String maNV = (String) tableModel.getValueAt(selectedRow, 0);
                String tenNV = (String) tableModel.getValueAt(selectedRow, 1);
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Bạn chắc chắn muốn xóa: " + tenNV + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseHandler.connect();
                         PreparedStatement pstmt = conn.prepareStatement("DELETE FROM nhan_vien WHERE ma_nv = ?")) {
                        pstmt.setString(1, maNV);
                        pstmt.executeUpdate();
                        
                        parent.danhSachNV.removeIf(nv -> nv.getMaNhanVien().equals(maNV));
                        
                        Toast.show("Đã xóa thành công!");
                        
                        //Đồng bộ lại toàn bộ
                        parent.refreshAllTabs();
                        
                    } catch (Exception ex) {
                        Toast.showError("Lỗi xóa: " + ex.getMessage());
                    }
                }
            } else {
                Toast.showError("Vui lòng chọn nhân viên cần xóa!");
            }
        });

        btnRefresh.addActionListener(e -> {
            txtTimKiem.setText("");
            cmbPhongBanLoc.setSelectedIndex(0);
            parent.refreshAllTabs(); //Refresh toàn bộ hệ thống
            Toast.show("Đã làm mới dữ liệu!");
        });
    }

    private String generateSmartMaNV(List<NhanVien> list) {
        int maxId = 0;
        for (NhanVien nv : list) {
            try {
                String numPart = nv.getMaNhanVien().toUpperCase().replace("NV", "").trim();
                int id = Integer.parseInt(numPart);
                if (id > maxId) maxId = id;
            } catch (Exception e) {}
        }
        return "NV" + String.format("%03d", maxId + 1);
    }

    private void applyFilters() {
        String keyword = txtTimKiem.getText().toLowerCase().trim();
        String selectedPB = (String) cmbPhongBanLoc.getSelectedItem();
        
        if (parent.danhSachNV == null) parent.danhSachNV = new ArrayList<>();

        List<NhanVien> ketQua = parent.danhSachNV.stream()
            .filter(nv -> {
                boolean matchPB = true;
                if (selectedPB != null && !selectedPB.equals("Tất cả")) {
                    try {
                        String pbNV = nv.getPhongBan().toLowerCase().trim();
                        String pbLoc = selectedPB.toLowerCase();
                        String maPbLoc = pbLoc.split("-")[0].trim();
                        if (pbNV.equals(maPbLoc) || pbLoc.contains(pbNV)) matchPB = true;
                        else matchPB = false;
                    } catch (Exception e) { matchPB = false; }
                }
                
                boolean matchSearch = true;
                if (!keyword.isEmpty()) {
                    matchSearch = nv.getHoTen().toLowerCase().contains(keyword) ||
                                  nv.getMaNhanVien().toLowerCase().contains(keyword) ||
                                  nv.getEmail().toLowerCase().contains(keyword);
                }
                return matchPB && matchSearch;
            })
            .collect(Collectors.toList());
            
        hienThiDanhSach(ketQua);
    }

    private void hienThiDanhSach(List<NhanVien> list) {
        tableModel.setRowCount(0); 
        for (NhanVien nv : list) {
            String tenPhongBanDayDu = getTenPhongBan(nv.getPhongBan());

            tableModel.addRow(new Object[]{
                nv.getMaNhanVien(),
                nv.getHoTen(),
                tenPhongBanDayDu, 
                nv.getEmail(),
                nv.getSdt(),
                nv.getNgaySinh(),
                nv.getThamNien() + " năm"
            });
        }
    }

    private String getTenPhongBan(String maPB) {
        if (parent.danhSachPB != null) {
            for (PhongBan pb : parent.danhSachPB) {
                if (pb.getMaPhongBan().equalsIgnoreCase(maPB)) {
                    return pb.getTenPhongBan(); 
                }
            }
        }
        return maPB;
    }

    public void refreshTableNV() {
        if (parent.danhSachNV != null) {
            hienThiDanhSach(parent.danhSachNV);
        }
    }

    public void updatePhongBanComboBox() {
        if (cmbPhongBanLoc != null) {
            ActionListener[] listeners = cmbPhongBanLoc.getActionListeners();
            for (ActionListener l : listeners) cmbPhongBanLoc.removeActionListener(l);

            cmbPhongBanLoc.removeAllItems();
            cmbPhongBanLoc.addItem("Tất cả");
            
            if (parent.danhSachPB != null) {
                for (objects.PhongBan pb : parent.danhSachPB) {
                    cmbPhongBanLoc.addItem(pb.getMaPhongBan() + " - " + pb.getTenPhongBan());
                }
            }
            
            for (ActionListener l : listeners) cmbPhongBanLoc.addActionListener(l);
        }
    }
}