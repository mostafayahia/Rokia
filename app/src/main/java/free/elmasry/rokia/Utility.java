package free.elmasry.rokia;

import android.content.Context;

/**
 * Created by yahia on 9/24/16.
 */

public class Utility {

    /** convert time in milli to minutes and return in format "mm minutes" */
    static String formatTime(Context context, int timeInMilliSec) {

        // convert remaining time from milliseconds to minutes
        timeInMilliSec /= 1000 * 60;

        // adjust time string
        String formattedTime;
        switch ((int) timeInMilliSec) {
            case 10:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
                formattedTime = (int) timeInMilliSec + " " + context.getString(R.string.str_minutes);
                break;
            case 2:
                formattedTime = context.getString(R.string.str_two_minutes);
                break;
            case 1:
                formattedTime = context.getString(R.string.str_one_minute);
                break;
            case 0:
                formattedTime = context.getString(R.string.str_less_than_one_minute);
                break;
            default:
                formattedTime = (int) timeInMilliSec + " " + context.getString(R.string.str_minute);
                break;
        }

        return formattedTime;

    }

}
