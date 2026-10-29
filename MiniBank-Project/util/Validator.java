package util;

import java.util.regex.Pattern;

/** Practical 4: every check uses a compiled Pattern and matches the WHOLE string (anchored by matches()). */
public final class Validator {
    private static final Pattern MOBILE = Pattern.compile("[6-9][0-9]{9}");
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern PAN = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");
    private static final Pattern IFSC = Pattern.compile("[A-Z]{4}0[A-Z0-9]{6}");
    private static final Pattern AMOUNT = Pattern.compile("[1-9][0-9]{0,14}");

    private Validator() {
    }

    public static boolean isValidMobile(String s) {
        return s != null && MOBILE.matcher(s).matches();
    }

    public static boolean isValidEmail(String s) {
        return s != null && EMAIL.matcher(s).matches();
    }

    public static boolean isValidPan(String s) {
        return s != null && PAN.matcher(s).matches();
    }

    public static boolean isValidIfsc(String s) {
        return s != null && IFSC.matcher(s).matches();
    }

    /** Supplementary: positive whole numbers only. */
    public static boolean isValidAmount(String s) {
        return s != null && AMOUNT.matcher(s).matches();
    }
}
