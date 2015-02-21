package ny2.flex.app;

import java.io.File;

import ny2.flex.board.BoardManager;
import ny2.flex.common.SystemUtility;
import ny2.flex.database.OutputDao;
import ny2.flex.fileio.FlexFileReader;
import ny2.flex.message.MessageBundle;
import ny2.flex.message.MessageConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FlexConverterImpl implements FlexConverter {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BoardManager boardManager;

    @Autowired
    @Qualifier("CsvDao")
    private OutputDao outputDao;

    @Value("${file.read.size}")
    private int fileReadSize;

    /** Write kdb date to disk in this frequency */
    @Value("${disk.write.frequency}")
    private int diskWriteFreq;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    public void startConvert(File[] targetFiles) {
        try {
            // Read files
            for (File file : targetFiles) {

                FlexFileReader fileReader = new FlexFileReader(file, fileReadSize);
                fileReader.openReader();
                // set date to dao
                outputDao.setTargetDate(fileReader.getDate());

                int counter = 0;
                while (!fileReader.isEnd()) {
                    // continue to read file
                    fileReader.readContinue();

                    // execute lines
                    logger.info("Exexute lines.");
                    String nextLine;
                    while ((nextLine = fileReader.pollNextLine()) != null) {
                        MessageBundle bundle = MessageConverter.extract(nextLine);
                        boardManager.executeMessageBundle(bundle);
                    }

                    // wWite date on disk, prevent to memory over in kdb
                    counter++;
                    if (counter % diskWriteFreq == 0) {
                        while (outputDao.isWriting()) {
                            SystemUtility.waitSleep(2000);
                        }
                        outputDao.wirteToDisk();
                    }
                }

                // Close Reader
                fileReader.closeReader();

                // Change Date
                boardManager.changeDate();
                // Write data on disk
                while (outputDao.isWriting()) {
                    SystemUtility.waitSleep(1000);
                }
                // wait. but maybe no problem with nowait.
                logger.info("Wait 10 secs before change date.");
                SystemUtility.waitSleep(10000);
                outputDao.wirteToDisk();

                // gc just in case...
                System.gc();
                System.gc();
            }

            logger.info("Wait 10 secs before finalize.");
            SystemUtility.waitSleep(10000);
            outputDao.finalizeData();

        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
