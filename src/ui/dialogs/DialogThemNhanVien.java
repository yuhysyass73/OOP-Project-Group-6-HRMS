package ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import MainApp.QuanLyNhanVienGUI;
import objects.NhanVien;
import objects.PhongBan;
import util.Validator;
import ui.components.*;

public class DialogThemNhanVien extends JDialog {

    private boolean isConfirmed = false;
    private QuanLyNhanVienGUI parent;
    private NhanVien nvEdit;
    
    private JTextField txtHoTen, txtEmail, txtSDT, txtCCCD, txtNgaySinh, txtLuong;
    private JComboBox<String> cmbPhongBan, cmbGioiTinh;
    private JTextArea txtDiaChi;
    private ModernButton btnSave, btnCancel, btnUploadAnh;
    private JLabel lblAvatar, lblTitle;

    public DialogThemNhanVien(QuanLyNhanVienGUI parent, NhanVien nvToEdit) {
        super(parent, nvToEdit == null ? "Thêm Nhân viên" : "Cập nhật Nhân viên", true);
        this.parent = parent;
        this.nvEdit = nvToEdit; //Lưu lại để biết đang sửa ai
        
        setSize(750, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        //Header
        RoundedPanel pnlHeader = new RoundedPanel(0, new Color(241, 245, 249));
        pnlHeader.setPreferredSize(new Dimension(750, 70));
        pnlHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 20));
        
        lblTitle = new JLabel(nvEdit == null ? "NHẬP THÔNG TIN NHÂN SỰ" : "CẬP NHẬT HỒ SƠ: " + nvEdit.getMaNhanVien());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        //Body
        JPanel pnlBody = new JPanel(new GridBagLayout());
        pnlBody.setBackground(Color.WHITE);
        pnlBody.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //Cột Trái: Avatar
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 5; gbc.anchor = GridBagConstraints.NORTH;
        
        RoundedPanel pnlAvatarContainer = new RoundedPanel(15, new Color(226, 232, 240));
        pnlAvatarContainer.setPreferredSize(new Dimension(160, 200));
        pnlAvatarContainer.setLayout(new BorderLayout());
        
        lblAvatar = new JLabel("Ảnh 3x4", SwingConstants.CENTER);
        lblAvatar.setForeground(Color.GRAY);
        lblAvatar.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        pnlAvatarContainer.add(lblAvatar, BorderLayout.CENTER);
        
        pnlBody.add(pnlAvatarContainer, gbc);

        //Nút Upload
        gbc.gridy = 5; gbc.gridheight = 1;
        btnUploadAnh = new ModernButton("Tải ảnh lên", new Color(100, 116, 139), new Color(71, 85, 105));
        btnUploadAnh.setPreferredSize(new Dimension(160, 35));
        pnlBody.add(btnUploadAnh, gbc);

        //Cột Phải: Thông tin
        gbc.gridheight = 1; gbc.weightx = 1.0; 
        
        gbc.gridx = 1; gbc.gridy = 0;
        pnlBody.add(createLabel("Họ và tên (*):"), gbc);
        gbc.gridx = 2;
        txtHoTen = new JTextField(); setupTextField(txtHoTen);
        pnlBody.add(txtHoTen, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        pnlBody.add(createLabel("Email công việc (*):"), gbc);
        gbc.gridx = 2;
        txtEmail = new JTextField(); setupTextField(txtEmail);
        pnlBody.add(txtEmail, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        pnlBody.add(createLabel("Điện thoại & Giới tính:"), gbc);
        gbc.gridx = 2;
        JPanel pnlRow2 = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlRow2.setOpaque(false);
        txtSDT = new JTextField(); setupTextField(txtSDT);
        cmbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        cmbGioiTinh.setBackground(Color.WHITE);
        pnlRow2.add(txtSDT);
        pnlRow2.add(cmbGioiTinh);
        pnlBody.add(pnlRow2, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        pnlBody.add(createLabel("CCCD & Ngày sinh (*):"), gbc);
        gbc.gridx = 2;
        JPanel pnlRow3 = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlRow3.setOpaque(false);
        txtCCCD = new JTextField(); setupTextField(txtCCCD);
        txtNgaySinh = new JTextField("01/01/1990"); setupTextField(txtNgaySinh);
        pnlRow3.add(txtCCCD);
        pnlRow3.add(txtNgaySinh);
        pnlBody.add(pnlRow3, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        pnlBody.add(createLabel("Phòng ban"), gbc);
        gbc.gridx = 2;
        JPanel pnlRow4 = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlRow4.setOpaque(false);
        
        cmbPhongBan = new JComboBox<>();
        cmbPhongBan.setBackground(Color.WHITE);
        loadPhongBanData(); //Load PB
        
        txtLuong = new JTextField("10000000"); setupTextField(txtLuong);
        pnlRow4.add(cmbPhongBan);
        //pnlRow4.add(txtLuong);
        pnlBody.add(pnlRow4, gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.NORTHWEST;
        pnlBody.add(createLabel("Địa chỉ thường trú:"), gbc);
        gbc.gridx = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; 
        txtDiaChi = new JTextArea(3, 20);
        txtDiaChi.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        txtDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlBody.add(new JScrollPane(txtDiaChi), gbc);

        add(pnlBody, BorderLayout.CENTER);

        //Footer
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlFooter.setBackground(new Color(241, 245, 249));
        pnlFooter.setPreferredSize(new Dimension(750, 70));
        
        btnCancel = new ModernButton("Hủy bỏ", new Color(148, 163, 184), new Color(100, 116, 139));
        btnSave = new ModernButton("Lưu Hồ Sơ", new Color(22, 163, 74), new Color(21, 128, 61));
        
        btnCancel.setPreferredSize(new Dimension(130, 45));
        btnSave.setPreferredSize(new Dimension(160, 45));
        
        pnlFooter.add(btnCancel);
        pnlFooter.add(btnSave);
        add(pnlFooter, BorderLayout.SOUTH);

        //Events
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateForm()) {
                    isConfirmed = true;
                    dispose();
                }
            }
        });
        btnUploadAnh.addActionListener(e -> Toast.show("Tính năng chọn ảnh đang phát triển!"));

        //NẾU LÀ CHẾ ĐỘ SỬA: ĐIỀN DỮ LIỆU CŨ VÀO FORM
        if (nvEdit != null) {
            fillDataForEditing();
        }
    }

    private void fillDataForEditing() {
        txtHoTen.setText(nvEdit.getHoTen());
        txtEmail.setText(nvEdit.getEmail());
        txtSDT.setText(nvEdit.getSdt());
        txtCCCD.setText(nvEdit.getCccd());
        txtNgaySinh.setText(nvEdit.getNgaySinh());
        
        //Chọn đúng phòng ban trong ComboBox
        //Duyệt qua từng item, nếu item chứa mã PB của nhân viên thì chọn
        String maPBNV = nvEdit.getPhongBan().trim().toLowerCase();
        for (int i = 0; i < cmbPhongBan.getItemCount(); i++) {
            String item = cmbPhongBan.getItemAt(i).toLowerCase();
            if (item.startsWith(maPBNV)) {
                cmbPhongBan.setSelectedIndex(i);
                break;
            }
        }
    }

    private void loadPhongBanData() {
        if (parent != null && parent.danhSachPB != null) {
            cmbPhongBan.removeAllItems();
            for (PhongBan pb : parent.danhSachPB) {
                cmbPhongBan.addItem(pb.getMaPhongBan() + " - " + pb.getTenPhongBan());
            }
        }
        if (cmbPhongBan.getItemCount() == 0) {
            cmbPhongBan.addItem("NS - Nhân sự");
        }
    }

    private void setupTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(200, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private boolean validateForm() {
        boolean pass = true;
        StringBuilder msg = new StringBuilder("Vui lòng sửa các lỗi sau:\n");

        if (!Validator.checkTextField(txtHoTen, 3)) { msg.append("- Thiếu Họ tên\n"); pass = false; }
        if (!Validator.checkTextField(txtEmail, 1)) { msg.append("- Email sai\n"); pass = false; }
        if (!Validator.checkTextField(txtSDT, 2)) { msg.append("- SĐT sai\n"); pass = false; }
        if (!Validator.isCCCD(txtCCCD.getText())) { 
             txtCCCD.setBorder(BorderFactory.createLineBorder(Color.RED));
             msg.append("- CCCD phải 12 số\n"); pass = false; 
        }
        if (!Validator.checkTextField(txtLuong, 4)) { msg.append("- Lương > 0\n"); pass = false; }

        if (!pass) {
            Toast.showError("Dữ liệu sai!");
            JOptionPane.showMessageDialog(this, msg.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return pass;
    }
    
    //Getters
    public boolean isConfirmed() { return isConfirmed; }
    public String getHoTen() { return txtHoTen.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public String getSDT() { return txtSDT.getText().trim(); }
    public String getCCCD() { return txtCCCD.getText().trim(); }
    public String getNgaySinh() { return txtNgaySinh.getText().trim(); }
    
    public String getPhongBan() { 
        String selected = (String) cmbPhongBan.getSelectedItem();
        if (selected != null && selected.contains("-")) {
            return selected.split("-")[0].trim(); //Chỉ lấy MÃ (VD: KT)
        }
        return "NS"; 
    }
}