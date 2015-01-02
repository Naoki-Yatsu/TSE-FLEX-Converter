package ny2.flex.message.individual;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ny2.flex.common.Side;
import ny2.flex.message.Message;
import ny2.flex.message.MessageConverter;
import ny2.flex.message.MessageType;

import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@ToString
public class CurrentPrice1P extends Message {

    // //////////////////////////////////////
    // Field (Final)
    // //////////////////////////////////////

    // Short Selling Regulation Flag
    private static final int INDEX_1P_REGULATION = 2;
    private static final int LENGTH_1P_REGULATION = 1;

    // Short Selling Regulation Change Flag
    private static final int INDEX_1P_REGULATION_CHANGE = 3;
    private static final int LENGTH_1P_REGULATION_CHANGE = 1;

    // Unit Flag
    private static final int INDEX_1P_UNIT = 4;
    private static final int LENGTH_1P_UNIT = 1;

    // Price
    private static final int INDEX_1P_PRICE = 5;
    private static final int LENGTH_1P_PRICE = 14;

    // Time
    private static final int INDEX_1P_TIME = 20;
    private static final int LENGTH_1P_TIME = 9;

    // Change Flag
    private static final int INDEX_1P_CHANGE = 29;
    private static final int LENGTH_1P_CHANGE = 1;

    // State Flag
    private static final int INDEX_1P_STATE = 30;
    private static final int LENGTH_1P_STATE = 2;

    // Closing Price Input
    private static final int INDEX_1P_CLOSING = 32;
    private static final int LENGTH_1P_CLOSING = 1;

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    //
    // Message values
    //
    private LocalTime time;

    private int shortSellingReuration;

    private int shortSellingReurationChange;

    private int unit;

    private String priceStr;

    /** Price as Long value (10000 * actual price) */
    private long longPrice;

    private int change;

    private String state;

    private int closingPriceInput;

    //
    // Calculated values
    //
    /** Side (Buy/Sell) for causing this price change (Not in message) */
    private Side causedSide;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public CurrentPrice1P(String messageStr) {
        super(MessageType.CP, messageStr);
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public void convertMessage() {
        setShortSellingReuration(MessageConverter.getFieldAsInt(messageStr, INDEX_1P_REGULATION, LENGTH_1P_REGULATION));
        setShortSellingReurationChange(MessageConverter.getFieldAsInt(messageStr, INDEX_1P_REGULATION_CHANGE, LENGTH_1P_REGULATION_CHANGE));

        setUnit(MessageConverter.getFieldAsInt(messageStr, INDEX_1P_UNIT, LENGTH_1P_UNIT));
        setPriceStr(MessageConverter.getFieldAsString(messageStr, INDEX_1P_PRICE, LENGTH_1P_PRICE));
        setLongPrice(MessageConverter.getFieldAsLong(messageStr, INDEX_1P_PRICE, LENGTH_1P_PRICE));
        setTime(MessageConverter.getFieldAsLocalTime(messageStr, INDEX_1P_TIME, LENGTH_1P_TIME));

        setChange(MessageConverter.getFieldAsInt(messageStr, INDEX_1P_CHANGE, LENGTH_1P_CHANGE));
        setState(MessageConverter.getFieldAsString(messageStr, INDEX_1P_STATE, LENGTH_1P_STATE));
        setClosingPriceInput(MessageConverter.getFieldAsInt(messageStr, INDEX_1P_CLOSING, LENGTH_1P_CLOSING));
    }

    @Override
    public boolean canConvert() {
        // if price is blank, cannot use the data.
        return !StringUtils.isBlank(MessageConverter.getFieldAsString(messageStr, INDEX_1P_PRICE, LENGTH_1P_PRICE));
    }

    public double getPrice() {
        BigDecimal bd = new BigDecimal(longPrice).movePointLeft(4);
        return bd.doubleValue();
    }
}
