package br.com.thiengo.webviewusersignup.extras;

import android.os.Build;

/**
 * Created by viniciusthiengo on 13/11/16.
 */

public class Util {
    public static boolean isPreKitKat(){
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        return currentapiVersion < Build.VERSION_CODES.KITKAT;
    }
}
