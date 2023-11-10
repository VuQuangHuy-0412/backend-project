package com.example.backendproject.util;

import com.example.backendproject.config.constant.ErrorEnum;
import com.example.backendproject.config.exception.Sc5Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import vn.ghtk.ewallet.admin.util.DateUtil;
import vn.ghtk.ewallet.commonlib.constant.ErrorEnum;
import vn.ghtk.ewallet.commonlib.exception.GhtkException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
public class ValidatorUtil {

    public static void validateMobileFormat(String mobile) {
        boolean isValid = StringUtils.hasText(mobile);
        if (!org.apache.commons.lang3.StringUtils.isNumeric(mobile)) {
            isValid = false;
        }
        if (!mobile.startsWith("0") && !mobile.startsWith("84")) {
            isValid = false;
        }
        if (mobile.length() > 15 || mobile.length() < 10) {
            isValid = false;
        }
        if (!isValid) {
            throw new Sc5Exception(ErrorEnum.INVALID_MOBILE_FORMAT);
        }
    }

    public static String correctMobileFormat(String mobile) {
        mobile = mobile.trim()
                .replaceAll("\\s+", "") // bỏ dấu cách
                .replaceAll("\\+", ""); // bỏ dấu cộng
        if (mobile.startsWith("84")) {
            mobile = "0" + mobile.substring(2);
        }
        return mobile;
    }

    public static String correctMobileWithCountryCode(String mobile) {
        mobile = mobile.trim()
                .replaceAll("\\s+", "") // bỏ dấu cách
                .replaceAll("\\+", ""); // bỏ dấu cộng
        if (mobile.startsWith("0")) {
            mobile = "84" + mobile.substring(1);
        }
        return mobile;
    }

    public static void validatePasswordFormat(String password) {
        if (password.length() < 6) {
            throw new Sc5Exception(ErrorEnum.INVALID_PASSWORD_FORMAT);
        }
    }

    public static void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_AMOUNT);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Sc5Exception(ErrorEnum.INVALID_AMOUNT);
        }
        if (amount.stripTrailingZeros().scale() > 0) {
            throw new Sc5Exception(ErrorEnum.INVALID_AMOUNT);
        }
    }

    public static boolean isEqualsIgnoreCaseAndAccent(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return FormatUtil.unAccentString(str1).equalsIgnoreCase(FormatUtil.unAccentString(str2));
    }

    public static boolean isValidCardHolderName(String ekycName, String holdName){
        if (org.apache.commons.lang3.StringUtils.isBlank(ekycName) || org.apache.commons.lang3.StringUtils.isBlank(holdName)){
            return false;
        }

        if (FormatUtil.unAccentString(ekycName.trim()).equalsIgnoreCase(FormatUtil.unAccentString(holdName.trim()))){
            return true;
        }

        String eName = ekycName.trim().replaceAll(" {3}", " ").replaceAll(" {2}", " ");
        String hName = holdName.trim().replaceAll(" {3}", " ").replaceAll(" {2}", " ");
        String[] arrEName = eName.split(" ");
        String[] arrHName = hName.split(" ");

        if (arrEName.length <= 3 || arrHName.length != arrEName.length){
            return false;
        }

        return FormatUtil.unAccentString(arrHName[0]).equalsIgnoreCase(FormatUtil.unAccentString(arrEName[0])) &&
                FormatUtil.unAccentString(arrHName[arrHName.length-1]).equalsIgnoreCase(FormatUtil.unAccentString(arrEName[arrEName.length -1]));
    }



    public static void validateTimeFromTo(Date fromTime, Date toTime, int dateBetween) {
        if (fromTime == null || toTime == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thời gian bắt đầu và thời gian kết thúc không được để trống");
        }

        LocalDateTime startDate = DateUtil.toLocalDateTime(fromTime);
        LocalDateTime maxEndDate = DateUtil.toLocalDateTime(toTime).minusDays(dateBetween);
        if (maxEndDate.isAfter(startDate)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Chỉ được phép tìm kiếm trong khoảng tối đa " + dateBetween + " ngày");
        }
    }

    public static void validateTimeFromToExport(Date fromTime, Date toTime, int dateBetween) {
        if (fromTime == null || toTime == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thời gian bắt đầu và thời gian kết thúc không được để trống");
        }

        LocalDateTime startDate = DateUtil.toLocalDateTime(fromTime);
        LocalDateTime maxEndDate = DateUtil.toLocalDateTime(toTime).minusDays(dateBetween);
        if (maxEndDate.isAfter(startDate)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Chỉ được phép xuất file trong khoảng tối đa " + dateBetween + " ngày");
        }
    }

    public static void validateTimeForSearch(Date fromTime, Date toTime) {
        if (fromTime == null || toTime == null) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thời gian bắt đầu và thời gian kết thúc không được để trống");
        }

        LocalDateTime startDate = DateUtil.toLocalDateTime(fromTime);
        LocalDateTime toDate = DateUtil.toLocalDateTime(toTime);
        if (!toDate.isAfter(startDate)) {
            throw new Sc5Exception(ErrorEnum.INVALID_INPUT_COMMON, "Thời gian bắt đầu phải nhỏ hơn hoặc bằng thời gian kết thúc");
        }
    }
}
