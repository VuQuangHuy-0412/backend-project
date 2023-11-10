package com.example.backendproject.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.regex.Pattern;

public class FormatUtil {

    public static String formatMoney(BigDecimal money) {
        NumberFormat formatter = new DecimalFormat("#,###");
        return formatter.format(money);
    }

    public static String unAccentString(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d");
    }

    public static boolean isContainAccentString(String s){
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

        if (pattern.matcher(temp).find()){
            return true;
        }
        if (s.contains("Đ") || s.contains("đ")){
            return true;
        }
        return false;
    }
}
