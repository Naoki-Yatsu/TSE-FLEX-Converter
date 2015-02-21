package ny2.flex.message.individual;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ny2.flex.common.BidAsk;
import ny2.flex.message.Message;
import ny2.flex.message.MessageConverter;
import ny2.flex.message.MessageType;

@Getter
@Setter
@ToString
public class QuoteQBQS extends Message {

    // //////////////////////////////////////
    // Field (Final)
    // //////////////////////////////////////

    // Change Flag
    private static final int INDEX_QO_CHANGE_FLAG = 4;
    private static final int LENGTH_QO_CHANGE_FLAG = 1;

    // Unit Flag
    private static final int INDEX_QO_UNIT = 5;
    private static final int LENGTH_QO_UNIT = 1;

    // Price
    private static final int INDEX_QO_PRICE = 6;
    private static final int LENGTH_QO_PRICE = 14;

    // Time
    private static final int INDEX_QO_TIME = 21;
    private static final int LENGTH_QO_TIME = 9;

    // Quote Flag
    private static final int INDEX_QO_QUOTE_FLAG = 30;
    private static final int LENGTH_QO_QUOTE_FLAG = 1;

    // Matching Flag
    private static final int INDEX_QO_MATCHING_FLAG = 31;
    private static final int LENGTH_QO_MATCHING_FLAG = 1;

    // Trading Volume Flag
    private static final int INDEX_QO_QUOTE_VOLUME_FLAG = 32;
    private static final int LENGTH_QO_QUOTE_VOLUME_FLAG = 1;

    // Quantity
    private static final int INDEX_QO_QUANTITY = 33;
    private static final int LENGTH_QO_QUANTITY = 14;

    // Trading Volume Flag
    private static final int INDEX_QO_ORDER_VOLUME_FLAG = 48;
    private static final int LENGTH_QO_ORDER_VOLUME_FLAG = 1;

    // Number of Orders
    private static final int INDEX_QO_NUMBER_ORDERS = 49;
    private static final int LENGTH_QO_NUMBER_ORDERS = 14;

    // Middle of Book Flag
    private static final int INDEX_QO_MIDDLE_FLAG = 64;
    private static final int LENGTH_QO_MIDDLE_FLAG = 1;

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    /** Bid/Ask Flag (Not in message) */
    private BidAsk bidAsk;

    /** change flag. 1: change something, space: not changed */
    private int changeFlag;

    /** Show price unit, value is between 1 and 4. If 4, price is integer */
    private int unitFlag;

    private String priceStr;

    /** Price as Long value (10000 * actual price) */
    private long longPrice;

    private LocalTime time;

    private int quoteFlag;

    private int matchingSign;

    private int quoteVolumeFlag;

    private long quantity;

    private int orderVolumeFlag;

    private long numberOfOrders;

    private int middleOfBookFlag;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    private QuoteQBQS(MessageType messageType, String messageStr) {
        super(messageType, messageStr);
    }

    /**
     * create instance of Bid Quote
     *
     * @return
     */
    public static QuoteQBQS createBidQuote(String messageStr) {
        QuoteQBQS quote = new QuoteQBQS(MessageType.QB, messageStr);
        quote.setBidAsk(BidAsk.BID);
        return quote;
    }

    /**
     * create instance of Ask Quote
     *
     * @return
     */
    public static QuoteQBQS createAskQuote(String messageStr) {
        QuoteQBQS quote = new QuoteQBQS(MessageType.QS, messageStr);
        quote.setBidAsk(BidAsk.ASK);
        return quote;
    }

    // //////////////////////////////////////
    // Method (@Override)
    // //////////////////////////////////////

    @Override
    public void convertMessage() {
        setChangeFlag(MessageConverter.getFieldAsInt(messageStr, INDEX_QO_CHANGE_FLAG, LENGTH_QO_CHANGE_FLAG));
        setUnitFlag(MessageConverter.getFieldAsInt(messageStr, INDEX_QO_UNIT, LENGTH_QO_UNIT));

        setPriceStr(MessageConverter.getFieldAsString(messageStr, INDEX_QO_PRICE, LENGTH_QO_PRICE));
        setLongPrice(MessageConverter.getFieldAsLong(messageStr, INDEX_QO_PRICE, LENGTH_QO_PRICE));
        setTime(MessageConverter.getFieldAsLocalTime(messageStr, INDEX_QO_TIME, LENGTH_QO_TIME));

        setQuoteFlag(MessageConverter.getFieldAsInt(messageStr, INDEX_QO_QUOTE_FLAG, LENGTH_QO_QUOTE_FLAG));
        setMatchingSign(MessageConverter.getFieldAsInt(messageStr, INDEX_QO_MATCHING_FLAG, LENGTH_QO_MATCHING_FLAG));

        setQuoteVolumeFlag(MessageConverter.getFieldAsInt(messageStr, INDEX_QO_QUOTE_VOLUME_FLAG, LENGTH_QO_QUOTE_VOLUME_FLAG));
        setQuantity(MessageConverter.getFieldAsLong(messageStr, INDEX_QO_QUANTITY, LENGTH_QO_QUANTITY));

        setOrderVolumeFlag(MessageConverter.getFieldAsInt(messageStr, INDEX_QO_ORDER_VOLUME_FLAG, LENGTH_QO_ORDER_VOLUME_FLAG));
        setNumberOfOrders(MessageConverter.getFieldAsLong(messageStr, INDEX_QO_NUMBER_ORDERS, LENGTH_QO_NUMBER_ORDERS));

        setMiddleOfBookFlag(MessageConverter.getFieldAsInt(messageStr, INDEX_QO_MIDDLE_FLAG, LENGTH_QO_MIDDLE_FLAG));
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public double getPrice() {
        BigDecimal bd = new BigDecimal(longPrice).movePointLeft(4);
        return bd.doubleValue();
    }

    public boolean isBestBidAsk() {
        if (middleOfBookFlag == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Quote is exist at the price or not
     *
     * @return
     */
    public boolean existQuote() {
        // If quantity == 0, consider to be disappeared
        if (quantity == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * If this quote is market order, return true
     */
    public boolean isMarketOrder() {
        if (longPrice == 0) {
            return true;
        } else {
            return false;
        }
    }

}
