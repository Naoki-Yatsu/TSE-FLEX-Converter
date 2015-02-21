package ny2.flex.database;

import java.time.LocalDate;
import java.util.List;

import ny2.flex.data.Data;

public interface OutputDao {

    /**
     * Set date of data.
     * @param targetDate
     */
    public void setTargetDate(LocalDate targetDate);

    /**
     * insert dataList
     * @param dataList
     */
    public void insertList(List<Data> dataList);

    /**
     * Write memory data to splayed table
     */
    public void wirteToDisk();

    /**
     * Finalize splayed tables
     */
    public void finalizeData();

    /**
     * If writing yet, return true.
     */
    public boolean isWriting();

}
