package tabs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import MainApp.*;
import objects.*;

public class TabNhanVien extends JPanel {

    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;
    private List<PhongBan> danhSachPB;

    private JComboBox<PhongBan> cmbPhongBanNV;
    private DefaultTableModel modelNV;
    private JTable tableNV;
    private JTextField txtMaNV, txtTenNV, txtSdt, txtEmail, txtNgaySinh, txtCccd, txtThamNien;

    // search
    private JComboBox<String> cmbTieuChiTimKiem;
    private JTextField txtTuKhoaTimKiem;

    public TabNhanVien(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;
        this.danhSachPB = parent.danhSachPB;

        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));


        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));

        searchPanel.add(new JLabel("Tiêu chí:"));
        String[] tieuChi = {"Mã Nhân viên", "Tên Nhân viên"};
        cmbTieuChiTimKiem = new JComboBox<>(tieuChi);
        searchPanel.add(cmbTieuChiTimKiem);

        searchPanel.add(new JLabel("    Từ khóa:"));
        txtTuKhoaTimKiem = new JTextField(20);
        searchPanel.add(txtTuKhoaTimKiem);

        // event when type
        txtTuKhoaTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { xuLyTimKiem(); }
            @Override public void removeUpdate(DocumentEvent e) { xuLyTimKiem(); }
            @Override public void changedUpdate(DocumentEvent e) { xuLyTimKiem(); }
        });

        cmbTieuChiTimKiem.addActionListener(e -> xuLyTimKiem());


        topPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 4, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));

        formPanel.add(new JLabel("Mã NV:"));
        txtMaNV = new JTextField();
        formPanel.add(txtMaNV);

        formPanel.add(new JLabel("Tên NV:"));
        txtTenNV = new JTextField();
        formPanel.add(txtTenNV);

        formPanel.add(new JLabel("Phòng ban:"));
        cmbPhongBanNV = new JComboBox<>();
        formPanel.add(cmbPhongBanNV);

        formPanel.add(new JLabel("SĐT:"));
        txtSdt = new JTextField();
        formPanel.add(txtSdt);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Ngày sinh (dd/mm/yyyy):"));
        txtNgaySinh = new JTextField();
        formPanel.add(txtNgaySinh);

        formPanel.add(new JLabel("CCCD:"));
        txtCccd = new JTextField();
        formPanel.add(txtCccd);

        formPanel.add(new JLabel("Thâm niên (năm):"));
        txtThamNien = new JTextField();
        formPanel.add(txtThamNien);

        topPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnThemNV = new JButton("Thêm");
        JButton btnSuaNV = new JButton("Sửa");
        JButton btnXoaNV = new JButton("Xóa");
        JButton btnLamMoiNV = new JButton("Làm mới");

        btnThemNV.addActionListener(e -> themNhanVien());
        btnSuaNV.addActionListener(e -> suaNhanVien());
        btnXoaNV.addActionListener(e -> xoaNhanVien());
        btnLamMoiNV.addActionListener(e -> lamMoiFormNV());

        buttonPanel.add(btnThemNV);
        buttonPanel.add(btnSuaNV);
        buttonPanel.add(btnXoaNV);
        buttonPanel.add(btnLamMoiNV);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mã NV", "Họ Tên", "Phòng ban", "SĐT", "Email", "Ngày sinh", "CCCD", "Thâm niên (năm)"};
        modelNV = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableNV = new JTable(modelNV);

        tableNV.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiThongTinLenFormNV();
            }
        });

        add(new JScrollPane(tableNV), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // === HÀM XỬ LÝ TÌM KIẾM REAL-TIME ===
    private void xuLyTimKiem() {
        String tuKhoa = txtTuKhoaTimKiem.getText().trim().toLowerCase();
        String tieuChi = (String) cmbTieuChiTimKiem.getSelectedItem();

        modelNV.setRowCount(0); //

        for (NhanVien nv : danhSachNV) {
            boolean thoaMan = false;

            if (tuKhoa.isEmpty()) {
                thoaMan = true;
            } else {
                if ("Mã Nhân viên".equals(tieuChi)) {
                    if (nv.getMaNhanVien().toLowerCase().contains(tuKhoa)) {
                        thoaMan = true;
                    }
                } else if ("Tên Nhân viên".equals(tieuChi)) {
                    if (nv.getHoTen().toLowerCase().contains(tuKhoa)) {
                        thoaMan = true;
                    }
                }
            }

            if (thoaMan) {
                modelNV.addRow(new Object[]{
                        nv.getMaNhanVien(), nv.getHoTen(), nv.getPhongBan(),
                        nv.getSdt(), nv.getEmail(), nv.getNgaySinh(),
                        nv.getCccd(), nv.getThamNien()
                });
            }
        }
    }

    private void hienThiThongTinLenFormNV() {
        int r = tableNV.getSelectedRow();
        if (r == -1) return;
        txtMaNV.setText(modelNV.getValueAt(r, 0).toString());
        txtTenNV.setText(modelNV.getValueAt(r, 1).toString());
        String tenPhongBan = modelNV.getValueAt(r, 2).toString();
        txtSdt.setText(modelNV.getValueAt(r, 3).toString());
        txtEmail.setText(modelNV.getValueAt(r, 4).toString());
        txtNgaySinh.setText(modelNV.getValueAt(r, 5).toString());
        txtCccd.setText(modelNV.getValueAt(r, 6).toString());
        txtThamNien.setText(String.valueOf(modelNV.getValueAt(r, 7)));
        for (int i = 0; i < cmbPhongBanNV.getItemCount(); i++) {
            if (cmbPhongBanNV.getItemAt(i).getTenPhongBan().equals(tenPhongBan)) {
                cmbPhongBanNV.setSelectedIndex(i);
                break;
            }
        }
        txtMaNV.setEditable(false);
    }

    private void themNhanVien() {
        String maNV = txtMaNV.getText();
        String tenNV = txtTenNV.getText();
        PhongBan pb = (PhongBan) cmbPhongBanNV.getSelectedItem();
        String sdt = txtSdt.getText();
        String email = txtEmail.getText();
        String ngaySinh = txtNgaySinh.getText();
        String cccd = txtCccd.getText();
        String thamNienStr = txtThamNien.getText();

        if (maNV.isEmpty() || tenNV.isEmpty() || pb == null) {
            JOptionPane.showMessageDialog(this, "Mã NV, Tên NV và Phòng ban là bắt buộc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (danhSachNV.stream().anyMatch(nv -> nv.getMaNhanVien().equals(maNV))) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int thamNienInt = 0;
        if (!thamNienStr.isEmpty()) {
            try {
                thamNienInt = Integer.parseInt(thamNienStr);
                if (thamNienInt < 0) {
                    JOptionPane.showMessageDialog(this, "Thâm niên phải là một số không âm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Thâm niên phải là một con số (ví dụ: 5).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        NhanVien nv = new NhanVien(maNV, tenNV, pb.getTenPhongBan(), sdt, email, ngaySinh, cccd, thamNienInt);
        danhSachNV.add(nv);

        xuLyTimKiem();

        JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
        lamMoiFormNV();

        parent.locNhanVienTheoPhongBan();
        parent.refreshLuongTable();
        parent.refreshBaoCaoTab();
    }

    private void suaNhanVien() {
        int r = tableNV.getSelectedRow();
        if (r == -1) { return; }

        String maNV = txtMaNV.getText();
        String tenMoi = txtTenNV.getText();
        PhongBan pbMoi = (PhongBan) cmbPhongBanNV.getSelectedItem();
        String sdtMoi = txtSdt.getText();
        String emailMoi = txtEmail.getText();
        String ngaySinhMoi = txtNgaySinh.getText();
        String cccdMoi = txtCccd.getText();
        String thamNienMoiStr = txtThamNien.getText();

        if (tenMoi.isEmpty() || pbMoi == null) { return; }
        int validatedThamNien = 0;
        try {
            if(!thamNienMoiStr.isEmpty()) validatedThamNien = Integer.parseInt(thamNienMoiStr);
        } catch(Exception ex) { return; }

        for(NhanVien nv : danhSachNV) {
            if(nv.getMaNhanVien().equals(maNV)) {
                nv.setHoTen(tenMoi);
                nv.setPhongBan(pbMoi.getTenPhongBan());
                nv.setSdt(sdtMoi);
                nv.setEmail(emailMoi);
                nv.setNgaySinh(ngaySinhMoi);
                nv.setCccd(cccdMoi);
                nv.setThamNien(validatedThamNien);
                break;
            }
        }

        xuLyTimKiem();

        JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
        lamMoiFormNV();

        parent.locNhanVienTheoPhongBan();
        parent.refreshLuongTable();
        parent.refreshBaoCaoTab();
    }

    private void xoaNhanVien() {
        int r = tableNV.getSelectedRow();
        if (r == -1) { return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String maNV = modelNV.getValueAt(r, 0).toString();
            danhSachNV.removeIf(nv -> nv.getMaNhanVien().equals(maNV));

            xuLyTimKiem();

            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
            lamMoiFormNV();

            parent.locNhanVienTheoPhongBan();
            parent.refreshLuongTable();
            parent.refreshBaoCaoTab();
        }
    }

    private void lamMoiFormNV() {
        txtMaNV.setText("");
        txtTenNV.setText("");
        if(cmbPhongBanNV.getItemCount() > 0) cmbPhongBanNV.setSelectedIndex(0);
        txtSdt.setText("");
        txtEmail.setText("");
        txtNgaySinh.setText("");
        txtCccd.setText("");
        txtThamNien.setText("");
        txtMaNV.setEditable(true);
        tableNV.clearSelection();
    }

    public void refreshTableNV() {
        txtTuKhoaTimKiem.setText("");
        xuLyTimKiem();
    }

    public void updatePhongBanComboBox() {
        cmbPhongBanNV.removeAllItems();
        for (PhongBan pb : danhSachPB) {
            cmbPhongBanNV.addItem(pb);
        }
    }
}