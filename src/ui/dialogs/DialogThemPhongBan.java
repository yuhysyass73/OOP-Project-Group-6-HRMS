package ui.dialogs;

import javax.swing.*;
import java.awt.*;
import MainApp.QuanLyNhanVienGUI;
import objects.PhongBan;
import ui.components.*;
import util.Validator;

public class DialogThemPhongBan extends JDialog {

    private boolean isConfirmed = false;
    private boolean isEditMode = false;
    
    private JTextField txtMaPB, txtTenPB;
    private JTextArea txtMoTa;
    private ModernButton btnSave, btnCancel;

    public DialogThemPhongBan(QuanLyNhanVienGUI parent, PhongBan pbCu) {
        super(parent, pbCu == null ? "Thêm Phòng Ban Mới" : "Cập Nhật Phòng Ban", true);
        this.isEditMode = (pbCu != null);
        
        setSize(500, 420);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Header
        RoundedPanel pnlHeader = new RoundedPanel(0, new Color(241, 245, 249));
        pnlHeader.setPreferredSize(new Dimension(500, 60));
        pnlHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        
        JLabel lblTitle = new JLabel(pbCu == null ? "THÊM PHÒNG BAN" : "SỬA PHÒNG BAN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(30, 41, 59));
        pnlHeader.add(lblTitle);
        add(pnlHeader, BorderLayout.NORTH);

        // Body
        JPanel pnlBody = new JPanel(new GridBagLayout());
        pnlBody.setBackground(Color.WHITE);
        pnlBody.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        pnlBody.add(createLabel("Mã Phòng (VD: KT):"), gbc);
        
        gbc.gridx = 1;
        txtMaPB = new JTextField();
        setupTextField(txtMaPB);
        if (isEditMode) {
            txtMaPB.setText(pbCu.getMaPhongBan());
            txtMaPB.setEditable(false); 
            txtMaPB.setBackground(new Color(241, 245, 249));
        }
        pnlBody.add(txtMaPB, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlBody.add(createLabel("Tên Phòng Ban:"), gbc);
        
        gbc.gridx = 1;
        txtTenPB = new JTextField();
        setupTextField(txtTenPB);
        if (isEditMode) txtTenPB.setText(pbCu.getTenPhongBan());
        pnlBody.add(txtTenPB, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST;
        pnlBody.add(createLabel("Mô tả / Ghi chú:"), gbc);
        
        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlBody.add(new JScrollPane(txtMoTa), gbc);

        add(pnlBody, BorderLayout.CENTER);

        // Footer
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        pnlFooter.setBackground(new Color(241, 245, 249));
        
        btnCancel = new ModernButton("Hủy", new Color(148, 163, 184), new Color(100, 116, 139));
        btnSave = new ModernButton("Lưu Dữ Liệu", new Color(22, 163, 74), new Color(21, 128, 61));
        
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnSave.setPreferredSize(new Dimension(140, 40));
        
        pnlFooter.add(btnCancel);
        pnlFooter.add(btnSave);
        add(pnlFooter, BorderLayout.SOUTH);

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
        if (!Validator.checkTextField(txtMaPB, 3)) {
            Toast.showError("Mã phòng ban không được trống!");
            return false;
        }
        if (!Validator.checkTextField(txtTenPB, 3)) {
            Toast.showError("Tên phòng ban không được trống!");
            return false;
        }
        return true;
    }

    public boolean isConfirmed() { return isConfirmed; }
    public String getMaPB() { return txtMaPB.getText().trim().toUpperCase(); }
    public String getTenPB() { return txtTenPB.getText().trim(); }
}