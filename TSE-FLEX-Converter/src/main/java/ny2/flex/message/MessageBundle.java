package ny2.flex.message;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ny2.flex.message.individual.CurrentPrice1P;
import ny2.flex.message.individual.QuoteQBQS;
import ny2.flex.message.individual.TradingVolumeVL;
import ny2.flex.message.individual.TurnoverVA;

@Getter
@Setter
@ToString
public class MessageBundle {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    //
    // Message Info
    //

    /** Message length */
    private int length;

    /** Serial No of all */
    private long serialNo;

    /** exchange code, 1=TSE */
    private int exchangeCode;

    /** classification, 11=First Section, 2=Second Section, ... */
    private IssueClassificationType issueClassificationType;

    /** code of the stock */
    private String issueCode;

    /** Update No for each Code */
    private int issueUpdateNo;

    /** for divided message */
    private int issueDividedSerialNo;
    private int issueDividedTotalNo;

    //
    // Others
    //

    /** messages */
    private List<QuoteQBQS> quoteMessages = new ArrayList<>();

    // Trading messages
    private CurrentPrice1P currentPrice1P;
    private TradingVolumeVL tradingVolumeVL;
    private TurnoverVA turnoverVA;

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public void addMessage(Message message) {
        switch (message.getMessageType()) {
            case QB:
            case QS:
                quoteMessages.add((QuoteQBQS) message);
                break;
            case CP:
                currentPrice1P = (CurrentPrice1P) message;
                break;
            case VL:
                tradingVolumeVL = (TradingVolumeVL) message;
                break;
            case VA:
                turnoverVA = (TurnoverVA) message;
            default:
                break;
        }
    }

    public boolean hasCurrentPriceMessage() {
        if (currentPrice1P != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasTradingMessage() {
        if (tradingVolumeVL != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasQuoteMessage() {
        if (!quoteMessages.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDividedMessage() {
        if (issueDividedTotalNo != 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLastDividedMessage() {
        if (issueDividedTotalNo == issueDividedSerialNo) {
            return true;
        } else {
            return false;
        }
    }

    public List<Message> getAllMessages() {
        List<Message> allMessages = new ArrayList<>();
        if (hasCurrentPriceMessage()) {
            allMessages.add(currentPrice1P);
        }
        if (hasTradingMessage()) {
            allMessages.add(tradingVolumeVL);
            allMessages.add(turnoverVA);
        }
        if (hasQuoteMessage()) {
            allMessages.addAll(quoteMessages);
        }
        return allMessages;
    }

}
