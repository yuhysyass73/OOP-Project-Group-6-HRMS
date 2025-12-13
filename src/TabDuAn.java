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

          JPanel memberControlPanel = new JPanel(new BorderLayout());
        
        JPanel selectDuAnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectDuAnPanel.add(new JLabel("Chọn Dự án:"));
        cmbChonDuAn = new JComboBox<>();
        cmbChonDuAn.addActionListener(e -> locThanhVienTheoDuAn());
        selectDuAnPanel.add(cmbChonDuAn);
        
        JPanel addMemberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addMemberPanel.add(new JLabel("Nhập Mã NV:"));
        txtMaNVThemVaoDuAn = new JTextField(10);
        addMemberPanel.add(txtMaNVThemVaoDuAn);
        JButton btnThemNVVaoDuAn = new JButton("Thêm Nhân viên");
        btnThemNVVaoDuAn.addActionListener(e -> themNhanVienVaoDuAn());
        addMemberPanel.add(btnThemNVVaoDuAn);

        memberControlPanel.add(selectDuAnPanel, BorderLayout.NORTH);
        memberControlPanel.add(addMemberPanel, BorderLayout.CENTER);
        
        memberPanel.add(memberControlPanel, BorderLayout.NORTH);

        String[] columnsThanhVien = {"Mã NV", "Họ Tên", "Phòng ban"};
        modelThanhVienDuAn = new DefaultTableModel(columnsThanhVien, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableThanhVienDuAn = new JTable(modelThanhVienDuAn);
        memberPanel.add(new JScrollPane(tableThanhVienDuAn), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, crudPanel, memberPanel);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);
    }
    private void themDuAn() 
    {
        String maDA = txtMaDuAn.getText().trim();
        String tenDA = txtTenDuAn.getText().trim();
        Integer doPhucTap = (Integer) cmbDoPhucTap.getSelectedItem();

        if (maDA.isEmpty() || tenDA.isEmpty()) { /* ... báo lỗi ... */ return; }
        if (danhSachDuAn.stream().anyMatch(da -> da.getMaDuAn().equals(maDA))) { /* ... báo lỗi ... */ return; }

        DuAn da = new DuAn(maDA, tenDA, doPhucTap);
        danhSachDuAn.add(da);
        modelDuAn.addRow(new Object[]{da.getMaDuAn(), da.getTenDuAn(), da.getDoPhucTap()});
        
        parent.updateDuAnComboBox();
        
        JOptionPane.showMessageDialog(this, "Thêm dự án thành công!");
        txtMaDuAn.setText("");
        txtTenDuAn.setText("");
    }
    private void themNhanVienVaoDuAn() {
        DuAn selectedDA = (DuAn) cmbChonDuAn.getSelectedItem();
        String maNV = txtMaNVThemVaoDuAn.getText().trim();

        if (selectedDA == null) { /* ... báo lỗi ... */ return; }
        if (maNV.isEmpty()) { /* ... báo lỗi ... */ return; }

        NhanVien nvFound = null;
        for (NhanVien nv : danhSachNV) {
            if (nv.getMaNhanVien().equals(maNV)) {
                nvFound = nv;
                break;
            }
        }
        if (nvFound == null) { /* ... báo lỗi ... */ return; }
        if (selectedDA.hasThanhVien(nvFound)) { /* ... báo lỗi ... */ return; }

        int diemThuong = selectedDA.getDoPhucTap();
        nvFound.addDiemThuongDuAn(diemThuong);
        selectedDA.addThanhVien(nvFound);

        locThanhVienTheoDuAn();

        JOptionPane.showMessageDialog(this, "Đã thêm " + nvFound.getHoTen() + " vào dự án.\n"
                + "Nhân viên được cộng " + diemThuong + " điểm thưởng (Tổng điểm thưởng: " + nvFound.getDiemThuongDuAn() + ").",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
        txtMaNVThemVaoDuAn.setText("");
        
        parent.refreshLuongTable();
        parent.refreshBaoCaoTab();
    }


}
