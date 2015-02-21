package ny2.flex.database.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ny2.flex.data.Data;
import ny2.flex.database.MarketBestManager;
import ny2.flex.database.OutputDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractDao implements OutputDao {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    // Logger
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // output target table
    @Value("${output.MarketDepth}")
    protected boolean outMarketDepth;

    @Value("${output.MarketBest}")
    protected boolean outMarketBest;

    @Value("${output.MarketTrade}")
    protected boolean outMarketTrade;

    @Value("${output.CurrentPrice}")
    protected boolean outCurrentPrice;

    /** current target date */
    protected LocalDate targetDate;

    /** extract MarketBest */
    protected MarketBestManager marketBestManager;

    /** Data Storing Queue */
    protected BlockingQueue<Data> dataQueue = new LinkedBlockingQueue<>(1000000);

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    @Override
    public void setTargetDate(LocalDate targetDate) {
        logger.info("Set date : {}", targetDate.toString());
        this.targetDate = targetDate;
        marketBestManager = new MarketBestManager();
    }

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
}
