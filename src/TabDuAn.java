import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TabDuAn extends JPanel
{
    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;
    private List<DuAn> danhSachDuAn;

    private JTextField txtMaDuAn, txtTenDuAn;
    private JComboBox<Integer> cmbDoPhucTap;
    private DefaultTableModel modelDuAn;
    private JTable tableDuAn;
    private JComboBox<DuAn> cmbChonDuAn;
    private JTextField txtMaNVThemVaoDuAn;
    private DefaultTableModel modelThanhVienDuAn;
    private JTable tableThanhVienDuAn;

    public TabDuAn(QuanLyNhanVienGUI parent)
    {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;
        this.danhSachDuAn = parent.danhSachDuAn;

        setLayout(new BorderLayout());

        JPanel crudPanel = new JPanel(new BorderLayout(10, 10));
        crudPanel.setBorder(BorderFactory.createTitledBorder("Quản lý Dự án"));
        
        JPanel formDuAnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formDuAnPanel.add(new JLabel("Mã Dự án:"));
        txtMaDuAn = new JTextField(10);
        formDuAnPanel.add(txtMaDuAn);
        
        formDuAnPanel.add(new JLabel("Tên Dự án:"));
        txtTenDuAn = new JTextField(20);
        formDuAnPanel.add(txtTenDuAn);
        
        formDuAnPanel.add(new JLabel("Độ phức tạp:"));
        cmbDoPhucTap = new JComboBox<>(new Integer[]{1, 2, 3});
        formDuAnPanel.add(cmbDoPhucTap);
        
        JButton btnThemDuAn = new JButton("Thêm Dự án");
        btnThemDuAn.addActionListener(e -> themDuAn());
        formDuAnPanel.add(btnThemDuAn);
        
        crudPanel.add(formDuAnPanel, BorderLayout.NORTH);
        
        String[] columnsDuAn = {"Mã DA", "Tên Dự án", "Độ phức tạp"};
        modelDuAn = new DefaultTableModel(columnsDuAn, 0) 
        {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableDuAn = new JTable(modelDuAn);
        crudPanel.add(new JScrollPane(tableDuAn), BorderLayout.CENTER);
        
        JPanel memberPanel = new JPanel(new BorderLayout(10, 10));
        memberPanel.setBorder(BorderFactory.createTitledBorder("Quản lý Thành viên Dự án"));


    }
}
