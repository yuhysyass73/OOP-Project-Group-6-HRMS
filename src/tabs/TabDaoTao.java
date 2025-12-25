package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import MainApp.*;
import dataa.*;

import java.awt.*;
import java.sql.*;

public class TabDaoTao extends JPanel {
    private QuanLyNhanVienGUI parent;
    private DefaultTableModel modelKhoa, modelHocVien;
    private JTable tableKhoa, tableHocVien;
    private JTextField txtMaKhoa, txtTenKhoa, txtNgayBD, txtNgayKT;
    private JTextField txtMaNVHoc;

    public TabDaoTao(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createPanelKhoaHoc(), createPanelHocVien());
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createPanelKhoaHoc() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh mục Khóa Đào tạo"));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Mã Khóa:")); txtMaKhoa = new JTextField(8); form.add(txtMaKhoa);
        form.add(new JLabel("Tên Khóa:")); txtTenKhoa = new JTextField(15); form.add(txtTenKhoa);
        form.add(new JLabel("Ngày BĐ:")); txtNgayBD = new JTextField(8); form.add(txtNgayBD);
        form.add(new JLabel("Ngày KT:")); txtNgayKT = new JTextField(8); form.add(txtNgayKT);
        
        JButton btnThem = new JButton("Tạo Khóa học");
        btnThem.addActionListener(e -> themKhoaHoc());
        form.add(btnThem);

        panel.add(form, BorderLayout.NORTH);

        modelKhoa = new DefaultTableModel(new String[]{"Mã Khóa", "Tên Khóa", "Bắt đầu", "Kết thúc"}, 0);
        tableKhoa = new JTable(modelKhoa);
        tableKhoa.getSelectionModel().addListSelectionListener(e -> loadHocVien());
        panel.add(new JScrollPane(tableKhoa), BorderLayout.CENTER);
        
        loadKhoaHoc();
        return panel;
    }
}