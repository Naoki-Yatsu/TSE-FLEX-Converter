package ny2.flex.kdb;

import java.time.LocalDate;
import java.util.List;

import ny2.flex.data.Data;

public interface KdbDao {

    /**
     * insert dataList
     * @param dataList
     */
    public void insertList(List<Data> dataList);

    /**
     * Write memory data to splayed table
     */
    public void wirteSplayedTables(LocalDate localDate);

    /**
     * Finalize splayed tables
     */
    public void finalizeSplayedTables();

    /**
     * If writing yet, return true.
     */
    public boolean isWriting();

}
