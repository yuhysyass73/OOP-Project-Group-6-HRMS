import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TabNhanVien extends JPanel {
    
    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;
    private List<PhongBan> danhSachPB;

    private JComboBox<PhongBan> cmbPhongBanNV;
    private DefaultTableModel modelNV;
    private JTable tableNV;
    private JTextField txtMaNV, txtTenNV, txtSdt, txtEmail, txtNgaySinh, txtCccd, txtThamNien;
    
    private JComboBox<String> cmbTieuChiTimKiem;
    private JTextField txtTuKhoaTimKiem;

    public TabNhanVien(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV; 
        this.danhSachPB = parent.danhSachPB; 

        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm & Lọc"));
        
        searchPanel.add(new JLabel("Tiêu chí:"));
        String[] tieuChi = {"Mã Nhân viên", "Tên Nhân viên"};
        cmbTieuChiTimKiem = new JComboBox<>(tieuChi);
        searchPanel.add(cmbTieuChiTimKiem);
        
        searchPanel.add(new JLabel("    Từ khóa:"));
        txtTuKhoaTimKiem = new JTextField(20);
        searchPanel.add(txtTuKhoaTimKiem);
        
        // event when type
        txtTuKhoaTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { xuLyTimKiem(); }
            @Override public void removeUpdate(DocumentEvent e) { xuLyTimKiem(); }
            @Override public void changedUpdate(DocumentEvent e) { xuLyTimKiem(); }
        });
        
        cmbTieuChiTimKiem.addActionListener(e -> xuLyTimKiem());

        
        topPanel.add(searchPanel, BorderLayout.NORTH); 

        JPanel formPanel = new JPanel(new GridLayout(0, 4, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết")); 
        
        formPanel.add(new JLabel("Mã NV:"));
        txtMaNV = new JTextField();
        formPanel.add(txtMaNV);
        
        formPanel.add(new JLabel("Tên NV:"));
        txtTenNV = new JTextField();
        formPanel.add(txtTenNV);
        
        formPanel.add(new JLabel("Phòng ban:"));
        cmbPhongBanNV = new JComboBox<>();
        formPanel.add(cmbPhongBanNV);
        
        formPanel.add(new JLabel("SĐT:"));
        txtSdt = new JTextField();
        formPanel.add(txtSdt);
        
        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);
        
        formPanel.add(new JLabel("Ngày sinh (dd/mm/yyyy):"));
        txtNgaySinh = new JTextField();
        formPanel.add(txtNgaySinh);
        
        formPanel.add(new JLabel("CCCD:"));
        txtCccd = new JTextField();
        formPanel.add(txtCccd);
        
        formPanel.add(new JLabel("Thâm niên (năm):"));
        txtThamNien = new JTextField();
        formPanel.add(txtThamNien);
        
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnThemNV = new JButton("Thêm");
        JButton btnSuaNV = new JButton("Sửa");
        JButton btnXoaNV = new JButton("Xóa");
        JButton btnLamMoiNV = new JButton("Làm mới");

        btnThemNV.addActionListener(e -> themNhanVien());
        btnSuaNV.addActionListener(e -> suaNhanVien());
        btnXoaNV.addActionListener(e -> xoaNhanVien());
        btnLamMoiNV.addActionListener(e -> lamMoiFormNV());

        buttonPanel.add(btnThemNV);
        buttonPanel.add(btnSuaNV);
        buttonPanel.add(btnXoaNV);
        buttonPanel.add(btnLamMoiNV);
        
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mã NV", "Họ Tên", "Phòng ban", "SĐT", "Email", "Ngày sinh", "CCCD", "Thâm niên (năm)"};
        modelNV = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableNV = new JTable(modelNV);

        tableNV.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiThongTinLenFormNV();
            }
        });
        
        add(new JScrollPane(tableNV), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    private void xuLyTimKiem() {
        
    }
}
