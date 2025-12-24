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
}