package fbgrabber.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

public class CommonUtils {

    public static Map<String, String> toMap(String url) {
        Map<String, String> map = null;
        if (url != null && url.indexOf("&") > -1 && url.indexOf("=") > -1) {
            map = new HashMap<String, String>();
            String[] arrTemp = url.split("&");
            for (String str : arrTemp) {
                String[] qs = str.split("=");
                map.put(qs[0], qs[1]);
            }
        }
        return map;
    }

    public static String getQueryString(String url, String name) {
        return toMap(url).get(name);
    }

    /**
     * remove all the space
     *
     * @param content the whole html-text
     * @return html-text string without any space
     */
    public static String minifyText(String content) {
        return content.replaceAll("\\s+", "");
    }

    /**
     * find the matchStrings that starts with prefix and end with suffix,then
     * put in a list
     *
     * @param prefix prefix of the matchString
     * @param suffix suffix of the matchString
     * @param wholeText html-text without any space
     * @return list of the match-contents
     */
    public static ArrayList<String> getMatchList(String prefix, String suffix, String wholeText) {
        wholeText = minifyText(wholeText);
        ArrayList<String> matchList = new ArrayList<String>();
        try {
            Pattern p = Pattern.compile(prefix + ".*?" + suffix);
            Matcher m = p.matcher(wholeText);
            while (m.find()) {
                String curMatchString = m.group();
                String gotString = curMatchString.replaceAll(prefix + "|" + suffix, "");
                gotString = gotString.replace("<br/>", " ");
                matchList.add(gotString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matchList;
    }

    /**
     * find the matchStrings that starts with prefix and end with suffix,then
     * put in a singl-line string
     *
     * @param prefix prefix of the matchString
     * @param suffix suffix of the matchString
     * @param wholeText html-text without any space
     * @return singl-line of the match-contents
     */
    public static String getMatchListString(String prefix, String suffix, String wholeText) {
        return list2SingleLine(getMatchList(prefix, suffix, wholeText));
    }

    /**
     * convert list to mutipleline-string
     *
     * @param list
     * @return mutipleline-string
     */
    public static String list2MutiLine(List<String> list) {
        String listString = "";
        for (int i = 0; i < list.size(); i++) {
            listString += list.get(i);
            if (i < list.size() - 1) {
                listString += "\r\n";
            }
        }
        return listString;
    }

    /**
     * convert list to single line splited by ';'
     *
     * @param list
     * @return
     */
    public static String list2SingleLine(List<String> list) {
        String listString = "";
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                listString += ";";
            }
            listString += list.get(i);
        }
        return listString;
    }

    public static String makeUrl(String hostUrl, HashMap<String, Object> params) {
        StringBuilder url = new StringBuilder(hostUrl);
        if (url.indexOf("?") < 0) {
            url.append('?');
        }

        for (String name : params.keySet()) {
            url.append('&');
            url.append(name);
            url.append('=');

            String value = String.valueOf(params.get(name));
            try {
                value = URLEncoder.encode(String.valueOf(params.get(name)), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            url.append(value);
        }

        return url.toString().replace("?&", "?");
    }

    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static String getUnicode(String s) {
        try {
            StringBuffer out = new StringBuffer("");
            byte[] bytes = s.getBytes("unicode");
            for (int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);

            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * digitString.replaceAll("[^0-9]", "")
     *
     * @param digitString
     * @return
     */
    public static long parse2Long(String digitString) {
        try {
            return Long.parseLong(digitString.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static int parse2Int(String digitString) {
        try {
            return Integer.parseInt(digitString.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static double parse2Double(String digitString) {
        try {
            return Double.parseDouble(digitString.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * ************************ get info ******************************
     */
    public static String getIpHost() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (Exception e) {
        }
        return "";
    }

    public static String getLocalMac() {
        // TODO Auto-generated method stub

        StringBuffer sb = new StringBuffer("");
        try {
            InetAddress ia = InetAddress.getLocalHost();
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                //字节转换为整数
                int temp = mac[i] & 0xff;
                String str = Integer.toHexString(temp);
                if (str.length() == 1) {
                    sb.append("0" + str);
                } else {
                    sb.append(str);
                }
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    public static String getSystemProperty() {
        Properties props = System.getProperties(); //获得系统属性集  
        String osName = props.getProperty("os.name"); //操作系统名称  
        String osArch = props.getProperty("os.arch"); //操作系统构架  
        String osVersion = props.getProperty("os.version"); //操作系统版本  

        return osName + "|" + osArch + "|" + osVersion;
    }

    public static String map2String(Map<String, String> map) {
        String mapString = "{";
        for (Object key : map.keySet()) {
            mapString += key + ": " + map.get(key) + "; ";
        }
        mapString += "}";
        return mapString;
    }

    // date methods
    public static final String TIMEFORMAT_STRING0 = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMEFORMAT_STRING1 = "yyyyMMddHHmm";
    public static final String TIMEFORMAT_STRING2 = "HH:mm:ss";
    public static final String TIMEFORMAT_STRING3 = "yyyy-MM-dd";
    public static final String TIMEFORMAT_STRING4 = "HH:mm";
    public static final String TIMEFORMAT_STRING5 = "yyMMddHHmmss";

    public static final String[] TIMEFORMAT_STRINGS = {TIMEFORMAT_STRING0, TIMEFORMAT_STRING1, TIMEFORMAT_STRING2, TIMEFORMAT_STRING3, TIMEFORMAT_STRING4};

    /**
     *
     * @param format 0: yyyy-MM-dd HH:mm:ss, 1: yyyyMMddHHmm, 2: HH:mm:ss, 3:
     * yyyy-MM-dd 4: HH:mm
     * @return
     */
    public static String getCurrentFormatTime(int format) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIMEFORMAT_STRINGS[format]);
        Date date = new Date(System.currentTimeMillis());
        return timeFormat.format(date);
    }

    public static String getCurrentFormatTime(String format) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return timeFormat.format(date);
    }

    public static boolean openBrowser(String urlString) {
        //判断当前系统是否支持Java AWT Desktop扩展
        boolean success = false;
        if (java.awt.Desktop.isDesktopSupported()) {
            try {
                //创建一个URI实例
                java.net.URI uri = java.net.URI.create(urlString);
                //获取当前系统桌面扩展
                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                //判断系统桌面是否支持要执行的功能
                if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    //获取系统默认浏览器打开链接
                    dp.browse(uri);
                    success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public static boolean openFile(File file) {
        //判断当前系统是否支持Java AWT Desktop扩展
        boolean success = false;
        if (java.awt.Desktop.isDesktopSupported()) {
            try {
                //获取当前系统桌面扩展
                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                dp.open(file);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public static void writeExcel(String[] headers, List<String[]> data, File file) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(file.getName());

        HSSFRow hssfRow = sheet.createRow(0);
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);

        HSSFCell headCell = null;

        for (int i = 0; i < headers.length; i++) {
            headCell = hssfRow.createCell(i);
            headCell.setCellValue(headers[i]);
            headCell.setCellStyle(cellStyle);
        }

        for (int i = 0; i < data.size(); i++) {
            hssfRow = sheet.createRow(i + 1);
            String[] datas = data.get(i);
            for (int j = 0; j < datas.length; j++) {
                headCell = hssfRow.createCell(j);
                headCell.setCellValue(datas[j]);
                headCell.setCellStyle(cellStyle);
            }
        }

        // 保存Excel文件
        OutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
    }

    public static int constait(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }

    public static String replaceUrlParam(String url, String key, String value) {
        if (url != null) {
            url = url.replaceAll(String.format("\\b%s=.*?(&|$)", key), String.format("%s=%s$1", key, value));
        }
        return url;
    }

}
