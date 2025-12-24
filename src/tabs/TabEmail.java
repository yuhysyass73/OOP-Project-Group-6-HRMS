package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import MainApp.*;
import doituong.*;

import java.awt.*;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class TabEmail extends JPanel {

    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;

    private JTable tableNV;
    private DefaultTableModel modelNV;
    private JTextField txtTieuDe;
    private JTextArea txtNoiDung;
    private JPasswordField txtMatKhauEmail;
    private JTextField txtEmailGui;
    private JProgressBar progressBar;
    private JLabel lblStatus;

    private JButton btnGui;
}