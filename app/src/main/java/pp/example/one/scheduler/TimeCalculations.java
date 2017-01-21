package pp.example.one.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by payalkothari on 1/13/17.
 */
public class TimeCalculations {


    public static String calculateTotal(String formattedDateStart, String formattedDateEnd) {
        StringBuilder strb = new StringBuilder();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(formattedDateStart);
            d2 = format.parse(formattedDateEnd);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");

//            String diffMinutesStr = "";
//            if(diffMinutes <10){
//                diffMinutesStr = "0" + String.valueOf(diffMinutes);
//            }

            strb.append(String.valueOf(diffHours)).append(":").append(diffMinutes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strb.toString();
    }

    public static String convertAmPmToHHmmssTimeFormat(String resultS) {

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat1 = new SimpleDateFormat("hh:mm a");
        Date tempDate1 = new Date();
        String spaceAddedStartTime = resultS.substring(0, 5) + " " + resultS.substring(5, resultS.length());
        try {
            tempDate1 = parseFormat1.parse(spaceAddedStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return format.format(tempDate1);
    }

    public static String forwardTimeByGivenHour(String timeToForward, int byHours, int byMinutes){

        SimpleDateFormat parseFormat1 = new SimpleDateFormat("hh:mm a");
        Date tempDate1 = new Date();
        String timeWithSpace = timeToForward.substring(0, 5) + " " + timeToForward.substring(5, timeToForward.length());
        try {
            tempDate1 = parseFormat1.parse(timeWithSpace);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tempDate1);
        calendar.add(Calendar.HOUR, byHours);
        calendar.add(Calendar.MINUTE, byMinutes);
        String forwardedTimeWithSpace = parseFormat1.format(calendar.getTime());

        String forwardedTimeWithoutSpace = forwardedTimeWithSpace.replace(" ", "");

        return forwardedTimeWithoutSpace;
    }

}
