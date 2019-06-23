package utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class formatUtilities {

    public static long dateToTimestampSeconds(int day, int month, int year){
        Calendar calendar = new GregorianCalendar(year, month, day);
        long timestamp = TimeUnit.MILLISECONDS.toSeconds(calendar.getTimeInMillis());
        return timestamp;
    }

    public static int[] timestampToDayMonthYear(long timestamp){
        int[] dmy = new int [3];
        long timestamp_ms = TimeUnit.SECONDS.toMillis(timestamp);
        Date date = new Date(timestamp_ms);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        dmy[0] = cal.get(Calendar.DAY_OF_MONTH);
        dmy[1] = cal.get(Calendar.MONTH);
        dmy[2] = cal.get(Calendar.YEAR);
        return dmy;
    }

    public static long getTimeMilliseconds(){
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime.getTime();
    }
}
