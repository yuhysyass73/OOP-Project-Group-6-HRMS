import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        cardLayoutHieuSuat = new CardLayout();
        cardPanelHieuSuat = new JPanel(cardLayoutHieuSuat);

        JPanel diemDanhPanel = createDiemDanhPanel();
        JPanel kpiPanel = createPlaceholderPanel("Chức năng Đánh giá KPI sẽ được xây dựng ở đây");
        

        cardPanelHieuSuat.add(diemDanhPanel, "Điểm danh");
        cardPanelHieuSuat.add(kpiPanel, "Đánh giá KPI");

        add(cardPanelHieuSuat, BorderLayout.CENTER);


        modeSelector.addActionListener(e -> {
            String selectedMode = (String) modeSelector.getSelectedItem();
            cardLayoutHieuSuat.show(cardPanelHieuSuat, selectedMode);
        });
    }


}
