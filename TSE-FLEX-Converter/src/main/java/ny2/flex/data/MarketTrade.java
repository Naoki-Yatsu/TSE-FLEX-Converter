package ny2.flex.data;

import java.time.LocalTime;
import java.util.StringJoiner;

import lombok.ToString;
import ny2.flex.common.Side;

@ToString
public class MarketTrade extends Data {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private LocalTime time;
    private String sym;

    // Trade
    private Side side;
    private double price;
    private long quantity;

    // Total
    private long totalQuantity;
    private long totalTurnover;

    // Others
    private int updateNo;
    private long serialNo;

    // not output
    private boolean isIntegerPrice;

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public DataType getDataType() {
        return DataType.MarketTrade;
    }

    @Override
    public String csvOut() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add(time.toString())
                .add(sym)
                .add(side.name())
                .add(convertPriceString(price))
                .add(String.valueOf(quantity))
                .add(String.valueOf(totalQuantity))
                .add(String.valueOf(totalTurnover))
                .add(String.valueOf(updateNo));
        return sj.toString();
    }

    public static String csvHeader() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add("time")
                .add("sym")
                .add("side")
                .add("price")
                .add("quantity")
                .add("totalQuantity")
                .add("totalTurnover")
                .add("updateNo");
        return sj.toString();
    }

    private String convertPriceString(double price) {
        if (isIntegerPrice) {
            return String.valueOf((int) price);
        } else {
            return String.valueOf(price);
        }
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

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public long getTotalTurnover() {
        return totalTurnover;
    }

    public void setTotalTurnover(long totalTurnover) {
        this.totalTurnover = totalTurnover;
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

    public boolean isIntegerPrice() {
        return isIntegerPrice;
    }

    public void setIntegerPrice(boolean isIntegerPrice) {
        this.isIntegerPrice = isIntegerPrice;
    }

}
