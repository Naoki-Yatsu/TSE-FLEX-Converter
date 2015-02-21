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
     * Data Type
     * @return
     */
    public abstract DataType getDataType();

    /**
     * Get Symbol
     * @return
     */
    public abstract String getSym();


    /**
     * Output data as csv format.
     * @return
     */
    public abstract String csvOut();


}
