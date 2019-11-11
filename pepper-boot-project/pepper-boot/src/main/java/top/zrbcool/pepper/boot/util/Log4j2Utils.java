package top.zrbcool.pepper.boot.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;

/**
 * Created by zhangrongbin on 2018/10/17.
 */
public class Log4j2Utils {
    public static final String CONTEXT_COLUMN_1 = "column1";
    public static final String CONTEXT_COLUMN_2 = "column2";
    public static final String LINE = StringUtils.repeat("-", 150);
    public static final String CONFIG_PREFIX = "** PEPPER BOOT CONFIG - ";

    public static void putContextColumn1(String value) {
        ThreadContext.put(CONTEXT_COLUMN_1, value);
    }

    public static void putContextColumn2(String value) {
        ThreadContext.put(CONTEXT_COLUMN_2, value);
    }

    public static void clearContext() {
        ThreadContext.clearAll();
    }

    public static String line(int repeat) {
        return StringUtils.repeat("-", repeat);
    }
}
