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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TabLuong extends JPanel {
    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;
    private NumberFormat currencyFormatter;

    private DefaultTableModel tableModel;
    private JTable table;

    private Map<String, Long> danhSachLCB = new HashMap<>();
    private long THAM_NIEN_BONUS = 5_000_000;
    private long PHAT_VI_PHAM = 500_000;
    private long THUONG_DU_AN_MULTI = 2_000_000;
    private long THUONG_CHUYEN_CAN = 1_000_000;

    TabLuong(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;
        this.currencyFormatter = parent.currencyFormatter;

        for (PhongBan i: parent.danhSachPB) {
            long DEFAULT_LUONGCOBAN = 15_000_000;
            danhSachLCB.put(i.getTenPhongBan(), DEFAULT_LUONGCOBAN);
        }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Làm mới bảng lương");
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> refreshLuongTable());

        JButton btnXuatExcel = new JButton("Xuất bảng lương (Excel)");
        btnXuatExcel.setBackground(new Color(0, 153, 76));
        btnXuatExcel.setForeground(Color.WHITE);
        btnXuatExcel.setFocusPainted(false);
        btnXuatExcel.addActionListener(e->xuatFileExcel());

        leftBtnPanel.add(btnRefresh);
        leftBtnPanel.add(btnXuatExcel);

        JButton btnSetting = new JButton("Điều chỉnh lương");
        btnSetting.addActionListener(e -> chinhLuong());
        JPanel rightBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightBtnPanel.add(btnSetting);

        JPanel btnPanel = new JPanel(new BorderLayout(10, 10));
        btnPanel.add(leftBtnPanel, BorderLayout.WEST);
        btnPanel.add(rightBtnPanel, BorderLayout.EAST);
        add(btnPanel, BorderLayout.NORTH);

        String[] columnNames = {"Mã NV", "Họ tên", "Phòng ban", "Lương (CB+TN)", "Điểm thưởng DA", "Thưởng Dự án", "Thưởng Chuyên cần", "Điểm vi phạm", "Tiền phạt", "Lương thực nhận"};
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

        //final long LUONG_CO_BAN = 15_000_000;


        for (NhanVien nv : danhSachNV) {
            long luongCoBan = danhSachLCB.get(nv.getPhongBan());
            long luongTruocTru = luongCoBan + nv.getThamNien()/3 * THAM_NIEN_BONUS;
            long thuongDuAn = nv.getDiemThuongDuAn() * THUONG_DU_AN_MULTI;
            long thuongChuyenCan = (nv.getDiemViPham() == 0 ? THUONG_CHUYEN_CAN : 0);
            long tienPhat = nv.getDiemViPham() * PHAT_VI_PHAM;
            long luongThuc = luongTruocTru + thuongDuAn + thuongChuyenCan - tienPhat;

            tableModel.addRow(new Object[]{
                    nv.getMaNhanVien(),
                    nv.getHoTen(),
                    nv.getPhongBan(),
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

    private void chinhLuong() {
        JDialog luongDialog = new JDialog(parent, "Điều chỉnh lương", true);
        luongDialog.setSize(500,400);
        luongDialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Phần lương cơ bản
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Sửa lương cơ bản"));
        List<JTextField> textArray = new ArrayList<>();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);
        int hang = 0;
        for (Entry entry : danhSachLCB.entrySet()) {
            gbc.gridx = 0; gbc.gridy = hang;
            topPanel.add(new JLabel(entry.getKey().toString()), gbc);

            gbc.gridx = 1;
            textArray.add(new JTextField(entry.getValue().toString(), 15));
            topPanel.add(textArray.getLast(), gbc);
            hang++;
        }

        panel.add(topPanel,BorderLayout.NORTH);

        // Phần còn lại
        JPanel midPanel = new JPanel(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        midPanel.add(new JLabel("Lương thâm niên: "), gbc);
        gbc.gridx = 1;
        JTextField txtThamNien = new JTextField(String.valueOf(THAM_NIEN_BONUS), 15);
        midPanel.add(txtThamNien, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        midPanel.add(new JLabel("Tiền thưởng điểm dự án: "), gbc);
        gbc.gridx = 1;
        JTextField txtThuongDuAn = new JTextField(String.valueOf(THUONG_DU_AN_MULTI), 15);
        midPanel.add(txtThuongDuAn, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        midPanel.add(new JLabel("Tiền thưởng chuyên cần: "), gbc);
        gbc.gridx = 1;
        JTextField txtChuyenCan = new JTextField(String.valueOf(THUONG_CHUYEN_CAN), 15);
        midPanel.add(txtChuyenCan, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        midPanel.add(new JLabel("Tiền phạt vi phạm: "), gbc);
        gbc.gridx = 1;
        JTextField txtPhat = new JTextField(String.valueOf(PHAT_VI_PHAM), 15);
        midPanel.add(txtPhat, gbc);

        JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        temp.add(midPanel);
        temp.setBorder(BorderFactory.createTitledBorder("Sửa lương khác: "));
        panel.add(temp,BorderLayout.CENTER);

        // Nút lưu
        JButton btnLuu = new JButton("Lưu");
        btnLuu.addActionListener(e -> {
            boolean check = true;
            for (JTextField field : textArray) {
                if (Long.parseLong(field.getText()) < 0) {
                    check = false; break;
                }
            }
            JTextField[] arrTxt = {txtThamNien,txtThuongDuAn,txtChuyenCan,txtPhat};
            for (JTextField field : arrTxt) {
                if (Long.parseLong(field.getText()) < 0) {
                    check = false; break;
                }
            }

            if (!check) {
                JOptionPane.showMessageDialog(luongDialog, "Lỗi: Lương không được âm", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            else {
                int i = 0;
                for (Entry entry : danhSachLCB.entrySet()) {
                    danhSachLCB.put(entry.getKey().toString(), Long.parseLong(textArray.get(i++).getText()));
                }

                THAM_NIEN_BONUS = Long.parseLong(txtThamNien.getText());
                THUONG_DU_AN_MULTI = Long.parseLong(txtThuongDuAn.getText());
                THUONG_CHUYEN_CAN = Long.parseLong(txtChuyenCan.getText());
                PHAT_VI_PHAM = Long.parseLong(txtPhat.getText());

                JOptionPane.showMessageDialog(luongDialog, "Sửa lương thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                luongDialog.dispose();
                refreshLuongTable();
            }
        });

        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> {
            luongDialog.dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnLuu);
        bottomPanel.add(btnHuy);
        panel.add(bottomPanel,BorderLayout.SOUTH);

        luongDialog.add(panel);
        luongDialog.setVisible(true);
    }
}
