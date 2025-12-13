import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.List;

public class TabLuong extends JPanel {
    //private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;
    private NumberFormat currencyFormatter;

    private DefaultTableModel tableModel;
    private JTable table;

    TabLuong(QuanLyNhanVienGUI parent) {
        //this.parent = parent;
        this.danhSachNV = parent.danhSachNV;
        this.currencyFormatter = parent.currencyFormatter;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm mới bảng lương");
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshLuongTable());

        JButton btnXuatExcel = new JButton("Xuất bảng lương (Excel)");
        btnXuatExcel.setBackground(new Color(0, 153, 76));
        btnXuatExcel.setForeground(Color.WHITE);
        btnXuatExcel.setFocusPainted(false);
        btnXuatExcel.addActionListener(e->xuatFileExcel());

        panel.add(btnRefresh);
        panel.add(btnXuatExcel);
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
    private void xuatFileExcel(){
        if (tableModel.getColumnCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu bảng lương");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel CSV (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        //TH nếu lưu file
        File saveFile = fileChooser.getSelectedFile();
        if (!saveFile.getAbsolutePath().endsWith(".csv")) {
            saveFile = new File(saveFile.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(saveFile), StandardCharsets.UTF_8))) {
                
            writer.write('\uFEFF'); 

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                writer.write(tableModel.getColumnName(i));
                if (i < tableModel.getColumnCount() - 1)
                    writer.write(',');
            }
            writer.newLine();

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++){
                    Object cell = tableModel.getValueAt(i, j);
                    String val = (cell == null) ? "" : cell.toString();
                    val = val.replace(',', '.');
                    writer.write(val);

                    if (i < tableModel.getColumnCount())
                        writer.write(',');
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(this, "Xuất file thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            try {
                Desktop.getDesktop().open(saveFile);
            }
            catch (Exception ex) {

            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }   
}
