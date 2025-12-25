package util;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Validator - Bộ công cụ kiểm tra dữ liệu đầu vào
 * Giúp đảm bảo dữ liệu sạch trước khi đẩy vào Database.
 */
public class Validator {

    // Các mẫu để kiểm tra định dạng
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(84|0[3|5|7|8|9])+([0-9]{8})$"); // Đầu số VN
    private static final Pattern CCCD_PATTERN = Pattern.compile("^\\d{12}$"); // Căn cước 12 số
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\p{L}]+$"); // Tên (hỗ trợ tiếng Việt)

    /**
     * Kiểm tra định dạng Email
     */
    public static boolean isEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Kiểm tra số điện thoại Việt Nam
     */
    public static boolean isPhoneNumber(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Kiểm tra số CCCD/CMND
     */
    public static boolean isCCCD(String cccd) {
        return cccd != null && CCCD_PATTERN.matcher(cccd).matches();
    }

    /**
     * Kiểm tra tên người (không chứa số, ký tự đặc biệt)
     */
    public static boolean isNameValid(String name) {
        return name != null && name.length() > 2 && NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Kiểm tra số dương (dùng cho lương, giá tiền)
     */
    public static boolean isPositiveNumber(String str) {
        try {
            double d = Double.parseDouble(str);
            return d > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Kiểm tra ngày tháng năm (dd/MM/yyyy)
     */
    public static boolean isDateValid(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // Chặt chẽ (VD: không nhận ngày 30/02)
        try {
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Hàm tiện ích: Tự động đổi màu viền JTextField nếu sai dữ liệu.
     * * @param txt Trường nhập liệu cần kiểm tra
     * @param type Loại kiểm tra (1: Email, 2: Phone, 3: Không rỗng, 4: Số dương, 5: Ngày tháng)
     * @return true nếu dữ liệu hợp lệ
     */
    public static boolean checkTextField(JTextField txt, int type) {
        String content = txt.getText().trim();
        boolean valid = false;

        switch (type) {
            case 1: // Email
                valid = isEmail(content);
                break;
            case 2: // Phone
                valid = isPhoneNumber(content);
                break;
            case 3: // Not Empty
                valid = !content.isEmpty();
                break;
            case 4: // Positive Number
                valid = isPositiveNumber(content);
                break;
            case 5: // Date
                valid = isDateValid(content);
                break;
            default:
                valid = true;
        }

        if (!valid) {
            //sai: Viền đỏ, nền hồng nhạt
            txt.setBorder(BorderFactory.createLineBorder(Color.RED));
            txt.setBackground(new Color(255, 240, 240));
            txt.setToolTipText("Dữ liệu không đúng định dạng!");
        } else {
            //đúng: Viền xám, nền trắng
            txt.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            txt.setBackground(Color.WHITE);
            txt.setToolTipText(null);
        }
        return valid;
    }
}