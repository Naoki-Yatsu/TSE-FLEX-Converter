package ny2.flex.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class FlexFileReader {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    /** assume filename contains date of yyyyMMdd */
    private static final String REGEX_DATE = "[0-9]{8}";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** file to read */
    private File file;

    /** reader */
    private BufferedReader reader;

    /** file read line for one time */
    private int fileReadSize;

    /** queue to store read lines */
    private LinkedBlockingQueue<String> lineQueue;

    /** After end of reading set true */
    private boolean end = false;

    /** line count of read lines */
    private long counter = 0;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public FlexFileReader(File file, int fileReadSize) {
        logger.info("Create FlexFileReader. File = {}", file.getName());
        this.file = file;
        this.fileReadSize = fileReadSize;
        lineQueue = new LinkedBlockingQueue<>(fileReadSize + 100);
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * Open Reader
     *
     * @throws FileNotFoundException
     */
    public void openReader() throws FileNotFoundException {
        logger.info("Open Reader.");
        this.reader = new BufferedReader(new FileReader(file));
    }

    /** close reader for last */
    public void closeReader() throws IOException {
        logger.info("Close Reader.");
        reader.close();
    }

    public void readContinue() throws IOException {
        logger.info("Continue to read file. Start = " + counter);
        String line;
        while (true) {
            line = reader.readLine();
            counter++;
            if (line == null) {
                end = true;
                return;
            }

            // Add line to queue
            if (!line.isEmpty()) {
                try {
                    lineQueue.put(line);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // if queue size is over READ_LINE_COUNT, stop read.
            if (lineQueue.size() >= fileReadSize) {
                return;
            }
        }
    }

    /**
     * Get next line data from Queue
     *
     * @return
     */
    public String pollNextLine() {
        return lineQueue.poll();
    }

    /**
     * Get date from filename.
     * @return
     */
    public LocalDate getDate() {
        String fileName = file.getName();
        Pattern pattern = Pattern.compile(REGEX_DATE);
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            String dateStr = matcher.group();
            LocalDate localDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            return localDate;
        } else {
            logger.error("Cannot find date from filename. filename = {}", fileName);
            System.exit(-1);
        }
        return null;
    }
}
