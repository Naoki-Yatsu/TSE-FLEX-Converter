package ny2.flex.database.impl;

import java.util.List;

import ny2.flex.data.Data;
import ny2.flex.data.MarketTrade;
import ny2.flex.database.KdbConverter;

import com.exxeleron.qjava.QTimespan;

public class MarketTradeKdbConverter implements KdbConverter {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final String TABLE_NAME = "MarketTrade";

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Data> Object[] convert(List<T> dataList) {
        return convertInternal((List<MarketTrade>) dataList);
    }

    public Object[] convertInternal(List<MarketTrade> dataList) {
        int rowCount = dataList.size();
        KdbMarketTrade kdbData = new KdbMarketTrade(rowCount);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            kdbData.addData(rowIndex, (MarketTrade) dataList.get(rowIndex));
        }
        return kdbData.toKdbDataObject();
    }

    @Override
    public Object[] convert(Data data) {
        KdbMarketTrade kdbData = new KdbMarketTrade(1);
        kdbData.addData(0, (MarketTrade) data);
        return kdbData.toKdbDataObject();
    }

    // //////////////////////////////////////
    // Inner Class
    // //////////////////////////////////////

    //
    // q> .Q.s meta MarketTrade
    //    c                | t f a
    //    -----------------| -----
    //    time             | n
    //    sym              | s
    //    side             | s
    //    averagePrice     | f
    //    currentPrice     | f
    //    quantity         | j
    //    executePrices    | F
    //    executeQuantities| J
    //    totalQuantity    | j
    //    totalTurnover    | j
    //    updateNo         | i
    //    serialNo         | j
    //
    private class KdbMarketTrade {
        // head
        QTimespan[] timeArray;
        String[] symArray;
        // Trade
        String[] sideArray;
        double[] priceArray;
        long[] quantityArray;
        long[] totalQuantityArray;
        long[] totalTurnoverArray;
        // Others
        int[] updateNoArray;
        long[] serialNoArray;

        public KdbMarketTrade(int rowCount) {
            // head
            timeArray = new QTimespan[rowCount];
            symArray = new String[rowCount];
            // Best
            sideArray = new String[rowCount];
            priceArray = new double[rowCount];
            quantityArray = new long[rowCount];
            totalQuantityArray = new long[rowCount];
            totalTurnoverArray = new long[rowCount];
            // Others
            updateNoArray = new int[rowCount];
            serialNoArray = new long[rowCount];
        }

        public void addData(int rowIndex, MarketTrade data) {
            // head
            timeArray[rowIndex] = KdbConverter.kdbValueTimespan(data.getTime());
            symArray[rowIndex] = KdbConverter.kdbValue(data.getSym());
            // Best
            sideArray[rowIndex] = KdbConverter.kdbValue(data.getSide());
            priceArray[rowIndex] = data.getPrice();
            quantityArray[rowIndex] = data.getQuantity();
            totalQuantityArray[rowIndex] = data.getTotalQuantity();
            totalTurnoverArray[rowIndex] = data.getTotalTurnover();
            // Others
            updateNoArray[rowIndex] = data.getUpdateNo();
            serialNoArray[rowIndex] = data.getSerialNo();
        }

        public Object[] toKdbDataObject() {
            return new Object[] {
                    // head
                    timeArray,
                    symArray,
                    // Trade
                    sideArray,
                    priceArray,
                    quantityArray,
                    totalQuantityArray,
                    totalTurnoverArray,
                    // Others
                    updateNoArray,
                    serialNoArray };
        }
    }
}
