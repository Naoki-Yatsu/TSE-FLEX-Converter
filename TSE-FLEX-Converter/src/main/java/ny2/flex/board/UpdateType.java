package ny2.flex.board;

import lombok.Getter;

@Getter
public enum UpdateType {

    // Update by Quote
    QUOTE_BID(1),
    QUOTE_ASK(2),
    QUOTE_BOTH(3),

    // Update by Deal
    DEAL_BUY(5),
    DEAL_SELL(6),
    DEAL_BOTH(7),

    // Update by both of Quote and Deal
    BOTH_QUOTE_DEAL(9),
    OTHERS(0);

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private int typeValue;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    private UpdateType(int typeValue) {
        this.typeValue = typeValue;
    }

}
