package ny2.flex.kdb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import ny2.flex.common.DateTimeUtility;
import ny2.flex.data.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exxeleron.qjava.QDate;
import com.exxeleron.qjava.QDateTime;
import com.exxeleron.qjava.QException;
import com.exxeleron.qjava.QTimespan;
import com.exxeleron.qjava.QType;

public interface KdbConverter {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    // Logger
    public static final Logger logger = LoggerFactory.getLogger(KdbConverter.class);

    public static final char[] EMPTY_CHAR_ARRAY = new char[0];

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * Insert target table
     * @return
     */
    public String getTableName();

    /**
     * Convert data to kdb style.
     *
     * @param dataList
     * @return
     */
    public <T extends Data> Object[] convert(List<T> dataList);

    /**
     * Convert one data to kdb style.
     *
     * @param data
     * @return
     */
    public <T extends Data> Object[] convert(T data);

    // //////////////////////////////////////
    // Method (static)
    // //////////////////////////////////////

    // [Null Values]
    //
    // put(BOOL, false);
    // put(BYTE, (byte) 0);
    // put(GUID, new UUID(0, 0));
    // put(SHORT, Short.MIN_VALUE);
    // put(INT, Integer.MIN_VALUE);
    // put(LONG, Long.MIN_VALUE);
    // put(FLOAT, Float.NaN);
    // put(DOUBLE, Double.NaN);
    // put(CHAR, ' ');
    // put(SYMBOL, "");
    // put(TIMESTAMP, new QTimestamp(Long.MIN_VALUE));
    // put(MONTH, new QMonth(Integer.MIN_VALUE));
    // put(DATE, new QDate(Integer.MIN_VALUE));
    // put(DATETIME, new QDateTime(Double.NaN));
    // put(TIMESPAN, new QTimespan(Long.MIN_VALUE));
    // put(MINUTE, new QMinute(Integer.MIN_VALUE));
    // put(SECOND, new QSecond(Integer.MIN_VALUE));
    // put(TIME, new QTime(Integer.MIN_VALUE));

    /**
     * int
     * @param integerValue
     * @return
     */
    public static Integer kdbValue(Integer integerValue) {
        if (integerValue == null) {
            try {
                return (Integer) QType.getQNull(QType.INT);
            } catch (QException e) {
                logger.error("", e);
            }
        }
        return integerValue;
    }

    /**
     * double
     * @param doubleValue
     * @return
     */
    public static Double kdbValue(Double doubleValue) {
        if (doubleValue == null) {
            try {
                return (Double) QType.getQNull(QType.DOUBLE);
            } catch (QException e) {
                logger.error("", e);
            }
        }
        return doubleValue;
    }

    /**
     * symbol(enum)
     * @param enumValue
     * @return
     */
    public static String kdbValue(Enum<?> enumValue) {
        if (enumValue == null) {
            try {
                return (String) QType.getQNull(QType.SYMBOL);
            } catch (QException e) {
                logger.error("", e);
            }
        }
        return enumValue.name();
    }

    /**
     * symbol(String)
     * @param enumValue
     * @return
     */
    public static String kdbValue(String stringValue) {
        if (stringValue == null) {
            try {
                return (String) QType.getQNull(QType.SYMBOL);
            } catch (QException e) {
                logger.error("", e);
            }
        }
        return stringValue;
    }

    /**
     * char list
     * @param stringValue
     * @return
     */
    public static char[] kdbValueList(String stringValue) {
        if (stringValue == null) {
            return EMPTY_CHAR_ARRAY;
        }
        return stringValue.toCharArray();
    }

    /**
     * datetime
     * @param dateTime
     * @return
     */
    public static QDateTime kdbValue(LocalDateTime dateTime) {
        if (dateTime == null) {
            try {
                return (QDateTime) QType.getQNull(QType.DATETIME);
            } catch (QException e) {
                logger.error("", e);
            }
        }
        return new QDateTime(DateTimeUtility.toDate(dateTime));
    }

    /**
     * datetime
     * @param dateTime
     * @return
     */
    public static QDate kdbValue(LocalDate localDate) {
        if (localDate == null) {
            try {
                return (QDate) QType.getQNull(QType.DATE);
            } catch (QException e) {
                logger.error("", e);
            }
        }
        return new QDate(DateTimeUtility.toDate(localDate));
    }

    /**
     * LocalTime to Timespan
     * @param localTime
     * @return
     */
    public static QTimespan kdbValueTimespan(LocalTime localTime) {
        if (localTime == null) {
            try {
                return (QTimespan) QType.getQNull(QType.TIMESPAN);
            } catch (QException e) {
                logger.error("", e);
            }
        }
        return new QTimespan(localTime.toNanoOfDay());
    }

    /**
     * datetime
     * @param dateTime
     * @return
     */
    public static QDate[] kdbValues(LocalDate fromDate, LocalDate toDate) {
        QDate fromQDate = null;
        QDate toQDate = null;
        if (fromDate == null || toDate == null) {
            // 片方の値があれば、それを両方に使う
            if (fromDate != null) {
                fromQDate = new QDate(DateTimeUtility.toDate(fromDate));
                toQDate = fromQDate;
            } else if (toDate != null) {
                toQDate = new QDate(DateTimeUtility.toDate(toDate));
                fromQDate = toQDate;
            } else {
                try {
                    fromQDate = (QDate) QType.getQNull(QType.DATE);
                    toQDate = (QDate) QType.getQNull(QType.DATE);
                } catch (QException e) {
                    logger.error("", e);
                }
            }
        } else {
            fromQDate = new QDate(DateTimeUtility.toDate(fromDate));
            toQDate = new QDate(DateTimeUtility.toDate(toDate));
        }
        // create array
        QDate[] dates = {fromQDate, toQDate};
        return dates;
    }

    /**
     * nullに使用する値を取得します。
     * @return
     */
    public static Double getNullValueDouble() {
        try {
            return (Double) QType.getQNull(QType.DOUBLE);
        } catch (QException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * nullに使用する値を取得します。
     * @return
     */
    public static String getNullValueSymbol() {
        try {
            return (String) QType.getQNull(QType.SYMBOL);
        } catch (QException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * nullに使用する値を取得します。
     * @return
     */
    public static char[] getNullValueCharList() {
        return EMPTY_CHAR_ARRAY;
    }

    /**
     * nullに使用する値を取得します。
     * @return
     */
    public static QDateTime getNullValueDataTime() {
        try {
            return (QDateTime) QType.getQNull(QType.DATETIME);
        } catch (QException e) {
            logger.error("", e);
        }
        return null;
    }
}
