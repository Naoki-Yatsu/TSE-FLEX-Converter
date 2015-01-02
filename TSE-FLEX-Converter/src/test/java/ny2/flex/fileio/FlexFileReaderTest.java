package ny2.flex.fileio;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FlexFileReaderTest {

    private static final String SAMPLE_PATH = "test\\FullEquities_20141215.line.7203";

    private FlexFileReader instance;

    @Before
    public void setUp() throws Exception {
        instance = new FlexFileReader(new File(SAMPLE_PATH), 1000);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetDate() {
        assertEquals(LocalDate.of(2014, 12, 15), instance.getDate());
    }

}
