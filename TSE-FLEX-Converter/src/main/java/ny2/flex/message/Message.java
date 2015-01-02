package ny2.flex.message;

import lombok.Getter;
import ny2.flex.message.individual.CurrentPrice1P;
import ny2.flex.message.individual.QuoteQBQS;
import ny2.flex.message.individual.TradingVolumeVL;
import ny2.flex.message.individual.TurnoverVA;

@Getter
public abstract class Message {

    // //////////////////////////////////////
    // Field (Final)
    // //////////////////////////////////////

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    protected MessageType messageType;

    protected String messageStr;

    public Message(MessageType messageType, String messageStr) {
        super();
        this.messageType = messageType;
        this.messageStr = messageStr;
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public static Message createMessage(MessageType messageType, String messageStr) {
        Message message;
        switch (messageType) {
            case QB:
                message = QuoteQBQS.createBidQuote(messageStr);
                break;
            case QS:
                message = QuoteQBQS.createAskQuote(messageStr);
                break;
            case CP:
                message = new CurrentPrice1P(messageStr);
                break;
            case VL:
                message = new TradingVolumeVL(messageStr);
                break;
            case VA:
                message = new TurnoverVA(messageStr);
                break;
            case NO:
                // NO message will not be come here.
                throw new RuntimeException("Not Expected.");

            case SC:
            case BC:
                // Not implemented yet.
                return null;
            case MG:
            case LC:
            case TC:
                // To Nothing, maybe...
                return null;
            default:
                return null;
        }

        // convert message field
        if (message.canConvert()) {
            message.convertMessage();
        } else {
            return null;
        }
        return message;
    }

    /**
     * Convert messageStr to each field
     */
    public abstract void convertMessage();

    /**
     * Overridde if connot convert
     * @return
     */
    public boolean canConvert() {
        return true;
    }
}
