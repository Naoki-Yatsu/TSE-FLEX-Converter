package ny2.flex.database.impl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import ny2.flex.common.DateTimeUtility;
import ny2.flex.data.CurrentPrice;
import ny2.flex.data.Data;
import ny2.flex.data.MarketDepth;
import ny2.flex.data.MarketTrade;
import ny2.flex.fileio.CsvFileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository("CsvDao")
public class CsvDao extends AbstractDao {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // csv params
    @Value("${csv.output.dir}")
    private String csvOutputDir;

    @Value("${csv.file.header}")
    private boolean outHeader;

    @Value("${csv.file.split.date}")
    private boolean splitDate;

    @Value("${csv.file.split.code}")
    private boolean splitCode;

    // csv date params
    @Value("${csv.data.separator.datetime}")
    private String separatorDateTIme;

//    @Value("${csv.data.format.date}")
//    private String formetStringDate;

    private DateTimeFormatter dateFormatter;

    @Value("${csv.data.format.date}")
    private void setDateFormatter(String format) {
        dateFormatter = DateTimeFormatter.ofPattern(format);
    }

    /** Executer for Insert data to database */
    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private AtomicBoolean executerRunningStatus = new AtomicBoolean(true);

    /** file writer */
    private CsvFileWriter marketDepthWriter;
    private CsvFileWriter marketBestWriter;
    private CsvFileWriter marketTradeWriter;
    private CsvFileWriter currentPriceWriter;

    private boolean writerInitialized = false;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public CsvDao() {
        logger.info("Create instance.");
    }

    @PostConstruct
    private void init() {
        logger.info("PostConstruct instance.");
        scheduledExecutor.scheduleWithFixedDelay(new BatchFileWriteWorker(), 0, 1000, TimeUnit.MILLISECONDS);

    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public void setTargetDate(LocalDate targetDate) {
        super.setTargetDate(targetDate);
        if (!splitDate && writerInitialized) {
            return;
        }

        // close previous writer
        finalizeData();

        // re-create csv writer
        createWriter();
    }

    private void createWriter() {
        try {
            String outDir = csvOutputDir;
            if (splitDate && splitCode) {
                outDir = csvOutputDir + File.pathSeparator + targetDate.format(DateTimeUtility.SIMPLE_DATE_FORMATTER);
            }
            marketDepthWriter = new CsvFileWriter("MarketDepth", outDir, splitDate, targetDate, splitCode, outHeader, MarketDepth.csvHeader(), targetDate.format(dateFormatter) + separatorDateTIme);
            marketBestWriter = new CsvFileWriter("MarketBest", outDir, splitDate, targetDate, splitCode, outHeader, MarketDepth.csvHeaderBestOnly(), targetDate.format(dateFormatter) + separatorDateTIme);
            marketTradeWriter = new CsvFileWriter("MarketTrade", outDir, splitDate, targetDate, splitCode, outHeader, MarketTrade.csvHeader(), targetDate.format(dateFormatter) + separatorDateTIme);
            currentPriceWriter = new CsvFileWriter("CurrentPrice", outDir, splitDate, targetDate, splitCode, outHeader, CurrentPrice.csvHeader(), targetDate.format(dateFormatter) + separatorDateTIme);

            writerInitialized = true;
        } catch (IOException e) {
            logger.error("Error in createing writer.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void wirteToDisk() {
        // Do nothing
    }

    @Override
    public void finalizeData() {
        if (marketDepthWriter != null) {
            marketDepthWriter.closeAll();
        }
        if (marketBestWriter != null) {
            marketBestWriter.closeAll();
        }
        if (marketTradeWriter != null) {
            marketTradeWriter.closeAll();
        }
        if (currentPriceWriter != null) {
            currentPriceWriter.closeAll();
        }
    }

    @Override
    public boolean isWriting() {
        return executerRunningStatus.get();
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    // //////////////////////////////////////
    // Inner Class
    // //////////////////////////////////////

    private class BatchFileWriteWorker implements Runnable {
        @Override
        public void run() {
            // drain from queue and create each list
            int queueSize = dataQueue.size();
            if (queueSize == 0) {
                executerRunningStatus.set(false);
                return;
            } else {
                executerRunningStatus.set(true);
            }

            List<Data> allDataList = new ArrayList<>(queueSize);
            dataQueue.drainTo(allDataList, queueSize);

            // each data list
            List<Data> marketDepthList = new ArrayList<Data>();
            List<Data> marketTradeList = new ArrayList<Data>();
            List<Data> currentPriceList = new ArrayList<Data>();

            for (Data data : allDataList) {
                switch (data.getDataType()) {
                    case MarketDepth:
                        marketDepthList.add(data);
                        break;
                    case MarketTrade:
                        marketTradeList.add(data);
                        break;
                    case CurrentPrice:
                        currentPriceList.add(data);
                        break;
                    default:
                        break;
                }
            }

            // write csv
            try {
                if (!marketDepthList.isEmpty()) {
                    if (outMarketDepth) {
                        marketDepthWriter.write(marketDepthList);
                    }
                    if (outMarketBest) {
                        ArrayList<Data> marketBestList = new ArrayList<>(marketDepthList.size());
                        // Check best price is updated.
                        marketDepthList.stream()
                                .filter(data -> marketBestManager.checkBestUpdated((MarketDepth) data))
                                .forEach(data -> marketBestList.add(data));
                        marketBestWriter.write(marketBestList);
                    }
                }
                if (!marketTradeList.isEmpty() && outMarketTrade) {
                    marketTradeWriter.write(marketTradeList);
                    ;
                }
                if (!currentPriceList.isEmpty() && outCurrentPrice) {
                    currentPriceWriter.write(currentPriceList);
                }

            } catch (Exception e) {
                logger.error("Error in writing csv.", e);
                throw new RuntimeException(e);
            }
        }
    }

}
