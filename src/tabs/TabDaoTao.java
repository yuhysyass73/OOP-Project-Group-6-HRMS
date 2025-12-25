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
}