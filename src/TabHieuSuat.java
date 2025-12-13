import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TabHieuSuat extends JPanel 
{
     private QuanLyNhanVienGUI parent;
    private List<NhanVien> danhSachNV;

    private DefaultTableModel modelTopThuong;
    private JTable tableTopThuong;
    private DefaultTableModel modelTopPhat;
    private JTable tableTopPhat;

    public TabBaoCao(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.danhSachNV = parent.danhSachNV;

        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        JPanel panelThuong = new JPanel(new BorderLayout(5, 5));
        panelThuong.setBorder(BorderFactory.createTitledBorder("Top 5 Nhân viên - Điểm thưởng Dự án cao nhất"));

        String[] columnsThuong = {"Hạng", "Mã NV", "Họ Tên", "Điểm thưởng DA"};
        modelTopThuong = new DefaultTableModel(columnsThuong, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableTopThuong = new JTable(modelTopThuong);
        panelThuong.add(new JScrollPane(tableTopThuong), BorderLayout.CENTER);

        JPanel panelPhat = new JPanel(new BorderLayout(5, 5));
        panelPhat.setBorder(BorderFactory.createTitledBorder("Top 5 Nhân viên - Điểm vi phạm cao nhất"));

        String[] columnsPhat = {"Hạng", "Mã NV", "Họ Tên", "Điểm vi phạm"};
        modelTopPhat = new DefaultTableModel(columnsPhat, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableTopPhat = new JTable(modelTopPhat);
        panelPhat.add(new JScrollPane(tableTopPhat), BorderLayout.CENTER);

        splitPane.setLeftComponent(panelThuong);
        splitPane.setRightComponent(panelPhat);

        add(splitPane, BorderLayout.CENTER);
        
        JButton btnRefresh = new JButton("Làm mới Thống kê");
        btnRefresh.addActionListener(e -> refreshBaoCao());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);
    }

}
