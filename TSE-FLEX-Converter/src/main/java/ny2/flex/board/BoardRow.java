package ny2.flex.board;

import java.math.BigDecimal;
import java.time.LocalTime;

import lombok.ToString;
import ny2.flex.message.individual.QuoteQBQS;

@ToString
public class BoardRow {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    /** Long Price (10000 times from actual Price) */
    private Long longPrice;

    /** Last update no of this row */
    private int lastIssueUpdateNo;

    /** Last update time of this row */
    private LocalTime lastUpdateTime;

    private long quantity;
    private long numberOfOrders;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public BoardRow(Long longPrice) {
        super();
        this.longPrice = longPrice;
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public void updateRow(QuoteQBQS quote, int issueUpdateNo) {
        // Update items
        this.lastIssueUpdateNo = issueUpdateNo;
        this.lastUpdateTime = quote.getTime();

        this.quantity = quote.getQuantity();
        this.numberOfOrders = quote.getNumberOfOrders();
    }

    public double getPrice() {
        BigDecimal bd = new BigDecimal(longPrice).movePointLeft(4);
        return bd.doubleValue();
    }

    // //////////////////////////////////////
    // Getters and Setters
    // //////////////////////////////////////

    public Long getLongPrice() {
        return longPrice;
    }

    public int getLastIssueUpdateNo() {
        return lastIssueUpdateNo;
    }

    public LocalTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public long getQuantity() {
        return quantity;
    }

    public long getNumberOfOrders() {
        return numberOfOrders;
    }

}
