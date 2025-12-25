package tabs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import MainApp.*;
import dataa.*;
import objects.*;
import ui.components.*;

import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TabHieuSuat extends JPanel {

    private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;

    private JComboBox<String> cmbCaLamViec;
    private JTextField txtMaNVChamCong;
    private DefaultTableModel modelChamCong;
    private JTable tableChamCong;
    private JLabel lblStatusCheckIn;
    private ModernButton btnCheckIn, btnCheckOut, btnRefresh;

    private JTextField txtMaNVNghi;
    private JTextField txtTuNgay, txtDenNgay;
    private JTextArea txtLyDo;
    private DefaultTableModel modelNghiPhep;
    private JTable tableNghiPhep;
    private ModernButton btnGuiDon, btnLoadDon;

    private JTextField txtMaNVViPham;
    private JRadioButton radioDiMuon;
    private JRadioButton radioKhongPhep; 
    private ButtonGroup groupViPham;
    private DefaultTableModel modelLichSuViPham;
    private JTable tableLichSuViPham;
    private ModernButton btnPhat;

    public TabHieuSuat(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV; 
        
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTabbedPane tabSub = new JTabbedPane();
        tabSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabSub.setBackground(Color.WHITE);
        
        JPanel pnlChamCong = createPanelChamCong();
        JPanel pnlNghiPhep = createPanelNghiPhep();
        JPanel pnlViPham = createPanelViPham(); 
        
        tabSub.addTab("Chấm công (Check-in/Out)", null, pnlChamCong, "Ghi nhận giờ vào ra");
        tabSub.addTab("Quản lý Nghỉ phép", null, pnlNghiPhep, "Duyệt đơn nghỉ phép");
        tabSub.addTab("Xử lý Vi phạm & Kỷ luật", null, pnlViPham, "Trừ điểm đi muộn/không phép");
        
        add(tabSub, BorderLayout.CENTER);
    }

    //CHẤM CÔNG

    private JPanel createPanelChamCong() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        RoundedPanel inputPanel = new RoundedPanel(15, Color.WHITE);
        inputPanel.setLayout(new GridBagLayout());
        
        JLabel lblTitle = new JLabel("GHI NHẬN CHẤM CÔNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        inputPanel.add(lblTitle, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        inputPanel.add(createLabel("Chọn Ca làm việc:"), gbc);
        gbc.gridx = 1; 
        cmbCaLamViec = new JComboBox<>();
        cmbCaLamViec.setBackground(Color.WHITE);
        cmbCaLamViec.setPreferredSize(new Dimension(300, 35));
        loadDanhSachCa();
        inputPanel.add(cmbCaLamViec, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(createLabel("Mã Nhân viên:"), gbc);
        gbc.gridx = 1; 
        txtMaNVChamCong = new JTextField(15);
        setupTextField(txtMaNVChamCong);
        txtMaNVChamCong.addActionListener(e -> xuLyCheckIn());
        inputPanel.add(txtMaNVChamCong, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        
        btnCheckIn = new ModernButton("CHECK-IN (Vào)", new Color(22, 163, 74), new Color(21, 128, 61));
        btnCheckIn.setPreferredSize(new Dimension(140, 40));
        
        btnCheckOut = new ModernButton("CHECK-OUT (Ra)", new Color(220, 38, 38), new Color(185, 28, 28));
        btnCheckOut.setPreferredSize(new Dimension(140, 40));

        btnPanel.add(btnCheckIn);
        btnPanel.add(Box.createHorizontalStrut(15));
        btnPanel.add(btnCheckOut);
        
        gbc.gridx = 1; gbc.gridy = 3; 
        inputPanel.add(btnPanel, gbc);

        lblStatusCheckIn = new JLabel("Vui lòng nhập Mã NV để chấm công.");
        lblStatusCheckIn.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblStatusCheckIn.setForeground(new Color(37, 99, 235));
        gbc.gridx = 1; gbc.gridy = 4; 
        inputPanel.add(lblStatusCheckIn, gbc);

        panel.add(inputPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Ngày", "Mã NV", "Ca", "Giờ Vào", "Giờ Ra"};
        modelChamCong = new DefaultTableModel(cols, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableChamCong = new JTable(modelChamCong);
        setupTable(tableChamCong);
        
        JScrollPane scrollPane = new JScrollPane(tableChamCong);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel tableContainer = new RoundedPanel(15, Color.WHITE);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(tableContainer, BorderLayout.CENTER);
        
        //Refresh Button
        btnRefresh = new ModernButton("Tải lại danh sách hôm nay", new Color(100, 116, 139), new Color(71, 85, 105));
        btnRefresh.setPreferredSize(new Dimension(200, 35));
        btnRefresh.addActionListener(e -> loadBangChamCongHienTai());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnRefresh);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        //Events
        btnCheckIn.addActionListener(e -> xuLyCheckIn());
        btnCheckOut.addActionListener(e -> xuLyCheckOut());

        loadBangChamCongHienTai(); 
        return panel;
    }

    //NGHỈ PHÉP

    private JPanel createPanelNghiPhep() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        RoundedPanel formPanel = new RoundedPanel(15, Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("TẠO ĐƠN XIN NGHỈ PHÉP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 41, 59));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        
        gbc.gridx = 0; formPanel.add(createLabel("Mã Nhân viên:"), gbc);
        gbc.gridx = 1; txtMaNVNghi = new JTextField(); setupTextField(txtMaNVNghi); formPanel.add(txtMaNVNghi, gbc);
        
        gbc.gridx = 2; formPanel.add(createLabel("Lý do:"), gbc);
        gbc.gridx = 3; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH;
        txtLyDo = new JTextArea(3, 20); 
        txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLyDo.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.add(new JScrollPane(txtLyDo), gbc);
        
        gbc.gridheight = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 2;
        
        gbc.gridx = 0; formPanel.add(createLabel("Từ ngày (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; txtTuNgay = new JTextField(); setupTextField(txtTuNgay); formPanel.add(txtTuNgay, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0; formPanel.add(createLabel("Đến ngày (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; txtDenNgay = new JTextField(); setupTextField(txtDenNgay); formPanel.add(txtDenNgay, gbc);
        
        //Button Send
        gbc.gridx = 3;
        btnGuiDon = new ModernButton("Gửi Đơn", new Color(37, 99, 235), new Color(29, 78, 216));
        btnGuiDon.setPreferredSize(new Dimension(120, 35));
        btnGuiDon.addActionListener(e -> guiDonNghiPhep());
        formPanel.add(btnGuiDon, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Mã NV", "Từ ngày", "Đến ngày", "Lý do", "Trạng thái"};
        modelNghiPhep = new DefaultTableModel(cols, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableNghiPhep = new JTable(modelNghiPhep);
        setupTable(tableNghiPhep);
        
        JPanel tableContainer = new RoundedPanel(15, Color.WHITE);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableContainer.add(new JScrollPane(tableNghiPhep), BorderLayout.CENTER);
        
        panel.add(tableContainer, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        btnLoadDon = new ModernButton("Làm mới danh sách", new Color(100, 116, 139), new Color(71, 85, 105));
        btnLoadDon.setPreferredSize(new Dimension(160, 35));
        btnLoadDon.addActionListener(e -> loadDanhSachDonNghi());
        bottomPanel.add(btnLoadDon);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadDanhSachDonNghi();
        return panel;
    }


    //VI PHẠM

    private JPanel createPanelViPham() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        RoundedPanel formPanel = new RoundedPanel(15, Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("XỬ LÝ VI PHẠM & KỶ LUẬT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(185, 28, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(lblTitle, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        gbc.gridx = 0; formPanel.add(createLabel("Nhập Mã NV vi phạm:"), gbc);
        
        gbc.gridx = 1; 
        txtMaNVViPham = new JTextField(15);
        setupTextField(txtMaNVViPham);
        formPanel.add(txtMaNVViPham, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0; formPanel.add(createLabel("Loại vi phạm:"), gbc);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        radioPanel.setOpaque(false);
        radioDiMuon = new JRadioButton("Đi muộn (+1 điểm)");
        radioKhongPhep = new JRadioButton("Nghỉ không phép (+2 điểm)");
        
        radioDiMuon.setOpaque(false); radioDiMuon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radioKhongPhep.setOpaque(false); radioKhongPhep.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        radioDiMuon.setSelected(true);
        
        groupViPham = new ButtonGroup();
        groupViPham.add(radioDiMuon);
        groupViPham.add(radioKhongPhep);
        
        radioPanel.add(radioDiMuon);
        radioPanel.add(radioKhongPhep);
        
        gbc.gridx = 1; formPanel.add(radioPanel, gbc);

        gbc.gridy = 3; gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        btnPhat = new ModernButton("Ghi nhận Vi phạm", new Color(220, 38, 38), new Color(185, 28, 28));
        btnPhat.setPreferredSize(new Dimension(160, 40));
        btnPhat.addActionListener(e -> xuLyGhiNhanViPham());
        formPanel.add(btnPhat, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        String[] columnNames = {"Mã NV", "Họ Tên", "Lỗi vi phạm", "Điểm cộng thêm"};
        modelLichSuViPham = new DefaultTableModel(columnNames, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableLichSuViPham = new JTable(modelLichSuViPham);
        setupTable(tableLichSuViPham);
        
        JPanel tableContainer = new RoundedPanel(15, Color.WHITE);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblTable = new JLabel("Lịch sử ghi nhận vừa qua:");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTable.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableContainer.add(lblTable, BorderLayout.NORTH);
        
        tableContainer.add(new JScrollPane(tableLichSuViPham), BorderLayout.CENTER);
        
        panel.add(tableContainer, BorderLayout.CENTER);

        return panel;
    }


    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private void setupTextField(JTextField txt) {
        txt.setPreferredSize(new Dimension(200, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void setupTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(100, 45));
    }



    private void loadDanhSachCa() {
        if(cmbCaLamViec == null) return;
        cmbCaLamViec.removeAllItems();
        new Thread(() -> {
            try (Connection conn = DatabaseHandler.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM ca_lam_viec")) {
                
                while (rs.next()) {
                    String item = rs.getInt("id") + " - " + rs.getString("ten_ca") 
                                + " (" + rs.getString("gio_bat_dau") + "-" + rs.getString("gio_ket_thuc") + ")";
                    
                    SwingUtilities.invokeLater(() -> cmbCaLamViec.addItem(item));
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }).start();
    }

    private void xuLyCheckIn() {
        String maNV = txtMaNVChamCong.getText().trim();
        if (maNV.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa nhập Mã NV!"); return; }
        
        if (cmbCaLamViec.getSelectedItem() == null) return;
        String caInfo = (String) cmbCaLamViec.getSelectedItem();
        int maCa = Integer.parseInt(caInfo.split(" - ")[0]); 
        
        btnCheckIn.setEnabled(false);
        btnCheckIn.setText("Đang xử lý...");
        lblStatusCheckIn.setText("⏳ Đang kết nối CSDL...");
        lblStatusCheckIn.setForeground(Color.BLUE);

        new Thread(() -> {
            String ngayHomNay = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String gioHienTai = new SimpleDateFormat("HH:mm:ss").format(new Date());

            String checkSql = "SELECT COUNT(*) FROM cham_cong WHERE ma_nv=? AND ngay_lam_viec=? AND ma_ca=?";
            String insertSql = "INSERT INTO cham_cong(ma_nv, ngay_lam_viec, ma_ca, gio_vao) VALUES(?,?,?,?)";

            try (Connection conn = DatabaseHandler.connect();
                 PreparedStatement pCheck = conn.prepareStatement(checkSql);
                 PreparedStatement pInsert = conn.prepareStatement(insertSql)) {
                
                pCheck.setString(1, maNV); pCheck.setString(2, ngayHomNay); pCheck.setInt(3, maCa);
                ResultSet rs = pCheck.executeQuery();
                rs.next();
                boolean daCheckIn = rs.getInt(1) > 0;
                rs.close();

                SwingUtilities.invokeLater(() -> {
                    if (daCheckIn) {
                        lblStatusCheckIn.setText("Lỗi: NV " + maNV + " đã Check-in ca này rồi!");
                        lblStatusCheckIn.setForeground(Color.RED);
                    } else {
                        try {
                            lblStatusCheckIn.setText("Đã Check-in NV " + maNV + " lúc " + gioHienTai);
                            lblStatusCheckIn.setForeground(new Color(0, 100, 0));
                            parent.ghiNhatKy("Check-in", "NV: " + maNV + ", Ca: " + maCa);
                            txtMaNVChamCong.setText(""); 
                            txtMaNVChamCong.requestFocus(); 
                            loadBangChamCongHienTai();
                        } catch (Exception ex) {}
                    }
                });

                if (!daCheckIn) {
                    pInsert.setString(1, maNV);
                    pInsert.setString(2, ngayHomNay);
                    pInsert.setInt(3, maCa);
                    pInsert.setString(4, gioHienTai);
                    pInsert.executeUpdate();
                    
                    SwingUtilities.invokeLater(() -> {
                        lblStatusCheckIn.setText("Check-in thành công: " + maNV + " lúc " + gioHienTai);
                        lblStatusCheckIn.setForeground(new Color(0, 100, 0));
                        parent.ghiNhatKy("Check-in", "NV: " + maNV + ", Ca: " + maCa);
                        txtMaNVChamCong.setText("");
                        txtMaNVChamCong.requestFocus();
                        
                        loadBangChamCongHienTai();
                    });
                }

            } catch (SQLException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    lblStatusCheckIn.setText("Lỗi DB: " + e.getMessage());
                    lblStatusCheckIn.setForeground(Color.RED);
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    btnCheckIn.setEnabled(true);
                    btnCheckIn.setText("CHECK-IN (Vào)");
                });
            }
        }).start();
    }

    private void xuLyCheckOut() {
        String maNV = txtMaNVChamCong.getText().trim();
        if (maNV.isEmpty()) { JOptionPane.showMessageDialog(this, "Chưa nhập Mã NV!"); return; }

        if (cmbCaLamViec.getSelectedItem() == null) return;
        String caInfo = (String) cmbCaLamViec.getSelectedItem();
        int maCa = Integer.parseInt(caInfo.split(" - ")[0]);

        btnCheckOut.setEnabled(false);
        btnCheckOut.setText("Đang xử lý...");

        new Thread(() -> {
            String ngayHomNay = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String gioHienTai = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String updateSql = "UPDATE cham_cong SET gio_ra = ? WHERE ma_nv=? AND ngay_lam_viec=? AND ma_ca=? AND gio_ra IS NULL";

            try (Connection conn = DatabaseHandler.connect();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                
                pstmt.setString(1, gioHienTai);
                pstmt.setString(2, maNV);
                pstmt.setString(3, ngayHomNay);
                pstmt.setInt(4, maCa);
                
                int rows = pstmt.executeUpdate();
                
                SwingUtilities.invokeLater(() -> {
                    if (rows > 0) {
                        lblStatusCheckIn.setText("Check-out thành công: " + maNV + " lúc " + gioHienTai);
                        lblStatusCheckIn.setForeground(new Color(0, 100, 0));
                        parent.ghiNhatKy("Check-out", "NV: " + maNV + ", Ca: " + maCa);
                        txtMaNVChamCong.setText("");
                        txtMaNVChamCong.requestFocus();
                        loadBangChamCongHienTai();
                    } else {
                        lblStatusCheckIn.setText("Không tìm thấy bản ghi Check-in để Check-out!");
                        lblStatusCheckIn.setForeground(Color.RED);
                    }
                });

            } catch (SQLException e) { 
                e.printStackTrace(); 
                SwingUtilities.invokeLater(() -> lblStatusCheckIn.setText("Lỗi DB: " + e.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    btnCheckOut.setEnabled(true);
                    btnCheckOut.setText("CHECK-OUT (Ra)");
                });
            }
        }).start();
    }

    private void loadBangChamCongHienTai() {
        if(modelChamCong == null) return;
        
        if (btnRefresh != null) {
            btnRefresh.setEnabled(false);
            btnRefresh.setText("Đang tải...");
        }

        new Thread(() -> {
            SwingUtilities.invokeLater(() -> modelChamCong.setRowCount(0));

            String ngayHomNay = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String sql = "SELECT * FROM cham_cong WHERE ngay_lam_viec = ? ORDER BY id DESC";
            
            try (Connection conn = DatabaseHandler.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, ngayHomNay);
                ResultSet rs = pstmt.executeQuery();
                while(rs.next()) {
                    Object[] rowData = {
                        rs.getInt("id"),
                        rs.getString("ngay_lam_viec"),
                        rs.getString("ma_nv"),
                        rs.getInt("ma_ca"),
                        rs.getString("gio_vao"),
                        rs.getString("gio_ra")
                    };
                    SwingUtilities.invokeLater(() -> modelChamCong.addRow(rowData));
                }
            } catch (SQLException e) { e.printStackTrace(); }
            finally {
                SwingUtilities.invokeLater(() -> {
                    if (btnRefresh != null) {
                        btnRefresh.setEnabled(true);
                        btnRefresh.setText("Tải lại danh sách hôm nay");
                    }
                });
            }
        }).start();
    }

    private void guiDonNghiPhep() {
        String maNV = txtMaNVNghi.getText().trim();
        String tuNgay = txtTuNgay.getText().trim();
        String denNgay = txtDenNgay.getText().trim();
        String lyDo = txtLyDo.getText().trim();

        if (maNV.isEmpty() || tuNgay.isEmpty() || denNgay.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!"); return;
        }

        String sql = "INSERT INTO don_nghi_phep(ma_nv, tu_ngay, den_ngay, ly_do, trang_thai, ngay_tao) VALUES(?,?,?,?,?,?)";
        String ngayTao = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maNV);
            pstmt.setString(2, tuNgay);
            pstmt.setString(3, denNgay);
            pstmt.setString(4, lyDo);
            pstmt.setString(5, "Chờ duyệt");
            pstmt.setString(6, ngayTao);
            
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Gửi đơn thành công!");
            txtMaNVNghi.setText(""); txtLyDo.setText("");
            loadDanhSachDonNghi();
            parent.ghiNhatKy("Xin nghỉ phép", "NV: " + maNV + " (" + tuNgay + " -> " + denNgay + ")");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi Database: " + e.getMessage());
        }
    }

    private void loadDanhSachDonNghi() {
        if(modelNghiPhep == null) return;
        modelNghiPhep.setRowCount(0);
        String sql = "SELECT * FROM don_nghi_phep ORDER BY id DESC";
        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                modelNghiPhep.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("ma_nv"),
                    rs.getString("tu_ngay"),
                    rs.getString("den_ngay"),
                    rs.getString("ly_do"),
                    rs.getString("trang_thai")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void xuLyGhiNhanViPham() {
        String maNV = txtMaNVViPham.getText().trim();
        if (maNV.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã NV!"); 
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
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên trong hệ thống!"); 
            return; 
        }

        int pointsToAdd = 0;
        String violationType = "";
        
        if (radioDiMuon.isSelected()) {
            pointsToAdd = 1;
            violationType = "Đi muộn";
        } else if (radioKhongPhep.isSelected()) {
            pointsToAdd = 2;
            violationType = "Nghỉ không phép";
        }

        nvFound.addDiemViPham(pointsToAdd);

        String sql = "UPDATE nhan_vien SET diem_vi_pham = ? WHERE ma_nv = ?";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nvFound.getDiemViPham());
            pstmt.setString(2, nvFound.getMaNhanVien());
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                        "Đã phạt lỗi [" + violationType + "] cho nhân viên " + nvFound.getHoTen() + ".\n"
                        + "Tổng điểm vi phạm hiện tại: " + nvFound.getDiemViPham(),
                        "Ghi nhận thành công", JOptionPane.INFORMATION_MESSAGE);

                modelLichSuViPham.addRow(new Object[]{
                    nvFound.getMaNhanVien(),
                    nvFound.getHoTen(),
                    violationType,
                    "+" + pointsToAdd
                });

                parent.ghiNhatKy("Phạt vi phạm", "NV: " + maNV + " - " + violationType);
                txtMaNVViPham.setText("");
                radioDiMuon.setSelected(true);
                
                parent.refreshLuongTable();
                parent.refreshBaoCaoTab();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi Database: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}