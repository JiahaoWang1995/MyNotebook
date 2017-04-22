package au.edu.anu.u5833429.mynotebook;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by lenovo on 2016/3/24.
 */
public class GetTimeTest {

    @Test
    public void testResult() throws Exception {
        String test = "";
        GetTime getTime = new GetTime();
        test = getTime.result();
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS");
        Date curDate = new Date();
        String str = format.format(curDate);
        assertEquals(test, str);
    }
}