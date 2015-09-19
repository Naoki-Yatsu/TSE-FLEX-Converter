package ny2.flex.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ny2.flex.common.DateTimeUtility;
import ny2.flex.data.Data;

/**
 * CSV file writer
 *
 * filename rule sample
 * Default : MarketDepth.csv
 * Split Code : MarketDepth_1234.csv
 * Split Date : MarketDepth_20140123.csv
 * Split Both : MarketDepth_1234_20140123.csv
 */
public class CsvFileWriter {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String filenameBase;

    private final String path;

    private final String suffixDate;

    /** write mode of split */
    private final boolean splitByCode;

    /** output header or not */
    private final boolean outHeader;

    /** Header */
    private final String header;

    /** Add head of each row as date column */
    private final String dateColumValue;

    // For Not splitByCode
    /** writer or not splitByCode */
    private BufferedWriter commonWriter;

    // For splitByCode
    /** writer, key is symbol */
    private Map<String, BufferedWriter> writerMap;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public CsvFileWriter(String filenameBase, String path, boolean splitByDate, LocalDate targetDate, boolean splitByCode, boolean outHeader, String header, String dateColumValue) throws IOException {
        this.filenameBase = filenameBase;
        if (path.endsWith(File.separator)) {
            this.path = path;
        } else {
            this.path = path + File.separator;
        }
        if (splitByDate) {
            this.suffixDate = "_" + targetDate.format(DateTimeUtility.SIMPLE_DATE_FORMATTER);
        } else {
            this.suffixDate = "";
        }
        this.splitByCode = splitByCode;
        this.outHeader = outHeader;
        this.header = header;
        this.dateColumValue = dateColumValue;

        // Check path dir is exist or not
        File dir = new File(path);
        if (!dir.exists()) {
            logger.info("Create directory : {}", dir.getPath());
            dir.mkdirs();
        }

        if (splitByCode) {
            writerMap = new HashMap<>();
        } else {
            createCommonWriter();
        }
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * Create Writer
     *
     * @throws IOException
     */
    private void createCommonWriter() throws IOException {
        String filename = path + filenameBase + suffixDate + ".csv";
        logger.info("Open writer. {}", filename);
        this.commonWriter = new BufferedWriter(new FileWriter(filename));
        if (outHeader) {
            commonWriter.write(header);
            commonWriter.newLine();
        }
    }

    /**
     * Create Writer
     *
     * @throws IOException
     */
    private BufferedWriter createWriter(String sym) throws IOException {
        String filename = path + filenameBase + "_" + sym + suffixDate + ".csv";
        logger.info("Open writer. {}", filename);
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writerMap.put(sym, writer);
        if (outHeader) {
            writer.write(header);
            writer.newLine();
        }

        return writer;
    }

    /**
     * Close Writer at last of day
     */
    public void closeAll() {
        logger.info("Close all writer. {}", filenameBase);
        // Not splitByCode
        if (!splitByCode) {
            try {
                commonWriter.close();
            } catch (IOException e) {
                logger.error("Failed to close writer.", e);
            }
            return;
        }

        // splitByCode
        for (BufferedWriter writer : writerMap.values()) {
            try {
                writer.close();
            } catch (IOException e) {
                logger.error("Failed to close writer.", e);
            }
        }
    }

    /**
     * Write data as csv
     * @param dataList
     * @throws IOException
     */
    public void write(List<Data> dataList) throws IOException {
        if (!splitByCode) {
            writeData(dataList, commonWriter);
            return;
        }

        // for split
        // create list split by code
        Map<String, List<Data>> symDataMap = new HashMap<>(10000);
        for (Data data : dataList) {
            // add depth to target list
            List<Data> symDataList = symDataMap.get(data.getSym());
            if (symDataList == null) {
                symDataList = new ArrayList<>();
                symDataMap.put(data.getSym(), symDataList);
            }
            symDataList.add(data);
        }

        for (Entry<String, List<Data>> dataEntry : symDataMap.entrySet()) {
            BufferedWriter writer = writerMap.get(dataEntry.getKey());
            if (writer == null) {
                writer = createWriter(dataEntry.getKey());
                writerMap.put(dataEntry.getKey(), writer);
            }
            // write csv
            writeData(dataEntry.getValue(), writer);
        }
    }

    private void writeData(List<Data> dataList, BufferedWriter writer) throws IOException {
        for (Data data : dataList) {
            writer.write(dateColumValue);
            writer.write(data.csvOut());
            writer.newLine();
        }
    }

}
