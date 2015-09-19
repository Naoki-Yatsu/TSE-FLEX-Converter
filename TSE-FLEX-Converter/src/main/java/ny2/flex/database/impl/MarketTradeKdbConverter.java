package ny2.flex.database.impl;

import java.util.List;

import com.exxeleron.qjava.QTimespan;

import ny2.flex.data.MarketTrade;
import ny2.flex.database.KdbConverter;
import ny2.flex.database.KdbUtility;

public class MarketTradeKdbConverter implements KdbConverter<MarketTrade> {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final String TABLE_NAME = "MarketTrade";


    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Object[] convert(List<MarketTrade> dataList) {
        return convertInternal(dataList);
    }

    public Object[] convertInternal(List<MarketTrade> dataList) {
        int rowCount = dataList.size();
        KdbMarketTrade kdbData = new KdbMarketTrade(rowCount);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            kdbData.addData(rowIndex, dataList.get(rowIndex));
        }
        return kdbData.toKdbDataObject();
    }

    @Override
    public Object[] convert(MarketTrade data) {
        KdbMarketTrade kdbData = new KdbMarketTrade(1);
        kdbData.addData(0, data);
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
            timeArray[rowIndex] = KdbUtility.kdbValueTimespan(data.getTime());
            symArray[rowIndex] = KdbUtility.kdbValue(data.getSym());
            // Best
            sideArray[rowIndex] = KdbUtility.kdbValue(data.getSide());
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
