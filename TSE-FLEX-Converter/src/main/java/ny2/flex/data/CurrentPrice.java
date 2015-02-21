package ny2.flex.data;

import java.time.LocalTime;
import java.util.StringJoiner;

public class CurrentPrice extends Data {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private LocalTime time;
    private String sym;

    private double price;
    private String state;

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
        return DataType.CurrentPrice;
    }

    @Override
    public String csvOut() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add(time.toString())
                .add(sym)
                .add(convertPriceString(price))
                .add(String.valueOf(updateNo));
        return sj.toString();
    }

    public static String csvHeader() {
        StringJoiner sj = new StringJoiner(getCsvSeparator());
        sj.add("time")
                .add("sym")
                .add("price")
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
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
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
