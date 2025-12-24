package tabs;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import MainApp.*;
import objects.*;
import dataa.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.Date;

/**
 TabLichLamViec - Hệ thống quản lý Phân ca & Lịch biểu trực quan.
 Tính năng:
 Vẽ lịch tháng (Calendar View) thủ công (Custom Painting).
 Kéo thả/Click để xếp ca cho nhân viên.
 Tự động xếp lịch (Auto-schedule, mức cơ bản nhất).
 Báo cáo nhanh tình hình nhân sự trong ngày.
 */
public class TabLichLamViec extends JPanel {

    private QuanLyNhanVienGUI parent;
    private Calendar currentCalendar;
    private int currentMonth; // 0-11
    private int currentYear;
    
    private JPanel pnlCalendarGrid;
    private JLabel lblMonthYear;
    private JComboBox<String> cmbNhanVienFilter;
    private JComboBox<String> cmbCaLamViecQuick;
    private JCheckBox chkCheDoGanNhanh;
    
    private Map<String, List<ShiftData>> mapLichLamViec;
    private List<CaLamViec> danhSachCa;
    private List<NhanVien> danhSachNV;

    private static final String[] DAYS_OF_WEEK = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
    private static final Color COL_HEADER = new Color(230, 240, 255);
    private static final Color COL_WEEKEND = new Color(255, 245, 245);
    private static final Color COL_TODAY_BORDER = new Color(255, 100, 0);
    
    //Formatter
    private SimpleDateFormat sdfDb = new SimpleDateFormat("dd/MM/yyyy");

    public TabLichLamViec(QuanLyNhanVienGUI parent) {
        this.parent = parent;
        this.currentCalendar = Calendar.getInstance();
        this.currentCalendar.set(Calendar.DAY_OF_MONTH, 1); // Reset về ngày 1
        this.currentMonth = currentCalendar.get(Calendar.MONTH);
        this.currentYear = currentCalendar.get(Calendar.YEAR);
        
        this.mapLichLamViec = new HashMap<>();
        this.danhSachCa = new ArrayList<>();
        this.danhSachNV = parent.danhSachNV;

        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        loadDanhSachCa();
        loadDuLieuLichThang(currentMonth, currentYear);

        add(createControlPanel(), BorderLayout.NORTH);
        add(createMainCalendarPanel(), BorderLayout.CENTER);
        add(createSidePanel(), BorderLayout.EAST);
        
        refreshCalendar();
    }


    private JPanel createControlPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBorder(new EmptyBorder(0, 0, 10, 0));

        //Navigation
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton btnPrev = new JButton("<< Tháng trước");
        JButton btnNext = new JButton("Tháng sau >>");
        JButton btnToday = new JButton("Hôm nay");
        
        lblMonthYear = new JLabel();
        lblMonthYear.setFont(new Font("Arial", Font.BOLD, 24));
        lblMonthYear.setForeground(new Color(0, 51, 102));

        btnPrev.addActionListener(e -> changeMonth(-1));
        btnNext.addActionListener(e -> changeMonth(1));
        btnToday.addActionListener(e -> {
            currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.DAY_OF_MONTH, 1);
            currentMonth = currentCalendar.get(Calendar.MONTH);
            currentYear = currentCalendar.get(Calendar.YEAR);
            refreshCalendar();
        });

        pnlNav.add(btnPrev);
        pnlNav.add(lblMonthYear);
        pnlNav.add(btnNext);
        pnlNav.add(btnToday);

        //Quick Actions
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        JButton btnAutoSchedule = new JButton("⚡ Tự động xếp ca");
        btnAutoSchedule.setBackground(new Color(255, 153, 51));
        btnAutoSchedule.addActionListener(e -> showAutoScheduleDialog());

        JButton btnPrint = new JButton("🖨 In Lịch");
        btnPrint.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tính năng đang phát triển..."));

        JButton btnReload = new JButton("🔄 Tải lại");
        btnReload.addActionListener(e -> refreshCalendar());

        pnlAction.add(btnAutoSchedule);
        pnlAction.add(btnPrint);
        pnlAction.add(btnReload);

        pnl.add(pnlNav, BorderLayout.WEST);
        pnl.add(pnlAction, BorderLayout.EAST);
        return pnl;
    }

    private JPanel createMainCalendarPanel() {
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setBorder(new LineBorder(Color.GRAY, 1));

        //Header Row (CN, T2, T3...)
        JPanel pnlHeader = new JPanel(new GridLayout(1, 7));
        pnlHeader.setPreferredSize(new Dimension(0, 30));
        for (String day : DAYS_OF_WEEK) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 14));
            lbl.setOpaque(true);
            lbl.setBackground(day.equals("CN") || day.equals("T7") ? COL_WEEKEND : COL_HEADER);
            lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            pnlHeader.add(lbl);
        }

        //Calendar Grid
        pnlCalendarGrid = new JPanel(new GridLayout(0, 7)); // Row dynamic
        pnlCalendarGrid.setBackground(Color.WHITE);

        pnlWrapper.add(pnlHeader, BorderLayout.NORTH);
        pnlWrapper.add(pnlCalendarGrid, BorderLayout.CENTER);
        
        return pnlWrapper;
    }

    private JPanel createSidePanel() {
        JPanel pnlSide = new JPanel();
        pnlSide.setLayout(new BoxLayout(pnlSide, BoxLayout.Y_AXIS));
        pnlSide.setPreferredSize(new Dimension(250, 0));
        pnlSide.setBorder(new EmptyBorder(0, 10, 0, 0));

        //Tool Box: Gán nhanh
        JPanel pnlTool = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlTool.setBorder(BorderFactory.createTitledBorder("Công cụ Gán nhanh"));
        
        chkCheDoGanNhanh = new JCheckBox("Bật chế độ Click-to-Assign");
        chkCheDoGanNhanh.setFont(new Font("Arial", Font.BOLD, 12));
        chkCheDoGanNhanh.setForeground(Color.RED);
        
        pnlTool.add(chkCheDoGanNhanh);
        pnlTool.add(new JLabel("Chọn Ca cần gán:"));
        
        cmbCaLamViecQuick = new JComboBox<>();
        for (CaLamViec ca : danhSachCa) {
            cmbCaLamViecQuick.addItem(ca.toString());
        }
        pnlTool.add(cmbCaLamViecQuick);
        
        pnlTool.add(new JLabel("<html><i>HD: Bật checkbox, chọn Ca,<br>sau đó click vào ngày trên lịch<br>để gán nhanh cho <b>tất cả NV</b><br>hoặc NV đang lọc.</i></html>"));

        //Filter: Lọc theo nhân viên
        JPanel pnlFilter = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlFilter.setBorder(BorderFactory.createTitledBorder("Lọc Lịch theo NV"));
        
        cmbNhanVienFilter = new JComboBox<>();
        cmbNhanVienFilter.addItem("--- Tất cả Nhân viên ---");
        for (NhanVien nv : danhSachNV) {
            cmbNhanVienFilter.addItem(nv.getMaNhanVien() + " - " + nv.getHoTen());
        }
        cmbNhanVienFilter.addActionListener(e -> refreshCalendar()); // vẽ lại khi đổi filter
        pnlFilter.add(cmbNhanVienFilter);

        //Legend (Chú thích)
        JPanel pnlLegend = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlLegend.setBorder(BorderFactory.createTitledBorder("Chú thích"));
        pnlLegend.add(createLegendItem(new Color(204, 255, 204), "Ca Sáng"));
        pnlLegend.add(createLegendItem(new Color(255, 229, 204), "Ca Chiều"));
        pnlLegend.add(createLegendItem(new Color(204, 229, 255), "Ca Tối"));
        pnlLegend.add(createLegendItem(new Color(255, 204, 229), "Ca HC/Khác"));

        //Stats Mini
        JPanel pnlStats = new JPanel(new BorderLayout());
        pnlStats.setBorder(BorderFactory.createTitledBorder("Thống kê tháng"));
        JTextArea txtStats = new JTextArea("Tổng công: ...\nSố ca đêm: ...");
        txtStats.setEditable(false);
        txtStats.setBackground(pnlSide.getBackground());
        pnlStats.add(txtStats);

        pnlSide.add(pnlTool);
        pnlSide.add(Box.createVerticalStrut(10));
        pnlSide.add(pnlFilter);
        pnlSide.add(Box.createVerticalStrut(10));
        pnlSide.add(pnlLegend);
        pnlSide.add(Box.createVerticalStrut(10));
        pnlSide.add(pnlStats);
        pnlSide.add(Box.createVerticalGlue());

        return pnlSide;
    }

    private JPanel createLegendItem(Color c, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel icon = new JLabel("     ");
        icon.setOpaque(true);
        icon.setBackground(c);
        icon.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.add(icon);
        p.add(new JLabel(text));
        return p;
    }

    // ***LOGIC & RENDERING
    
    private void changeMonth(int offset) {
        currentCalendar.add(Calendar.MONTH, offset);
        currentMonth = currentCalendar.get(Calendar.MONTH);
        currentYear = currentCalendar.get(Calendar.YEAR);
        loadDuLieuLichThang(currentMonth, currentYear);
        refreshCalendar();
    }

    private void refreshCalendar() {
        pnlCalendarGrid.removeAll();
        
        lblMonthYear.setText("Tháng " + (currentMonth + 1) + " / " + currentYear);

        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=CN, 2=T2...
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int gridOffset = startDayOfWeek - 1; 

        //Vẽ các ô trống trước ngày 1
        for (int i = 0; i < gridOffset; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(new Color(245, 245, 245));
            empty.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            pnlCalendarGrid.add(empty);
        }

        //Vẽ các ngày trong tháng
        String currentFilterNV = null;
        if (cmbNhanVienFilter.getSelectedIndex() > 0) {
            String selected = (String) cmbNhanVienFilter.getSelectedItem();
            currentFilterNV = selected.split(" - ")[0];
        }

        Calendar today = Calendar.getInstance();

        for (int d = 1; d <= maxDay; d++) {
            String dateKey = String.format("%02d/%02d/%04d", d, currentMonth + 1, currentYear);
            List<ShiftData> shifts = mapLichLamViec.getOrDefault(dateKey, new ArrayList<>());

            //Filter logic: Nếu đang lọc NV, chỉ hiển thị shift của NV đó
            List<ShiftData> displayShifts = new ArrayList<>();
            if (currentFilterNV != null) {
                for (ShiftData s : shifts) {
                    if (s.maNV.equals(currentFilterNV)) displayShifts.add(s);
                }
            } else {
                displayShifts = shifts;
            }

            boolean isToday = (d == today.get(Calendar.DAY_OF_MONTH) 
                            && currentMonth == today.get(Calendar.MONTH) 
                            && currentYear == today.get(Calendar.YEAR));

            DayPanel dayPanel = new DayPanel(d, dateKey, displayShifts, isToday);
            pnlCalendarGrid.add(dayPanel);
        }

        //điền nốt các ô trống cuối bảng cho đẹp grid (tổng 42 ô thường dùng cho 6 hàng)
        int totalSlots = gridOffset + maxDay;
        int remaining = 42 - totalSlots;
        if (remaining < 7 && totalSlots <= 35) remaining += 7;
        
        for (int i = 0; i < remaining; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(new Color(245, 245, 245));
            empty.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            pnlCalendarGrid.add(empty);
        }

        pnlCalendarGrid.revalidate();
        pnlCalendarGrid.repaint();
    }

    //INNER CLASSES (CUSTOM COMPONENTS)

    /**
     * DayPanel: Đại diện cho 1 ô ngày trên lịch
     * Chịu trách nhiệm render danh sách ca làm việc
     */
    private class DayPanel extends JPanel {
        private int day;
        private String dateStr;
        private List<ShiftData> shifts;

        public DayPanel(int day, String dateStr, List<ShiftData> shifts, boolean isToday) {
            this.day = day;
            this.dateStr = dateStr;
            this.shifts = shifts;
            
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            setBackground(Color.WHITE);

            //Header ngày
            JLabel lblDay = new JLabel(String.valueOf(day) + "  ", SwingConstants.RIGHT);
            lblDay.setFont(new Font("Arial", isToday ? Font.BOLD : Font.PLAIN, 14));
            if (isToday) {
                lblDay.setForeground(Color.RED);
                setBorder(BorderFactory.createLineBorder(COL_TODAY_BORDER, 2));
            }
            add(lblDay, BorderLayout.NORTH);

            //Body: Danh sách Shift (Vẽ tối đa 3 dòng, còn lại hiện +...)
            JPanel pnlShifts = new JPanel();
            pnlShifts.setLayout(new BoxLayout(pnlShifts, BoxLayout.Y_AXIS));
            pnlShifts.setBackground(Color.WHITE);
            pnlShifts.setBorder(new EmptyBorder(2, 2, 2, 2));

            int count = 0;
            for (ShiftData s : shifts) {
                if (count >= 3) {
                    JLabel more = new JLabel("+" + (shifts.size() - 3) + " khác...");
                    more.setFont(new Font("Arial", Font.ITALIC, 10));
                    more.setForeground(Color.GRAY);
                    pnlShifts.add(more);
                    break;
                }
                
                //Vẽ 1 dòng shift: [Sáng] NV001
                JPanel row = new JPanel(new BorderLayout());
                row.setOpaque(true);
                row.setBackground(getColorForShift(s.tenCa));
                row.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
                
                String shortName = getTenNVNganGon(s.maNV);
                JLabel lblInfo = new JLabel(shortName);
                lblInfo.setFont(new Font("Arial", Font.PLAIN, 10));
                       
                row.add(lblInfo, BorderLayout.CENTER);
                pnlShifts.add(row);
                pnlShifts.add(Box.createVerticalStrut(1));
                count++;
            }
            add(pnlShifts, BorderLayout.CENTER);

            //Event Click
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (chkCheDoGanNhanh.isSelected()) {
                        xuLyGanNhanh(dateStr);
                    } else {
                        showDetailDialog(dateStr, shifts);
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(240, 248, 255));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(Color.WHITE);
                }
            });
        }
    }

    private class ShiftData {
        int id;
        String maNV;
        int maCa;
        String tenCa;
        String ghiChu;

        public ShiftData(int id, String maNV, int maCa, String tenCa, String ghiChu) {
            this.id = id;
            this.maNV = maNV;
            this.maCa = maCa;
            this.tenCa = tenCa;
            this.ghiChu = ghiChu;
        }
    }

    private class CaLamViec {
        int id;
        String tenCa;
        String gioVao, gioRa;

        public CaLamViec(int id, String tenCa, String gioVao, String gioRa) {
            this.id = id;
            this.tenCa = tenCa;
            this.gioVao = gioVao;
            this.gioRa = gioRa;
        }
        @Override
        public String toString() {
            return id + " - " + tenCa;
        }
    }
}