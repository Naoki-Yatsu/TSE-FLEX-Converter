package ny2.flex.message;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageConverter {

    // //////////////////////////////////////
    // Field (Final)
    // //////////////////////////////////////

    private static final Logger logger = LoggerFactory.getLogger(MessageConverter.class);

    //
    // Header
    //

    // length
    private static final int INDEX_HD_LENGTH = 0;
    private static final int LENGTH_HD_LENGTH = 6;

    // Serial No
    private static final int INDEX_HD_SERIAL_NO = 6;
    private static final int LENGTH_HD_SERIAL_NO = 11;

    // Exchange Code
    private static final int INDEX_HD_EXCHANGE_CODE = 20;
    private static final int LENGTH_HD_EXCHANGE_CODE = 1;

    // Issue Classification
    private static final int INDEX_HD_ISSUE_CLASSIFICATION = 23;
    private static final int LENGTH_HD_ISSUE_CLASSIFICATION = 4;

    // Issue Code
    private static final int INDEX_HD_ISSUE_CODE = 27;
    private static final int LENGTH_HD_ISSUE_CODE = 12;

    //
    // message
    //
    private static final String MESSAGE_SEPARATOR = "\t";

    private static final int INDEX_USER_TAGID = 0;
    private static final int LENGTH_USER_TAGID = 2;

    // message - Update No
    private static final int INDEX_NO_UPDATE_NO = 2;
    private static final int LENGTH_NO_UPDATE_NO = 8;

    private static final int INDEX_NO_DIVIDED_SERIAL_NO = 10;
    private static final int LENGTH_NO_DIVIDED_SERIAL_NO = 5;

    private static final int INDEX_NO_DIVIDED_TOTAL_NO = 15;
    private static final int LENGTH_NO_DIVIDED_TOTAL_NO = 5;

    //
    // Others
    //

    /** Time Formatter */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmssSSS");

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * Extract Message for String
     *
     * @param messageBundleStr
     */
    public static MessageBundle extract(String messageBundleStr) {
        MessageBundle bundle = new MessageBundle();

        // convert header
        extractHeader(bundle, messageBundleStr);

        // split message with separator
        String[] messages = messageBundleStr.split(MESSAGE_SEPARATOR);
        // Skip first item = header
        for (int i = 1; i < messages.length; i++) {
            String messageStr = messages[i];
            if (messageStr.length() == 0) {
                continue;
            }
            MessageType messageType = getMessageType(messageStr);
            if (messageType == MessageType.NO) {
                // extract NO(Update No) message
                extractNoMessage(bundle, messageStr);

            } else {
                // add message to MessageBundle
                try {
                    Message message = Message.createMessage(messageType, messageStr);
                    if (message != null) {
                        bundle.addMessage(message);
                    }
                } catch (Throwable t) {
                    logger.error("Error in convert message.\n  full message = {}\n  message = {}", messageBundleStr, messageStr, t);
                }
            }
        }
        return bundle;
    }

    /**
     * extract header
     *
     * @param bundle
     * @param messageBundleStr
     */
    private static void extractHeader(MessageBundle bundle, String messageBundleStr) {
        bundle.setLength(getFieldAsInt(messageBundleStr, INDEX_HD_LENGTH, LENGTH_HD_LENGTH));
        bundle.setSerialNo(getFieldAsLong(messageBundleStr, INDEX_HD_SERIAL_NO, LENGTH_HD_SERIAL_NO));
        bundle.setExchangeCode(getFieldAsInt(messageBundleStr, INDEX_HD_EXCHANGE_CODE, LENGTH_HD_EXCHANGE_CODE));
        String issueClassificationCode = getFieldAsString(messageBundleStr, INDEX_HD_ISSUE_CLASSIFICATION, LENGTH_HD_ISSUE_CLASSIFICATION);
        bundle.setIssueClassificationType(IssueClassificationType.parseCode(issueClassificationCode));
        bundle.setIssueCode(getFieldAsString(messageBundleStr, INDEX_HD_ISSUE_CODE, LENGTH_HD_ISSUE_CODE));
    }

    /**
     * extract No(Update No) Message
     *
     * @param bundle
     * @param messageStr
     */
    private static void extractNoMessage(MessageBundle bundle, String messageStr) {
        bundle.setIssueUpdateNo(getFieldAsInt(messageStr, INDEX_NO_UPDATE_NO, LENGTH_NO_UPDATE_NO));
        bundle.setIssueDividedSerialNo(getFieldAsInt(messageStr, INDEX_NO_DIVIDED_SERIAL_NO, LENGTH_NO_DIVIDED_SERIAL_NO));
        bundle.setIssueDividedTotalNo(getFieldAsInt(messageStr, INDEX_NO_DIVIDED_TOTAL_NO, LENGTH_NO_DIVIDED_TOTAL_NO));
    }

    // //////////////////////////////////////
    // Method (Static)
    // //////////////////////////////////////

    /**
     * get Message Type by tagId
     *
     * @param messageStr
     * @return
     */
    public static MessageType getMessageType(String messageStr) {
        String tagId = getFieldAsStringNoTrim(messageStr, INDEX_USER_TAGID, LENGTH_USER_TAGID);
        return MessageType.parseTagId(tagId);
    }

    /**
     * Get filed value as int. If filed value is not integer, return zero.
     *
     * @param messageStr
     * @param begin
     * @param length
     * @return
     */
    public static int getFieldAsInt(String messageStr, int begin, int length) {
        String field = messageStr.substring(begin, begin + length).trim();
        try {
            return Integer.parseInt(field);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Get filed value as int. If filed value is not integer, return Integer.MIN_VALUE.
     *
     * @param messageStr
     * @param begin
     * @param length
     * @return
     */
    public static int getFieldAsIntError(String messageStr, int begin, int length) {
        String field = messageStr.substring(begin, begin + length).trim();
        try {
            return Integer.parseInt(field);
        } catch (NumberFormatException nfe) {
            return Integer.MIN_VALUE;
        }
    }

    /**
     * Get filed value as long. If filed value is not integer, return 0L.
     *
     * @param messageStr
     * @param begin
     * @param length
     * @return
     */
    public static long getFieldAsLong(String messageStr, int begin, int length) {
        String field = messageStr.substring(begin, begin + length).trim();
        try {
            return Long.parseLong(field);
        } catch (NumberFormatException nfe) {
            return 0L;
        }
    }

    /**
     * Get filed value as long. If filed value is not integer, return Long.MIN_VALUE.
     *
     * @param messageStr
     * @param begin
     * @param length
     * @return
     */
    public static long getFieldAsLongError(String messageStr, int begin, int length) {
        String field = messageStr.substring(begin, begin + length).trim();
        try {
            return Long.parseLong(field);
        } catch (NumberFormatException nfe) {
            return Long.MIN_VALUE;
        }
    }

    /**
     * Get filed value as String.
     *
     * @param messageStr
     * @param begin
     * @param length
     * @return
     */
    public static String getFieldAsString(String messageStr, int begin, int length) {
        return messageStr.substring(begin, begin + length).trim();
    }

    public static String getFieldAsStringNoTrim(String messageStr, int begin, int length) {
        return messageStr.substring(begin, begin + length);
    }

    /**
     * Get filed value as LocalTime.
     *
     * @param messageStr
     * @param begin
     * @param length
     * @return
     */
    public static LocalTime getFieldAsLocalTime(String messageStr, int begin, int length) {
        String field = messageStr.substring(begin, begin + length);
        try {
            return LocalTime.parse(field, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.error("Error in parse massage. message = {}", messageStr, e);
            throw new RuntimeException(e);
        }
    }
}
