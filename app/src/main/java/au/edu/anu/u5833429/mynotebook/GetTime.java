package au.edu.anu.u5833429.mynotebook;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2016/3/24.
 */
public class GetTime {
    public void GetTime(){
    }
    public String result(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_SS");
        Date curDate = new Date();
        String str = format.format(curDate);
        return str;
    }
}
