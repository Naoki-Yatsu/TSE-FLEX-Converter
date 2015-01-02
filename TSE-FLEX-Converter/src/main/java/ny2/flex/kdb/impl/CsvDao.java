package ny2.flex.kdb.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import ny2.flex.data.Data;
import ny2.flex.fileio.CsvFileWriter;
import ny2.flex.kdb.KdbConverter;
import ny2.flex.kdb.KdbDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository("CsvDao")
public class CsvDao implements KdbDao {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // output target table
    @Value("${output.MarketDepth}")
    private boolean outMarketDepth;

    @Value("${output.MarketBest}")
    private boolean outMarketBest;

    @Value("${output.MarketTrade}")
    private boolean outMarketTrade;

    @Value("${output.CurrentPrice}")
    private boolean outCurrentPrice;

    // csv params
    @Value("${csv.output.path}")
    private String csvOutputPath;

    @Value("${csv.file.split.date}")
    private boolean splitDate;

    @Value("${csv.file.split.code}")
    private boolean splitCode;

    /** Executer for Insert data to database */
    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    /** Data Storing Queue */
    private BlockingQueue<Data> dataQueue = new LinkedBlockingQueue<>(1000000);

    /** Lock for kdb writing */
    private Lock lock = new ReentrantLock();

    /** file writer */
    private CsvFileWriter marketDepthWriter;
    private CsvFileWriter marketBestWriter;
    private CsvFileWriter marketTradeWriter;
    private CsvFileWriter currentPriceWriter;

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

    @PreDestroy
    private void closeConnection() {

    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public void insertList(List<Data> dataList) {
        for (Data data : dataList) {
            try {
                dataQueue.put(data);
            } catch (InterruptedException e) {
                logger.error("");
            }
        }
    }

    private void insertList(List<Data> dataList, KdbConverter converter) {

    }

    @Override
    public void wirteSplayedTables(LocalDate localDate) {
        lock.lock();

        lock.unlock();
    }

    @Override
    public void finalizeSplayedTables() {
        lock.lock();

        lock.unlock();
    }

    @Override
    public boolean isWriting() {
        if (dataQueue.size() > 0) {
            logger.debug("Writing data. remain = {}", dataQueue.size());
            return true;
        } else {
            return false;
        }
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
                return;
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

            if (!marketDepthList.isEmpty()) {
                if (outMarketDepth) {
//                    insertList(marketDepthList, marketDepthKdbConverter);
                }
                if (outMarketBest) {
//                    insertList(marketDepthList, marketBestKdbConverter);
                }
            }
            if (!marketTradeList.isEmpty() && outMarketTrade) {
//                insertList(marketTradeList, marketTradeKdbConverter);
            }
            if (!currentPriceList.isEmpty() && outCurrentPrice) {
//                insertList(currentPriceList, currentPriceKdbConverter);
            }
        }
    }

}
