package objects;
public class TieuChiKPI {
    private String tenTieuChi;
    private double trongSo;
    private int diem;

    public TieuChiKPI(String tenTieuChi, double trongSo) {
        this.tenTieuChi = tenTieuChi;
        this.trongSo = trongSo;
        this.diem = 0;
    }

    
    public String getTenTieuChi() { return tenTieuChi; }
    public double getTrongSo() { return trongSo; }
    public int getDiem() { return diem; }
    public void setDiem(int diem) { this.diem = diem; }

    // Tính điểm thành phần (Điểm * Trọng số)
    public double getDiemThanhPhan() {
        return diem * trongSo;
    }
}
