package Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sahebjot on 4/16/16.
 */
public class Helpers {

    public static SharedPreferences getSP(Context c, String tag) {
        return c.getSharedPreferences(tag, Context.MODE_PRIVATE);
    }
}
