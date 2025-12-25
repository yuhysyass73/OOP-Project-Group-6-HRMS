package ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import MainApp.QuanLyNhanVienGUI;
import ui.components.*;
import util.Validator;

public class DialogThemUngVien extends JDialog {

    private boolean isConfirmed = false;
    private boolean isEditMode = false;
    
    private JTextField txtMaUV, txtHoTen, txtViTri, txtEmail, txtSDT;
    private JComboBox<String> cmbTrangThai;
    private ModernButton btnSave, btnCancel;

    //Constructor
    public DialogThemUngVien(QuanLyNhanVienGUI parent, String maUV, String hoTen, String viTri, String email, String sdt, String trangThai) {
        super(parent, maUV == null ? "Thêm Ứng Viên Mới" : "Cập Nhật Hồ Sơ Ứng Viên", true);
        this.isEditMode = (maUV != null);
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        //Header
        RoundedPanel pnlHeader = new RoundedPanel(0, new Color(241, 245, 249));
        pnlHeader.setPreferredSize(new Dimension(600, 60));
        pnlHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        
        JLabel lblTitle = new JLabel(isEditMode ? "CẬP NHẬT ỨNG VIÊN: " + maUV : "HỒ SƠ ỨNG VIÊN MỚI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
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

        //Mã UV
        gbc.gridx = 0; gbc.gridy = 0; pnlBody.add(createLabel("Mã Ứng Viên:"), gbc);
        gbc.gridx = 1; 
        txtMaUV = new JTextField(); setupTextField(txtMaUV);
        if (isEditMode) {
            txtMaUV.setText(maUV);
            txtMaUV.setEditable(false);
            txtMaUV.setBackground(new Color(241, 245, 249));
        } else {
            txtMaUV.setText("Tự động tạo");
            txtMaUV.setEditable(false);
            txtMaUV.setForeground(Color.GRAY);
        }
        pnlBody.add(txtMaUV, gbc);

        //Họ Tên
        gbc.gridx = 0; gbc.gridy = 1; pnlBody.add(createLabel("Họ và Tên (*):"), gbc);
        gbc.gridx = 1; 
        txtHoTen = new JTextField(); setupTextField(txtHoTen);
        if(hoTen != null) txtHoTen.setText(hoTen);
        pnlBody.add(txtHoTen, gbc);

        //Vị trí ứng tuyển
        gbc.gridx = 0; gbc.gridy = 2; pnlBody.add(createLabel("Vị trí ứng tuyển:"), gbc);
        gbc.gridx = 1; 
        txtViTri = new JTextField(); setupTextField(txtViTri);
        if(viTri != null) txtViTri.setText(viTri);
        pnlBody.add(txtViTri, gbc);

        //Email & SĐT
        gbc.gridx = 0; gbc.gridy = 3; pnlBody.add(createLabel("Email & SĐT (*):"), gbc);
        gbc.gridx = 1;
        JPanel pnlContact = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlContact.setOpaque(false);
        txtEmail = new JTextField(); setupTextField(txtEmail);
        txtSDT = new JTextField(); setupTextField(txtSDT);
        if(email != null) txtEmail.setText(email);
        if(sdt != null) txtSDT.setText(sdt);
        pnlContact.add(txtEmail); pnlContact.add(txtSDT);
        pnlBody.add(pnlContact, gbc);

        //Trạng thái
        gbc.gridx = 0; gbc.gridy = 4; pnlBody.add(createLabel("Trạng thái hồ sơ:"), gbc);
        gbc.gridx = 1; 
        String[] statuses = {"Mới nhận", "Đang phỏng vấn", "Chờ duyệt", "Đã tuyển dụng", "Từ chối"};
        cmbTrangThai = new JComboBox<>(statuses);
        cmbTrangThai.setBackground(Color.WHITE);
        cmbTrangThai.setPreferredSize(new Dimension(200, 35));
        if(trangThai != null) cmbTrangThai.setSelectedItem(trangThai);
        pnlBody.add(cmbTrangThai, gbc);

        add(pnlBody, BorderLayout.CENTER);

        //Footer
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlFooter.setBackground(new Color(241, 245, 249));
        
        btnCancel = new ModernButton("Hủy bỏ", new Color(148, 163, 184), new Color(100, 116, 139));
        btnSave = new ModernButton("Lưu Hồ Sơ", new Color(22, 163, 74), new Color(21, 128, 61));
        
        btnCancel.setPreferredSize(new Dimension(120, 40));
        btnSave.setPreferredSize(new Dimension(150, 40));
        
        pnlFooter.add(btnCancel); pnlFooter.add(btnSave);
        add(pnlFooter, BorderLayout.SOUTH);

        //Events
        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            if (validateForm()) {
                isConfirmed = true;
                dispose();
            }
        });
    }

    private void setupTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(250, 35));
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
        if (!Validator.checkTextField(txtHoTen, 3)) {
            Toast.showError("Họ tên không được để trống!");
            return false;
        }
        if (!Validator.checkTextField(txtEmail, 1)) {
            Toast.showError("Email không hợp lệ!");
            return false;
        }
        if (!Validator.checkTextField(txtSDT, 2)) {
            Toast.showError("Số điện thoại không hợp lệ!");
            return false;
        }
        if (!Validator.checkTextField(txtViTri, 3)) {
            Toast.showError("Vui lòng nhập vị trí ứng tuyển!");
            return false;
        }
        return true;
    }

    // Getters
    public boolean isConfirmed() { return isConfirmed; }
    public String getHoTen() { return txtHoTen.getText().trim(); }
    public String getViTri() { return txtViTri.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public String getSDT() { return txtSDT.getText().trim(); }
    public String getTrangThai() { return (String) cmbTrangThai.getSelectedItem(); }
}