import javax.swing.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuanLyNhanVienGUI extends JFrame {
    
    List<NhanVien> danhSachNV;
    List<PhongBan> danhSachPB;
    List<DuAn> danhSachDuAn;

    private TabNhanVien tabNhanVien;
    private TabPhongBan tabPhongBan;
    private TabDuAn tabDuAn;
    private TabHieuSuat tabHieuSuat;
    private TabLuong tabLuong;
    private TabBaoCao tabBaoCao;
    
    NumberFormat currencyFormatter;

    public QuanLyNhanVienGUI() {
        setTitle("Phần mềm Quản lý Nhân sự");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QuanLyNhanVienGUI().setVisible(true));
    }
}
