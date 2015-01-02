package ny2.flex.fileio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvFileWriter {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** file to read */
    private File file;

    /** writer */
    private BufferedWriter writer;

    // //////////////////////////////////////
    // Constructor
    // //////////////////////////////////////

    public CsvFileWriter(String filename, String path) throws IOException {
        if (!path.endsWith(File.pathSeparator)) {
            path = path + File.pathSeparator;
        }
        this.file = new File(path + filename);

        open();
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    /**
     * Open Writer
     * @throws IOException
     */
    public void open() throws IOException {
        logger.info("Open writer.");
        this.writer = new BufferedWriter(new FileWriter(file));
    }

    /**
     * Close Writer at last
     * @throws IOException
     */
    public void close() throws IOException {
        logger.info("Close writer.");
        writer.close();
    }

    public void write(List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }
}
