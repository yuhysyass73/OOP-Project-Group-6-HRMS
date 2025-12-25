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
}