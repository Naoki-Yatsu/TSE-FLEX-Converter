package ny2.flex.database.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.exxeleron.qjava.QBasicConnection;
import com.exxeleron.qjava.QException;

import ny2.flex.data.CurrentPrice;
import ny2.flex.data.Data;
import ny2.flex.data.IssueInformation;
import ny2.flex.data.MarketDepth;
import ny2.flex.data.MarketTrade;
import ny2.flex.database.KdbConverter;
import ny2.flex.database.KdbUtility;

/**
 * kdb dao
 */
//@Repository("KdbDao") -> xml
public class KdbDao extends AbstractDao {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // connection parameter
    @Value("${kdb.host}")
    private String host;

    @Value("${kdb.port.tp}")
    private int port;

    @Value("${kdb.username}")
    private String username;

    @Value("${kdb.password}")
    private String password;

    // output parameter
    @Value("${kdb.table.split.code}")
    private boolean splitByCode;

    /** insert function */
    private static final String Q_INSERT = "insert";
    private static final String Q_WRITE = "writeAndClear";
    private static final String Q_FINALIZE = "finish[]";

    // kdb converter
    private MarketDepthKdbConverter marketDepthKdbConverter = new MarketDepthKdbConverter();
    private MarketBestKdbConverter marketBestKdbConverter = new MarketBestKdbConverter();
    private MarketTradeKdbConverter marketTradeKdbConverter = new MarketTradeKdbConverter();
    private CurrentPriceKdbConverter currentPriceKdbConverter = new CurrentPriceKdbConverter();
    private IssueInformationKdbConverter issueInformationKdbConverter = new IssueInformationKdbConverter();

    /** Executer for Insert data to database */
    // private ScheduledThreadPoolExecutor scheduledExecutor = (ScheduledThreadPoolExecutor) Executors.newSingleThreadScheduledExecutor();
    private ScheduledThreadPoolExecutor scheduledExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    /** Lock for kdb writing */
    private Lock lock = new ReentrantLock();

    // Q connection. Should be 1, or need lock function.
    // If insert and writing disk are done in the same time, some data will be lost.
    private static final int CONNECTION_POOL_SIZE = 1;
    private BlockingQueue<QBasicConnection> connectionPool = new LinkedBlockingQueue<>(CONNECTION_POOL_SIZE);

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public KdbDao() {
        logger.info("Create instance.");
    }

    @PostConstruct
    private void init() {
        logger.info("PostConstruct instance.");
        scheduledExecutor.scheduleWithFixedDelay(new BatchInsertWorker(), 0, 200, TimeUnit.MILLISECONDS);

        // create connections
        for (int i = 0; i < CONNECTION_POOL_SIZE; i++) {
            connectionPool.add(createConnection());
        }
    }

    @PreDestroy
    private void closeConnection() {
        for (QBasicConnection qBasicConnection : connectionPool) {
            try {
                qBasicConnection.close();
            } catch (IOException e) {
            }
        }
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    private <T extends Data> void insertList(List<T> dataList, KdbConverter<T> converter) {
        lock.lock();
        executeQuery(Q_INSERT, converter.getTableName(), converter.convert(dataList));
        lock.unlock();
    }

    private void insertListSplitDepth(List<MarketDepth> dataList, MarketDepthKdbConverter converter) {
        // create list split by code
        Map<String, List<MarketDepth>> symDepthMap = new HashMap<>(10000);
        for (MarketDepth depth : dataList) {
            // add depth to target list
            List<MarketDepth> depthList = symDepthMap.get(depth.getSym());
            if (depthList == null) {
                depthList = new ArrayList<>();
                symDepthMap.put(depth.getSym(), depthList);
            }
            depthList.add(depth);
        }
        lock.lock();
        for (Entry<String, List<MarketDepth>> entry : symDepthMap.entrySet()) {
            executeQuery(Q_INSERT, converter.getTableName() + "_" + entry.getKey(), converter.convert(dataList));
        }
        lock.unlock();
    }

    @Override
    public void wirteToDisk() {
        lock.lock();

        logger.info("Write splayed table. date = {}", targetDate.toString());

        if (outMarketDepth) {
            logger.info("Write splayed table. {}", marketDepthKdbConverter.getTableName());
            executeQuery(Q_WRITE, KdbUtility.kdbValue(targetDate), KdbUtility.kdbValueCharList(marketDepthKdbConverter.getTableName()));
        }
        if (outMarketBest) {
            logger.info("Write splayed table. {}", marketBestKdbConverter.getTableName());
            executeQuery(Q_WRITE, KdbUtility.kdbValue(targetDate), KdbUtility.kdbValueCharList(marketBestKdbConverter.getTableName()));
        }
        if (outMarketTrade) {
            logger.info("Write splayed table. {}", marketTradeKdbConverter.getTableName());
            executeQuery(Q_WRITE, KdbUtility.kdbValue(targetDate), KdbUtility.kdbValueCharList(marketTradeKdbConverter.getTableName()));
        }
        if (outCurrentPrice) {
            logger.info("Write splayed table. {}", currentPriceKdbConverter.getTableName());
            executeQuery(Q_WRITE, KdbUtility.kdbValue(targetDate), KdbUtility.kdbValueCharList(currentPriceKdbConverter.getTableName()));
        }
        if (outIssueInformation) {
            logger.info("Write splayed table. {}", issueInformationKdbConverter.getTableName());
            executeQuery(Q_WRITE, KdbUtility.kdbValue(targetDate), KdbUtility.kdbValueCharList(issueInformationKdbConverter.getTableName()));
        }

        lock.unlock();
    }

    @Override
    public void finalizeData() {
        lock.lock();
        logger.info("Finalize splayed table.");
        executeQuery(Q_FINALIZE);
        lock.unlock();
    }

    @Override
    public boolean isWriting() {
        if (dataQueue.size() > 0 || scheduledExecutor.getActiveCount() > 0) {
            logger.debug("Writing data. queue = {}, active = {}", dataQueue.size(), scheduledExecutor.getActiveCount());
            return true;
        } else {
            return false;
        }
    }

    private void executeQuery(String query, final Object... parameters) {
        QBasicConnection qConnection = getConnection();
        try {
            qConnection.sync(query, parameters);
        } catch (Exception e) {
            StringJoiner sj = new StringJoiner(" ");
            sj.add(query);
            for (Object object : parameters) {
                sj.add(object.toString());
            }
            logger.error("Error in insert data. Reset connection just in case...\n" + sj.toString(), e);
            try {
                qConnection.reset();
            } catch (IOException | QException e1) {
                logger.error("Error in reset connection", e);
            }
        } finally {
            // return connection to queue
            connectionPool.add(qConnection);
        }
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public QBasicConnection getConnection() {
        try {
            QBasicConnection qConnection = connectionPool.take();

            // check connection
            while (!qConnection.isConnected()) {
                try {
                    qConnection.reset();
                    Thread.sleep(20);
                } catch (IOException | QException e) {
                    logger.error("", e);
                }
            }
            ;
            return qConnection;
        } catch (InterruptedException e) {
            logger.error("", e);
        }
        return null;
    }

    private QBasicConnection createConnection() {
        QBasicConnection qConnection = null;
        try {
            qConnection = new QBasicConnection(host, port, username, password);
            qConnection.open();
        } catch (QException | IOException e) {
            logger.error("Error in create connection.", e);
            throw new RuntimeException(e);
        }
        return qConnection;
    }

    // //////////////////////////////////////
    // Inner Class
    // //////////////////////////////////////

    private class BatchInsertWorker implements Runnable {

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
            List<MarketDepth> marketDepthList = new ArrayList<>();
            List<MarketTrade> marketTradeList = new ArrayList<>();
            List<CurrentPrice> currentPriceList = new ArrayList<>();
            List<IssueInformation> issueInformationList = new ArrayList<>();

            for (Data data : allDataList) {
                switch (data.getDataType()) {
                    case MarketDepth:
                        marketDepthList.add((MarketDepth) data);
                        break;
                    case MarketTrade:
                        marketTradeList.add((MarketTrade) data);
                        break;
                    case CurrentPrice:
                        currentPriceList.add((CurrentPrice) data);
                        break;
                    case IssueInformation:
                        issueInformationList.add((IssueInformation) data);
                        break;
                    default:
                        break;
                }
            }
            // MarketDepth & MarketBest
            if (!marketDepthList.isEmpty()) {
                if (outMarketDepth) {
                    if (splitByCode) {
                        insertListSplitDepth(marketDepthList, marketDepthKdbConverter);
                    } else {
                        insertList(marketDepthList, marketDepthKdbConverter);
                    }
                }
                if (outMarketBest) {
                    ArrayList<MarketDepth> marketBestList = new ArrayList<>(marketDepthList.size());
                    // Check best price is updated.
                    marketDepthList.stream()
                            .filter(data -> marketBestManager.checkBestUpdated(data))
                            .forEach(data -> marketBestList.add(data));
                    insertList(marketBestList, marketBestKdbConverter);
                }
            }
            // MarketTrade
            if (!marketTradeList.isEmpty() && outMarketTrade) {
                insertList(marketTradeList, marketTradeKdbConverter);
            }
            // CurrnetPrice
            if (!currentPriceList.isEmpty() && outCurrentPrice) {
                insertList(currentPriceList, currentPriceKdbConverter);
            }
            // IssueInformation
            if (!issueInformationList.isEmpty() && outIssueInformation) {
                insertList(issueInformationList, issueInformationKdbConverter);
            }
        }
    }
}
