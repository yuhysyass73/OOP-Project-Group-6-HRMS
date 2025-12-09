import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

public class TabLuong extends JPanel {
    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;
    private NumberFormat currencyFormatter;

    private DefaultTableModel tableModel;
    private JTable table;

    TabLuong(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;
        this.currencyFormatter = parent.currencyFormatter;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm mới bảng lương");
        btnRefresh.addActionListener(e -> refreshLuongTable());
        panel.add(btnRefresh);
        add(panel, BorderLayout.NORTH);

        String[] columnNames = {"Mã NV", "Họ tên", "Lương (CB+TN)", "Điểm thưởng DA", "Thưởng Dự án", "Thưởng Chuyên cần", "Điểm vi phạm", "Tiền phạt", "Lương thực nhận"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void refreshLuongTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);

        final long LUONG_CO_BAN = 15_000_000;
        final long THAM_NIEN_BONUS = 5_000_000;
        final long PHAT_VI_PHAM = 500_000;
        final long THUONG_DU_AN_MULTI = 2_000_000;
        final long THUONG_CHUYEN_CAN = 1_000_000;

        for (NhanVien nv : danhSachNV) {
            long luongTruocTru = LUONG_CO_BAN + nv.getThamNien()/3 * THAM_NIEN_BONUS;
            long thuongDuAn = nv.getDiemThuongDuAn() * THUONG_DU_AN_MULTI;
            long thuongChuyenCan = (nv.getDiemViPham() == 0 ? THUONG_CHUYEN_CAN : 0);
            long tienPhat = nv.getDiemViPham() * PHAT_VI_PHAM;
            long luongThuc = luongTruocTru + thuongDuAn + thuongChuyenCan - tienPhat;

            tableModel.addRow(new Object[]{
                    nv.getMaNhanVien(),
                    nv.getHoTen(),
                    currencyFormatter.format(luongTruocTru),
                    nv.getDiemThuongDuAn(),
                    currencyFormatter.format(thuongDuAn),
                    currencyFormatter.format(thuongChuyenCan),
                    nv.getDiemViPham(),
                    currencyFormatter.format(tienPhat),
                    currencyFormatter.format(luongThuc)
            });
        }
    }
}
