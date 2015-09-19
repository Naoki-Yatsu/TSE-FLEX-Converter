package ny2.flex.database;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ny2.flex.data.Data;

public interface KdbConverter<T extends Data> {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    // Logger
    public static final Logger logger = LoggerFactory.getLogger(KdbConverter.class);

    public static final char[] EMPTY_CHAR_ARRAY = new char[0];

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * Insert target table
     * @return
     */
    public String getTableName();

    /**
     * Convert data to kdb style.
     *
     * @param dataList
     * @return
     */
    public Object[] convert(List<T> dataList);

    /**
     * Convert one data to kdb style.
     *
     * @param data
     * @return
     */
    public Object[] convert(T data);

}
