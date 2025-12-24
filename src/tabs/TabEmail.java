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

    public TabEmail(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //DANH SÁCH NHÂN VIÊN
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBorder(BorderFactory.createTitledBorder("Chọn Nhân viên nhận mail"));
        pnlLeft.setPreferredSize(new Dimension(400, 0));

        String[] cols = {"Chọn", "Mã NV", "Họ Tên", "Email"};
        modelNV = new DefaultTableModel(cols, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(columnIndex == 0) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
        };
        tableNV = new JTable(modelNV);
    }
}