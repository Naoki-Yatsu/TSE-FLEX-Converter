package ny2.flex.common;

import lombok.Getter;

@Getter
public enum Side {

    BUY(BidAsk.ASK, BidAsk.BID, '1'),
    SELL(BidAsk.BID, BidAsk.ASK, '2'),
    BOTH(null, null, '0');

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    /** その方向の売買をするためのプライス種別 */
    private BidAsk openBidAsk;

    /** その方向のポジションを保持している場合に、決済に使うレートの方向 */
    private BidAsk closeBidAsk;

    /** FIXメッセージ用のSide */
    private char sideForFIX;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    private Side(BidAsk openBidAsk, BidAsk closeBidAsk, char sideForFIX) {
        this.openBidAsk = openBidAsk;
        this.closeBidAsk = closeBidAsk;
        this.sideForFIX = sideForFIX;
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public static Side getReverseSide(Side side) {
        if (side == BUY) {
            return SELL;
        } else {
            return BUY;
        }
    }

    public Side getReverseSide() {
        if (this == BUY) {
            return SELL;
        } else {
            return BUY;
        }
    }
}
