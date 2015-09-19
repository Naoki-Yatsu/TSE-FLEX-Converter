package ny2.flex.database.impl;

import java.util.List;

import com.exxeleron.qjava.QTimespan;

import ny2.flex.data.MarketDepth;
import ny2.flex.database.KdbConverter;
import ny2.flex.database.KdbUtility;

public class MarketBestKdbConverter implements KdbConverter<MarketDepth> {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final String TABLE_NAME = "MarketBest";


    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Object[] convert(List<MarketDepth> dataList) {
        return convertInternal(dataList);
    }

    public Object[] convertInternal(List<MarketDepth> dataList) {
        int rowCount = dataList.size();
        KdbMarketBest kdbData = new KdbMarketBest(rowCount);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            kdbData.addData(rowIndex, dataList.get(rowIndex));
        }
        return kdbData.toKdbDataObject();
    }

    @Override
    public Object[] convert(MarketDepth data) {
        KdbMarketBest kdbData = new KdbMarketBest(1);
        kdbData.addData(0, data);
        return kdbData.toKdbDataObject();
    }

    // //////////////////////////////////////
    // Inner Class
    // //////////////////////////////////////

    //
    //    c            | t f a
    //    -------------| -----
    //    time         | n
    //    sym          | s
    //    bidPrice     | f
    //    askPrice     | f
    //    bidQuantity  | j
    //    askQuantity  | j
    //    updateType   | s
    //    updateNo     | i
    //    serialNo     | j
    //
    private class KdbMarketBest {
        // head
        QTimespan[] timeArray;
        String[] symArray;
        // Best
        double[] bidPriceArray;
        double[] askPriceArray;
        long[] bidQuantityArray;
        long[] askQuantityArray;
        // Others
        String[] updateTypeArray;
        int[] updateNoArray;
        long[] serialNoArray;

        public KdbMarketBest(int rowCount) {
            // head
            timeArray = new QTimespan[rowCount];
            symArray = new String[rowCount];
            // Best
            bidPriceArray = new double[rowCount];
            askPriceArray = new double[rowCount];
            bidQuantityArray = new long[rowCount];
            askQuantityArray = new long[rowCount];
            // Others
            updateTypeArray = new String[rowCount];
            updateNoArray = new int[rowCount];
            serialNoArray = new long[rowCount];
        }

        public void addData(int rowIndex, MarketDepth data) {
            // head
            timeArray[rowIndex] = KdbUtility.kdbValueTimespan(data.getTime());
            symArray[rowIndex] = KdbUtility.kdbValue(data.getSym());
            // Best
            bidPriceArray[rowIndex] = data.getBidPrice();
            askPriceArray[rowIndex] = data.getAskPrice();
            bidQuantityArray[rowIndex] = data.getBidQuantity();
            askQuantityArray[rowIndex] = data.getAskQuantity();
            // Others
            updateTypeArray[rowIndex] = KdbUtility.kdbValue(data.getUpdateType());
            updateNoArray[rowIndex] = data.getUpdateNo();
            serialNoArray[rowIndex] = data.getSerialNo();
        }

        public Object[] toKdbDataObject() {
            return new Object[] {
                    // head
                    timeArray,
                    symArray,
                    // Best
                    bidPriceArray,
                    askPriceArray,
                    bidQuantityArray,
                    askQuantityArray,
                    // Others
                    updateTypeArray,
                    updateNoArray,
                    serialNoArray };
        }
    }
}
