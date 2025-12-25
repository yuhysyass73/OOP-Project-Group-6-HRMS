package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import MainApp.QuanLyNhanVienGUI;
import dataa.DatabaseHandler;
import objects.NhanVien;
import objects.PhongBan;
import ui.components.*;
import ui.dialogs.DialogThemPhongBan;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabPhongBan extends JPanel {

    private QuanLyNhanVienGUI parent;
    private JTable tablePB;
    private DefaultTableModel tableModel;
    private JTextField txtTimKiem;
    private ModernButton btnThem, btnSua, btnXoa, btnRefresh, btnTimKiem;

    public TabPhongBan(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(241, 245, 249)); 
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //TOOLBAR
        RoundedPanel pnlToolbar = new RoundedPanel(15, Color.WHITE);
        pnlToolbar.setPreferredSize(new Dimension(1000, 80));
        pnlToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 20));

        //Ô tìm kiếm
        pnlToolbar.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(250, 35));
        txtTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));
        pnlToolbar.add(txtTimKiem);

        btnTimKiem = new ModernButton("Tìm", new Color(59, 130, 246), new Color(37, 99, 235));
        btnTimKiem.setPreferredSize(new Dimension(80, 35));
        pnlToolbar.add(btnTimKiem);

        pnlToolbar.add(Box.createHorizontalStrut(30));

        //Nút chức năng
        btnThem = new ModernButton("+ Thêm PB", new Color(22, 163, 74), new Color(21, 128, 61));
        btnThem.setPreferredSize(new Dimension(120, 35));
        
        btnSua = new ModernButton("Sửa Tên", new Color(234, 179, 8), new Color(202, 138, 4));
        btnSua.setPreferredSize(new Dimension(100, 35));
        
        btnXoa = new ModernButton("Xóa PB", new Color(220, 38, 38), new Color(185, 28, 28));
        btnXoa.setPreferredSize(new Dimension(100, 35));

        btnRefresh = new ModernButton("Tải lại", new Color(100, 116, 139), new Color(71, 85, 105));
        btnRefresh.setPreferredSize(new Dimension(100, 35));

        pnlToolbar.add(btnThem);
        pnlToolbar.add(btnSua);
        pnlToolbar.add(btnXoa);
        pnlToolbar.add(btnRefresh);

        add(pnlToolbar, BorderLayout.NORTH);

        //BẢNG DỮ LIỆU
        String[] columns = {"Mã Phòng Ban", "Tên Phòng Ban", "Số Lượng Nhân Sự"};
        
        //Chặn sửa trực tiếp
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablePB = new JTable(tableModel);
        
        tablePB.setRowHeight(35);
        tablePB.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablePB.setSelectionBackground(new Color(219, 234, 254));
        tablePB.setSelectionForeground(Color.BLACK);
        tablePB.setShowVerticalLines(false);
        tablePB.setGridColor(new Color(241, 245, 249));

        JTableHeader header = tablePB.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(100, 45));

        JScrollPane scrollPane = new JScrollPane(tablePB);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableContainer.add(scrollPane);

        add(tableContainer, BorderLayout.CENTER);

        //XỬ LÝ SỰ KIỆN
        setupActions();
        
        //Load dữ liệu
        refreshTablePB();
    }

    private void setupActions() {
        //Tìm kiếm
        ActionListener searchAction = e -> {
            String keyword = txtTimKiem.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                hienThiDanhSach(parent.danhSachPB);
            } else {
                List<PhongBan> ketQua = parent.danhSachPB.stream()
                    .filter(pb -> pb.getTenPhongBan().toLowerCase().contains(keyword) || 
                                  pb.getMaPhongBan().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());
                hienThiDanhSach(ketQua);
            }
        };
        btnTimKiem.addActionListener(searchAction);
        txtTimKiem.addActionListener(searchAction);

        //Thêm mới
        btnThem.addActionListener(e -> {
            DialogThemPhongBan dialog = new DialogThemPhongBan(parent, null);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                String maMoi = dialog.getMaPB();
                String tenMoi = dialog.getTenPB();

                boolean exists = parent.danhSachPB.stream().anyMatch(pb -> pb.getMaPhongBan().equalsIgnoreCase(maMoi));
                if (exists) {
                    Toast.showError("Mã phòng ban '" + maMoi + "' đã tồn tại!");
                    return;
                }

                String sql = "INSERT INTO phong_ban(ma_pb, ten_pb) VALUES(?, ?)";
                try (Connection conn = DatabaseHandler.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, maMoi);
                    pstmt.setString(2, tenMoi);
                    pstmt.executeUpdate();

                    parent.danhSachPB.add(new PhongBan(maMoi, tenMoi));
                    
                    Toast.show("Đã thêm phòng ban: " + tenMoi);
                    parent.ghiNhatKy("Thêm Phòng Ban", "Mã: " + maMoi);
                    
                    //Đồng bộ tất cả các tab (Để TabNhanVien thấy PB mới)
                    parent.refreshAllTabs();
                    refreshTablePB();
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.showError("Lỗi thêm: " + ex.getMessage());
                }
            }
        });

        //Sửa Tên
        btnSua.addActionListener(e -> {
            int row = tablePB.getSelectedRow();
            if (row < 0) {
                Toast.showError("Vui lòng chọn phòng ban cần sửa!");
                return;
            }
            String maPB = (String) tableModel.getValueAt(row, 0);
            String tenPB = (String) tableModel.getValueAt(row, 1);
            
            PhongBan pbCu = new PhongBan(maPB, tenPB);
            DialogThemPhongBan dialog = new DialogThemPhongBan(parent, pbCu);
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                String tenMoi = dialog.getTenPB();
                
                String sql = "UPDATE phong_ban SET ten_pb = ? WHERE ma_pb = ?";
                try (Connection conn = DatabaseHandler.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, tenMoi);
                    pstmt.setString(2, maPB);
                    pstmt.executeUpdate();

                    for (PhongBan pb : parent.danhSachPB) {
                        if (pb.getMaPhongBan().equals(maPB)) {
                            pb.setTenPhongBan(tenMoi);
                            break;
                        }
                    }
                    
                    Toast.show("Cập nhật thành công!");
                    parent.ghiNhatKy("Sửa Phòng Ban", "Mã: " + maPB + " -> Tên mới: " + tenMoi);
                    
                    parent.refreshAllTabs(); //Đồng bộ
                    refreshTablePB();
                    
                } catch (Exception ex) {
                    Toast.showError("Lỗi sửa: " + ex.getMessage());
                }
            }
        });

        //Xóa
        btnXoa.addActionListener(e -> {
            int row = tablePB.getSelectedRow();
            if (row < 0) {
                Toast.showError("Chọn phòng ban để xóa!");
                return;
            }
            String maPB = (String) tableModel.getValueAt(row, 0);
            
            //Lấy số lượng từ bảng
            Object val = tableModel.getValueAt(row, 2);
            int soLuongNV = (val instanceof Integer) ? (int)val : Integer.parseInt(val.toString());

            if (soLuongNV > 0) {
                Toast.showError("Cảnh báo: Phòng ban đang có người!");
                JOptionPane.showMessageDialog(this, 
                    "Phòng ban này đang có " + soLuongNV + " nhân sự.\n" +
                    "Bạn phải chuyển nhân viên sang phòng khác trước khi xóa!", 
                    "Không thể xóa", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Xóa phòng ban " + maPB + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseHandler.connect();
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM phong_ban WHERE ma_pb = ?")) {
                    pstmt.setString(1, maPB);
                    pstmt.executeUpdate();

                    parent.danhSachPB.removeIf(pb -> pb.getMaPhongBan().equals(maPB));
                    
                    Toast.show("Đã xóa phòng ban!");
                    parent.ghiNhatKy("Xóa Phòng Ban", "Mã: " + maPB);
                    
                    parent.refreshAllTabs(); //Đồng bộ
                    refreshTablePB();
                    
                } catch (Exception ex) {
                    Toast.showError("Lỗi xóa: " + ex.getMessage());
                }
            }
        });

        //Refresh
        btnRefresh.addActionListener(e -> {
            txtTimKiem.setText("");
            refreshTablePB();
            Toast.show("Đã làm mới dữ liệu!");
        });
    }

    /**
     * HÀM ĐẾM SỐ LƯỢNG NHÂN VIÊN THEO PHÒNG BAN
     * Đếm cả khi nhân viên lưu Mã (KT) và khi nhân viên lưu Tên (Kỹ thuật)
     */
    private void hienThiDanhSach(List<PhongBan> list) {
        tableModel.setRowCount(0);
        
        //Lấy danh sách nhân viên từ RAM (đã được load đầy đủ)
        List<NhanVien> listNV = parent.danhSachNV;
        if (listNV == null) listNV = new ArrayList<>();

        for (PhongBan pb : list) {
            String maPB = pb.getMaPhongBan().trim().toLowerCase();
            String tenPB = pb.getTenPhongBan().trim().toLowerCase();

            
            long count = listNV.stream().filter(nv -> {
                String pbCuaNV = nv.getPhongBan().trim().toLowerCase();
                //Nhân viên thuộc phòng này nếu chuỗi lưu trữ trùng Mã hoặc trùng Tên
                return pbCuaNV.equals(maPB) || pbCuaNV.equals(tenPB) || pbCuaNV.contains(tenPB);
            }).count();

            tableModel.addRow(new Object[]{
                pb.getMaPhongBan(),
                pb.getTenPhongBan(),
                (int) count
            });
        }
    }

    public void refreshTablePB() {
        if (parent.danhSachPB != null) {
            hienThiDanhSach(parent.danhSachPB);
        }
    }
    
    //Delegate methods cho Parent gọi
    public void updatePhongBanComboBox() {
        
        refreshTablePB();
    }
    public void locNhanVienTheoPhongBan() {}
}