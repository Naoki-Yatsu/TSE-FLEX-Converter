package ny2.flex.message.individual;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ny2.flex.message.Message;
import ny2.flex.message.MessageConverter;
import ny2.flex.message.MessageType;

@Getter
@Setter
@ToString
public class TurnoverVA extends Message {

    // //////////////////////////////////////
    // Field (Final)
    // //////////////////////////////////////

    // Turnover Unit Flag
    private static final int INDEX_VA_UNIT_FLAG = 5;
    private static final int LENGTH_VA_UNIT_FLAG = 1;

    // Trading Volume
    private static final int INDEX_VA_TURNOVER = 6;
    private static final int LENGTH_VA_TURNOVER = 14;

    // Time
    private static final int INDEX_VA_TIME = 20;
    private static final int LENGTH_VA_TIME = 9;

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    /** show unit of times ten */
    private int turnoverUnit;

    /** Turnover in message */
    private long turnoverOriginal;

    /** Turnover unit conversion */
    private long turnover;

    private LocalTime time;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public TurnoverVA(String messageStr) {
        super(MessageType.VA, messageStr);
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public void convertMessage() {
        turnoverUnit = MessageConverter.getFieldAsInt(messageStr, INDEX_VA_UNIT_FLAG, LENGTH_VA_UNIT_FLAG);
        turnoverOriginal = MessageConverter.getFieldAsLong(messageStr, INDEX_VA_TURNOVER, LENGTH_VA_TURNOVER);
        time = MessageConverter.getFieldAsLocalTime(messageStr, INDEX_VA_TIME, LENGTH_VA_TIME);

        // calc trading volume
        turnover = new BigDecimal(turnoverOriginal).movePointRight(turnoverUnit).longValue();
    }

}
