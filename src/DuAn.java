import java.util.ArrayList;
import java.util.List;

public class DuAn {

    private String maDuAn;
    private String tenDuAn;
    private int doPhucTap; // thang 1 2 3

    private List<NhanVien> danhSachThanhVien;

    public DuAn(String maDuAN, String tenDuAn, int doPhucTap)
    {
        this.maDuAn = maDuAn;
        this.tenDuAn = tenDuAn;
        this.doPhucTap = doPhucTap;
        this.danhSachThanhVien = new ArrayList<>();
    }
}