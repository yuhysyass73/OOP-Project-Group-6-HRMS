package tabs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import MainApp.*;
import objects.*;
import objects.NhanVien;

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

        loadNhanVien();
        
        pnlLeft.add(new JScrollPane(tableNV), BorderLayout.CENTER);
        
        JButton btnSelectAll = new JButton("Chọn tất cả");
        btnSelectAll.addActionListener(e -> toggleSelection(true));
        
        JButton btnDeselectAll = new JButton("Bỏ chọn hết");
        btnDeselectAll.addActionListener(e -> toggleSelection(false));
        
        JPanel pnlBtnSelect = new JPanel(new FlowLayout());
        pnlBtnSelect.add(btnSelectAll);
        pnlBtnSelect.add(btnDeselectAll);
        pnlLeft.add(pnlBtnSelect, BorderLayout.SOUTH);

        //PANEL PHẢI: SOẠN THẢO EMAIL
        JPanel pnlRight = new JPanel(new BorderLayout(10, 10));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Soạn thảo Nội dung"));

        //Cấu hình Sender
        JPanel pnlConfig = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlConfig.setBorder(BorderFactory.createTitledBorder("Cấu hình Gmail Gửi (Bắt buộc)"));
        
        pnlConfig.add(new JLabel("Gmail gửi (VD: admin@gmail.com):"));
        txtEmailGui = new JTextField("nhanvientest.java@gmail.com");//demo
        pnlConfig.add(txtEmailGui);
        
        pnlConfig.add(new JLabel("Mật khẩu Ứng dụng (App Password):"));
        txtMatKhauEmail = new JPasswordField();
        pnlConfig.add(txtMatKhauEmail);

        //Form soạn thảo
        JPanel pnlCompose = new JPanel(new BorderLayout(5, 5));
        JPanel pnlSubject = new JPanel(new BorderLayout());
        pnlSubject.add(new JLabel("Tiêu đề: "), BorderLayout.WEST);
        txtTieuDe = new JTextField("Thông báo từ Ban Giám Đốc");
        pnlSubject.add(txtTieuDe, BorderLayout.CENTER);

        txtNoiDung = new JTextArea("Kính gửi nhân viên,\n\nĐây là email thông báo về việc...\n\nTrân trọng,\nPhòng Nhân sự.");
        txtNoiDung.setLineWrap(true);

        pnlCompose.add(pnlConfig, BorderLayout.NORTH);
        pnlCompose.add(pnlSubject, BorderLayout.CENTER);

        JPanel pnlContent = new JPanel(new BorderLayout(hgap: 5, vgap: 5));
        pnlContent.add(pnlSubject, BorderLayout.NORTH);
        pnlContent.add(new JScrollPane(txtNoiDung), BorderLayout.CENTER);

        pnlRight.add(pnlConfig, BorderLayout.NORTH);
        pnlRight.add(pnlContent, BorderLayout.CENTER);

        //PANEL DƯỚI: NÚT GỬI & TIẾN ĐỘ
        JPanel pnlBottom = new JPanel(new BorderLayout(5, 5));
        
        btnGui = new JButton("GỬI EMAIL HÀNG LOẠT");
        btnGui.setBackground(new Color(0, 102, 204));
        btnGui.setForeground(Color.WHITE);
        btnGui.setFont(new Font("Arial", Font.BOLD, 14));
        btnGui.addActionListener(e -> xuLyGuiEmail());
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        lblStatus = new JLabel("Trạng thái: Chờ lệnh gửi...");

        JPanel pnlProgress = new JPanel(new BorderLayout());
        pnlProgress.add(lblStatus, BorderLayout.NORTH);
        pnlProgress.add(progressBar, BorderLayout.CENTER);

        pnlBottom.add(btnGui, BorderLayout.WEST);
        pnlBottom.add(pnlProgress, BorderLayout.CENTER);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private void loadNhanVien() {
        modelNV.setRowCount(0);
        for (NhanVien nv : danhSachNV) {
            modelNV.addRow(new Object[]{false, nv.getMaNhanVien(), nv.getHoTen(), nv.getEmail()});
        }
    }
    
    public void capNhatDanhSachNhanVien(List<NhanVien> listMoi) {
        this.danhSachNV = listMoi;
        loadNhanVien();
        System.out.println("Tab Email đã cập nhật danh sách: " + listMoi.size() + " nhân viên.");
    }

    private void toggleSelection(boolean check) {
        for (int i = 0; i < modelNV.getRowCount(); i++) {
            modelNV.setValueAt(check, i, 0);
        }
    }
}