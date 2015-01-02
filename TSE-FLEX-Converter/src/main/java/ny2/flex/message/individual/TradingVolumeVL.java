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
public class TradingVolumeVL extends Message {

    // //////////////////////////////////////
    // Field (Final)
    // //////////////////////////////////////

    // Trading Volume Unit Flag
    private static final int INDEX_VL_UNIT_FLAG = 5;
    private static final int LENGTH_VL_UNIT_FLAG = 1;

    // Trading Volume
    private static final int INDEX_VL_VOLUME = 6;
    private static final int LENGTH_VL_VOLUME = 14;

    // Time
    private static final int INDEX_VL_TIME = 20;
    private static final int LENGTH_VL_TIME = 9;

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    /** show unit of times ten */
    private int tradingVolumeUnit;

    /** Trading Volume in message */
    private long tradingVolumeOriginal;

    /** Trading Volume after unit conversion */
    private long tradingVolume;

    private LocalTime time;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public TradingVolumeVL(String messageStr) {
        super(MessageType.VL, messageStr);
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public void convertMessage() {
        setTradingVolumeUnit(MessageConverter.getFieldAsInt(messageStr, INDEX_VL_UNIT_FLAG, LENGTH_VL_UNIT_FLAG));
        setTradingVolumeOriginal(MessageConverter.getFieldAsLong(messageStr, INDEX_VL_VOLUME, LENGTH_VL_VOLUME));
        setTime(MessageConverter.getFieldAsLocalTime(messageStr, INDEX_VL_TIME, LENGTH_VL_TIME));

        // calc trading volume
        tradingVolume = new BigDecimal(tradingVolumeOriginal).movePointRight(tradingVolumeUnit).longValue();
    }

}
