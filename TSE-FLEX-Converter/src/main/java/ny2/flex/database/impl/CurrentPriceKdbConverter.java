package ny2.flex.database.impl;

import java.util.List;

import com.exxeleron.qjava.QTimespan;

import ny2.flex.data.CurrentPrice;
import ny2.flex.database.KdbConverter;
import ny2.flex.database.KdbUtility;

public class CurrentPriceKdbConverter implements KdbConverter<CurrentPrice> {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final String TABLE_NAME = "CurrentPrice";

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Object[] convert(List<CurrentPrice> dataList) {
        return convertInternal((List<CurrentPrice>) dataList);
    }

    public Object[] convertInternal(List<CurrentPrice> dataList) {
        int rowCount = dataList.size();
        KdbCurrentPrice kdbData = new KdbCurrentPrice(rowCount);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            kdbData.addData(rowIndex, (CurrentPrice) dataList.get(rowIndex));
        }
        return kdbData.toKdbDataObject();
    }

    @Override
    public Object[] convert(CurrentPrice data) {
        KdbCurrentPrice kdbData = new KdbCurrentPrice(1);
        kdbData.addData(0, (CurrentPrice) data);
        return kdbData.toKdbDataObject();
    }

    // //////////////////////////////////////
    // Inner Class
    // //////////////////////////////////////

    //
    // q> .Q.s meta CurrentPrice
    //    c       | t f a
    //    --------| -----
    //    time    | n
    //    sym     | s
    //    price   | f
    //    state   | s
    //    updateNo| i
    //    serialNo| j
    //
    private class KdbCurrentPrice {
        // head
        QTimespan[] timeArray;
        String[] symArray;
        // Data
        double[] priceArray;
        String[] stateArray;
        // Others
        int[] updateNoArray;
        long[] serialNoArray;

        public KdbCurrentPrice(int rowCount) {
            // head
            timeArray = new QTimespan[rowCount];
            symArray = new String[rowCount];
            // Data
            priceArray = new double[rowCount];
            stateArray = new String[rowCount];
            // Others
            updateNoArray = new int[rowCount];
            serialNoArray = new long[rowCount];
        }

        public void addData(int rowIndex, CurrentPrice data) {
            // head
            timeArray[rowIndex] = KdbUtility.kdbValueTimespan(data.getTime());
            symArray[rowIndex] = KdbUtility.kdbValue(data.getSym());
            // Data
            priceArray[rowIndex] = data.getPrice();
            stateArray[rowIndex] = KdbUtility.kdbValue(data.getState());
            // Others
            updateNoArray[rowIndex] = data.getUpdateNo();
            serialNoArray[rowIndex] = data.getSerialNo();
        }

        public Object[] toKdbDataObject() {
            return new Object[] {
                    // head
                    timeArray,
                    symArray,
                    // Data
                    priceArray,
                    stateArray,
                    // Others
                    updateNoArray,
                    serialNoArray };
        }
    }
}
