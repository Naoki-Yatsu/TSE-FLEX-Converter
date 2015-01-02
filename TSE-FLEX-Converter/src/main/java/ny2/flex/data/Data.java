package ny2.flex.data;

public abstract class Data {

    private static String csvSeparator = ",";

    protected static final String LIST_SEPARATOR = " ";

    public static String getCsvSeparator() {
        return csvSeparator;
    }

    public static void setCsvSeparator(String csvSeparator) {
        Data.csvSeparator = csvSeparator;
    }

    /**
     * Output data as csv format.
     * @return
     */
    public abstract String csvOut();


    /**
     * Data Type
     * @return
     */
    public abstract DataType getDataType();

}
