import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class TabHieuSuat extends JPanel 
{
    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;


    private JPanel cardPanelHieuSuat;
    private CardLayout cardLayoutHieuSuat;
    private JTextField txtMaNVDiemDanh;
    private JRadioButton radioDiMuon;
    private JRadioButton radioVangMat;
    private ButtonGroup groupDiemDanh;
    private DefaultTableModel modelViPham;
    private JTable tableViPham;
    
    private JTextField txtMaNVKPI;
    private DefaultTableModel modelKPI;
    private JTable tableKPI;
    private JLabel lblDiemTongKet;
    private List<TieuChiKPI> danhSachTieuChi;
    
    public TabHieuSuat(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Chọn chức năng:"));
        String[] modes = {"Điểm danh", "Đánh giá KPI"};
        JComboBox<String> modeSelector = new JComboBox<>(modes);
        
        add(topPanel, BorderLayout.NORTH);
        topPanel.add(modeSelector);
        
        cardLayoutHieuSuat = new CardLayout();
        cardPanelHieuSuat = new JPanel(cardLayoutHieuSuat);

        JPanel diemDanhPanel = createDiemDanhPanel();
        JPanel kpiPanel = createKPIPanel();
        

        cardPanelHieuSuat.add(diemDanhPanel, "Điểm danh");
        cardPanelHieuSuat.add(kpiPanel, "Đánh giá KPI");

        add(cardPanelHieuSuat, BorderLayout.CENTER);


        modeSelector.addActionListener(e -> {
            String selectedMode = (String) modeSelector.getSelectedItem();
            cardLayoutHieuSuat.show(cardPanelHieuSuat, selectedMode);
        });
    }

    private JPanel createDiemDanhPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;


        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nhập Mã nhân viên:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtMaNVDiemDanh = new JTextField(15);
        panel.add(txtMaNVDiemDanh, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Lỗi vi phạm:"), gbc);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioDiMuon = new JRadioButton("Đi muộn (+1 điểm)");
        radioVangMat = new JRadioButton("Vắng mặt (+2 điểm)");
        radioDiMuon.setSelected(true);
        groupDiemDanh = new ButtonGroup();
        groupDiemDanh.add(radioDiMuon);
        groupDiemDanh.add(radioVangMat);
        radioPanel.add(radioDiMuon);
        radioPanel.add(radioVangMat);
        gbc.gridx = 1;
        panel.add(radioPanel, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        JButton btnGhiNhanDiemDanh = new JButton("Ghi nhận");
        btnGhiNhanDiemDanh.addActionListener(e -> xuLyGhiNhanDiemDanh());
        panel.add(btnGhiNhanDiemDanh, gbc);
        

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0; 
        
        String[] columnNames = {"Mã NV", "Họ Tên", "Lỗi vi phạm"};
        modelViPham = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableViPham = new JTable(modelViPham);
        
        JScrollPane scrollPane = new JScrollPane(tableViPham);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách Vi phạm Đã ghi nhận (trong phiên)"));
        panel.add(scrollPane, gbc);

        return panel;
    }

    private void xuLyGhiNhanDiemDanh() {
        String maNV = txtMaNVDiemDanh.getText().trim();
        if (maNV.isEmpty()) { /* ... báo lỗi ... */ return; }

        NhanVien nvFound = null;
        for (NhanVien nv : danhSachNV) {
            if (nv.getMaNhanVien().equals(maNV)) {
                nvFound = nv;
                break;
            }
        }
        if (nvFound == null) { /* ... báo lỗi ... */ return; }

        int pointsToAdd = 0;
        String violationType = "";
        if (radioDiMuon.isSelected()) {
            pointsToAdd = 1;
            violationType = "Đi muộn";
        } else if (radioVangMat.isSelected()) {
            pointsToAdd = 2;
            violationType = "Vắng mặt";
        }

        nvFound.addDiemViPham(pointsToAdd);

        JOptionPane.showMessageDialog(this,
                "Đã ghi nhận " + violationType + " cho nhân viên " + nvFound.getHoTen() + ".\n"
                + "Tổng điểm vi phạm mới: " + nvFound.getDiemViPham(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
        modelViPham.addRow(new Object[]{
            nvFound.getMaNhanVien(),
            nvFound.getHoTen(),
            violationType
        });

        txtMaNVDiemDanh.setText("");
        radioDiMuon.setSelected(true);
        

        parent.refreshLuongTable();
        parent.refreshBaoCaoTab();
    }


    private JPanel createKPIPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Mã Nhân viên cần đánh giá:"));
        txtMaNVKPI = new JTextField(15);
        inputPanel.add(txtMaNVKPI);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnTinhDiem = new JButton("Tính điểm Tổng kết");
        btnTinhDiem.setBackground(new Color(230, 240, 255));
        
        lblDiemTongKet = new JLabel("  Tổng điểm: 0.0  ");
        lblDiemTongKet.setFont(new Font("Arial", Font.BOLD, 14));
        lblDiemTongKet.setForeground(Color.BLUE);
        lblDiemTongKet.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JButton btnLuuKPI = new JButton("Lưu kết quả KPI");
        btnLuuKPI.setBackground(new Color(200, 255, 200));
        
        actionPanel.add(btnTinhDiem);
        actionPanel.add(lblDiemTongKet);
        actionPanel.add(btnLuuKPI);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        
        topContainer.add(inputPanel);
        topContainer.add(actionPanel);

        panel.add(topContainer, BorderLayout.NORTH);

        danhSachTieuChi = new ArrayList<>();
        danhSachTieuChi.add(new TieuChiKPI("Hiệu quả công việc (40%)", 0.4));
        danhSachTieuChi.add(new TieuChiKPI("Kỹ năng làm việc nhóm (30%)", 0.3));
        danhSachTieuChi.add(new TieuChiKPI("Kỷ luật & Giờ giấc (20%)", 0.2));
        danhSachTieuChi.add(new TieuChiKPI("Sáng tạo (10%)", 0.1));

        String[] cols = {"Tiêu chí", "Trọng số", "Điểm (1-5)", "Điểm thành phần (Điểm * Trọng số)"};
        modelKPI = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        for (TieuChiKPI tc : danhSachTieuChi) {
            modelKPI.addRow(new Object[]{
                tc.getTenTieuChi(),
                tc.getTrongSo(),
                0,
                0.0
            });
        }

        tableKPI = new JTable(modelKPI);
        panel.add(new JScrollPane(tableKPI), BorderLayout.CENTER);

        
        btnTinhDiem.addActionListener(e -> {
            double tongDiem = 0;
            try {
                for (int i = 0; i < tableKPI.getRowCount(); i++) {
                    int diemNhap = Integer.parseInt(tableKPI.getValueAt(i, 2).toString());
                    
                    if (diemNhap < 1 || diemNhap > 5) {
                        JOptionPane.showMessageDialog(this, "Điểm phải từ 1 đến 5!");
                        return;
                    }

                    TieuChiKPI tc = danhSachTieuChi.get(i);
                    tc.setDiem(diemNhap);

                    double diemThanhPhan = tc.getDiemThanhPhan();
                    modelKPI.setValueAt(diemThanhPhan, i, 3);
                    
                    tongDiem += diemThanhPhan;
                }
                lblDiemTongKet.setText("Tổng điểm: " + String.format("%.2f", tongDiem));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số nguyên vào cột Điểm!");
            }
        });

        btnLuuKPI.addActionListener(e -> {
            String maNV = txtMaNVKPI.getText().trim();
            if (maNV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chưa nhập mã nhân viên!");
                return;
            }
            
            NhanVien nvFound = null;
            for (NhanVien nv : danhSachNV) {
                if (nv.getMaNhanVien().equals(maNV)) {
                    nvFound = nv;
                    break;
                }
            }
            
            if (nvFound == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên này!");
                return;
            }

            String textDiem = lblDiemTongKet.getText().replace("Tổng điểm: ", "").replace(",", ".");
            double diemKPI = Double.parseDouble(textDiem);

            if (diemKPI == 0) {
                 JOptionPane.showMessageDialog(this, "Vui lòng tính điểm trước khi lưu!");
                 return;
            }

            nvFound.setDiemKPI(diemKPI);
            
            JOptionPane.showMessageDialog(this, "Đã lưu điểm KPI (" + diemKPI + ") cho nhân viên " + nvFound.getHoTen());
            
            // Reset
            txtMaNVKPI.setText("");
            lblDiemTongKet.setText("Tổng điểm: 0.0");
            
            for(int i=0; i<tableKPI.getRowCount(); i++) {
                tableKPI.setValueAt(0, i, 2);
                tableKPI.setValueAt(0.0, i, 3);
            }
        });

        return panel;
    }
}
