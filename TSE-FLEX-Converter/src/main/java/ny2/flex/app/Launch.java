package ny2.flex.app;

import java.io.File;
import java.util.Arrays;

import ny2.flex.common.SystemUtility;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launch {

    // //////////////////////////////////////
    // Field
    // //////////////////////////////////////

    private static final String SPRING_CONFIG_FILE = "classpath:applicationContext.xml";

    private static final Logger logger = LoggerFactory.getLogger(Launch.class);

    @Option(name = "-f", aliases = "--file", usage = "file path")
    private String filepath;

    @Option(name = "-d", aliases = "--dir", usage = "directory path for loading all files")
    private String dirpath;

    @Option(name = "-h", aliases = "--help", usage = "show usage message and exit")
    private boolean usageFlag;

    private File[] targetFiles;

    // //////////////////////////////////////
    // Main
    // //////////////////////////////////////

    public static void main(String[] args) {
        logger.info("START Application.");

        // parse args
        Launch launch = new Launch();
        launch.parseArgs(args);

        // execute
        AbstractApplicationContext context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE);
        FlexConverter flexConverter = context.getBean(FlexConverter.class);
        flexConverter.startConvert(launch.targetFiles);

        context.close();
        logger.info("END Application.");

        SystemUtility.waitSleep(5000);
        System.exit(0);
    }

    // //////////////////////////////////////
    // Method
    // //////////////////////////////////////

    private void parseArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(-1);
        }

        if (usageFlag) {
            showUsageAndExit(parser);
        }

        // Check options
        if ((filepath == null && dirpath == null) || filepath != null && dirpath != null) {
            logger.error("Error: Please set filepath \"or\" dirpath");
            showUsageAndExit(parser);
        }

        // check path
        if (filepath != null) {
            File file = new File(filepath);
            if (!file.exists() || !file.isFile()) {
                logger.error("File is not exist. path = {}", filepath);
                System.exit(-1);
            } else {
                targetFiles = new File[] { file };
            }

        } else if (dirpath != null) {
            File dir = new File(dirpath);
            if (!dir.exists() || !dir.isDirectory()) {
                logger.error("Directory is not exist. path = {}", dirpath);
                System.exit(-1);
            } else {
                targetFiles = dir.listFiles();
                Arrays.sort(targetFiles, (file1, file2) -> file1.getName().compareTo(file2.getName()));
            }
        }

        Arrays.stream(targetFiles).forEach(file -> logger.info("Target File = {}", file.getName()));
    }

    private void showUsageAndExit(CmdLineParser parser) {
        System.out.println("Usage:");
        System.out.println(" Launch -f file");
        System.out.println(" Launch -d dir");
        System.out.println(" Launch -h");
        System.out.println();
        System.out.println("Options:");
        parser.printUsage(System.out);
        System.exit(0);
    }
}
