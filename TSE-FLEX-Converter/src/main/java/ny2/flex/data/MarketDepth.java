package ny2.flex.data;

import java.time.LocalTime;
import java.util.StringJoiner;

import ny2.flex.board.UpdateType;
import ny2.flex.message.IssueClassificationType;

public class MarketDepth extends Data {

    private LocalTime time;
    private String sym;
    private IssueClassificationType classification;

    // Best Bid/Ask
    private double bidPrice;
    private double askPrice;
    private long bidQuantity;
    private long askQuantity;
    private long bidNumOrder;
    private long askNumOrder;

    // Depth
    private double[] bidPrices;
    private double[] askPrices;
    private long[] bidQuantities;
    private long[] askQuantities;
    private long[] bidNumOrders;
    private long[] askNumOrders;

    // Others
    private UpdateType updateType;
    private int updateNo;
    private long serialNo;

    @Override
    public DataType getDataType() {
        return DataType.MarketDepth;
    }

    @Override
    public String csvOut() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add(time.toString())
                .add(sym)
                .add(String.valueOf(bidPrice))
                .add(String.valueOf(askPrice))
                .add(String.valueOf(bidQuantity))
                .add(String.valueOf(askQuantity))
                .add(String.valueOf(bidNumOrder))
                .add(String.valueOf(askNumOrder))
                // Depth
                .add(convertListToString(bidPrices))
                .add(convertListToString(askPrices))
                .add(convertListToString(bidQuantities))
                .add(convertListToString(askQuantities))
                .add(convertListToString(bidNumOrders))
                .add(convertListToString(askNumOrders))
                // Others
                .add(updateType.name())
                .add(String.valueOf(updateNo));
        return sj.toString();
    }

    public String csvOutBestOnly() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add(time.toString())
                .add(sym)
                .add(String.valueOf(bidPrice))
                .add(String.valueOf(askPrice))
                .add(String.valueOf(bidQuantity))
                .add(String.valueOf(askQuantity))
                // Others
                .add(updateType.name())
                .add(String.valueOf(updateNo));
        return sj.toString();
    }

    public static String csvHeader() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add("time")
                .add("sym")
                .add("bidPrice")
                .add("askPrice")
                .add("bidQuantity")
                .add("askQuantity")
                .add("bidNumOrder")
                .add("askNumOrder")
                // Depth
                .add("bidPrices")
                .add("askPrices")
                .add("bidQuantities")
                .add("askQuantities")
                .add("bidNumOrders")
                .add("askNumOrders")
                // Others
                .add("updateType")
                .add("updateNo");
        return sj.toString();
    }

    public static String csvHeaderBestOnly() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add("time")
                .add("sym")
                .add("bidPrice")
                .add("askPrice")
                .add("bidQuantity")
                .add("askQuantity")
                // Others
                .add("updateType")
                .add("updateNo");
        return sj.toString();
    }

    private String convertListToString(double[] values) {
        StringJoiner sj = new StringJoiner(LIST_SEPARATOR);
        for (double d : values) {
            sj.add(String.valueOf(d));
        }
        return sj.toString();
    }

    private String convertListToString(long[] values) {
        StringJoiner sj = new StringJoiner(LIST_SEPARATOR);
        for (long l : values) {
            sj.add(String.valueOf(l));
        }
        return sj.toString();
    }

    // //////////////////////////////////////
    // Getters and Setters
    // //////////////////////////////////////

    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }
    public String getSym() {
        return sym;
    }
    public void setSym(String sym) {
        this.sym = sym;
    }
    public IssueClassificationType getClassification() {
        return classification;
    }
    public void setClassification(IssueClassificationType classification) {
        this.classification = classification;
    }
    public double getBidPrice() {
        return bidPrice;
    }
    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }
    public double getAskPrice() {
        return askPrice;
    }
    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }
    public long getBidQuantity() {
        return bidQuantity;
    }
    public void setBidQuantity(long bidQuantity) {
        this.bidQuantity = bidQuantity;
    }
    public long getAskQuantity() {
        return askQuantity;
    }
    public void setAskQuantity(long askQuantity) {
        this.askQuantity = askQuantity;
    }
    public long getBidNumOrder() {
        return bidNumOrder;
    }
    public void setBidNumOrder(long bidNumOrder) {
        this.bidNumOrder = bidNumOrder;
    }
    public long getAskNumOrder() {
        return askNumOrder;
    }
    public void setAskNumOrder(long askNumOrder) {
        this.askNumOrder = askNumOrder;
    }
    public double[] getBidPrices() {
        return bidPrices;
    }
    public void setBidPrices(double[] bidPrices) {
        this.bidPrices = bidPrices;
    }
    public double[] getAskPrices() {
        return askPrices;
    }
    public void setAskPrices(double[] askPrices) {
        this.askPrices = askPrices;
    }
    public long[] getBidQuantities() {
        return bidQuantities;
    }
    public void setBidQuantities(long[] bidQuantities) {
        this.bidQuantities = bidQuantities;
    }
    public long[] getAskQuantities() {
        return askQuantities;
    }
    public void setAskQuantities(long[] askQuantities) {
        this.askQuantities = askQuantities;
    }
    public long[] getBidNumOrders() {
        return bidNumOrders;
    }
    public void setBidNumOrders(long[] bidNumOrders) {
        this.bidNumOrders = bidNumOrders;
    }
    public long[] getAskNumOrders() {
        return askNumOrders;
    }
    public void setAskNumOrders(long[] askNumOrders) {
        this.askNumOrders = askNumOrders;
    }
    public UpdateType getUpdateType() {
        return updateType;
    }
    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }
    public int getUpdateNo() {
        return updateNo;
    }
    public void setUpdateNo(int updateNo) {
        this.updateNo = updateNo;
    }
    public long getSerialNo() {
        return serialNo;
    }
    public void setSerialNo(long serialNo) {
        this.serialNo = serialNo;
    }

}
