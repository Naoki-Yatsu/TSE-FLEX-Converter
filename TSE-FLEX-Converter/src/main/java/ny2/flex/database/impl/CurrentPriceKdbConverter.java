package ny2.flex.database.impl;

import java.util.List;

import ny2.flex.data.CurrentPrice;
import ny2.flex.data.Data;
import ny2.flex.database.KdbConverter;

import com.exxeleron.qjava.QTimespan;

public class CurrentPriceKdbConverter implements KdbConverter {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final String TABLE_NAME = "CurrentPrice";

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
    public Object[] convert(Data data) {
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
            timeArray[rowIndex] = KdbConverter.kdbValueTimespan(data.getTime());
            symArray[rowIndex] = KdbConverter.kdbValue(data.getSym());
            // Data
            priceArray[rowIndex] = data.getPrice();
            stateArray[rowIndex] = KdbConverter.kdbValue(data.getState());
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
