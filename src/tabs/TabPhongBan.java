package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import MainApp.*;
import doituong.*;
import dataa.*;

import java.awt.*;
import java.util.List;


public class TabPhongBan extends JPanel {
      
    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;
    private List<PhongBan> danhSachPB;

    private JComboBox<PhongBan> cmbChonPhongBan;
    private DefaultTableModel modelNhanVienTheoPB;
    private JTable tableNhanVienTheoPB;


    public TabPhongBan(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;
        this.danhSachPB = parent.danhSachPB;

        setLayout(new BorderLayout(10, 10));

        // giao diện phòng ban
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectPanel.add(new JLabel("Chọn phòng ban để xem nhân viên:"));
        cmbChonPhongBan = new JComboBox<>();
        cmbChonPhongBan.addActionListener(e -> locNhanVienTheoPhongBan());
        selectPanel.add(cmbChonPhongBan);
        add(selectPanel, BorderLayout.NORTH);
    
        // thông tin nhân viên phòng ban
        String[] columnNamesNV = {"Mã NV", "Họ Tên", "SĐT", "Email", "Ngày sinh", "CCCD", "Thâm niên (năm)"};
        modelNhanVienTheoPB = new DefaultTableModel(columnNamesNV, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableNhanVienTheoPB = new JTable(modelNhanVienTheoPB);
        
        add(new JScrollPane(tableNhanVienTheoPB), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
