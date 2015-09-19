package ny2.flex.database.impl;

import java.util.List;

import ny2.flex.data.IssueInformation;
import ny2.flex.database.KdbConverter;
import ny2.flex.database.KdbUtility;

public class IssueInformationKdbConverter implements KdbConverter<IssueInformation> {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final String TABLE_NAME = "IssueInformation";

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Object[] convert(List<IssueInformation> dataList) {
        return convertInternal((List<IssueInformation>) dataList);
    }

    public Object[] convertInternal(List<IssueInformation> dataList) {
        int rowCount = dataList.size();
        KdbIssueInformation kdbData = new KdbIssueInformation(rowCount);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            kdbData.addData(rowIndex, (IssueInformation) dataList.get(rowIndex));
        }
        return kdbData.toKdbDataObject();
    }

    @Override
    public Object[] convert(IssueInformation data) {
        KdbIssueInformation kdbData = new KdbIssueInformation(1);
        kdbData.addData(0, data);
        return kdbData.toKdbDataObject();
    }

    // //////////////////////////////////////
    // Inner Class
    // //////////////////////////////////////

    //
    // q> .Q.s meta IssueInformation
    //    c       | t f a
    //    --------| -----
    //    sym     | s
    //    exchangeCode       | i
    //    classificationCode | s
    //
    private class KdbIssueInformation {
        String[] symArray;
        int[] exchangeCodeArray;
        String[] classificationCodeArray;

        public KdbIssueInformation(int rowCount) {
            symArray = new String[rowCount];
            exchangeCodeArray = new int[rowCount];
            classificationCodeArray = new String[rowCount];
        }

        public void addData(int rowIndex, IssueInformation data) {
            symArray[rowIndex] = KdbUtility.kdbValue(data.getSym());
            exchangeCodeArray[rowIndex] = data.getExchangeCode();
            classificationCodeArray[rowIndex] = KdbUtility.kdbValue(data.getIssueClassificationType().getCode());
        }

        public Object[] toKdbDataObject() {
            return new Object[] {
                    symArray,
                    exchangeCodeArray,
                    classificationCodeArray};
        }
    }
}
