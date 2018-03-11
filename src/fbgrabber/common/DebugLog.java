/**
 * Created on 2012-9-7, DebugLog.java
 *
 * @Author: XuGuobiao
 * @Comment:
 */
package fbgrabber.common;


public class DebugLog {

    public static final boolean isDebugEnable = true;
    public static final String TAG = "FbGrabber";

    public static void log(String logString) {
        if (isDebugEnable) {
            System.out.println(logString);
        }
    }
    
    public static void log(String logString, Object... args) {
        if (isDebugEnable) {
            System.out.println(String.format(logString, args));
        }
    }
    
     public static void e(String logString) {
        if (isDebugEnable) {
            System.err.println(logString);
        }
    }

}
