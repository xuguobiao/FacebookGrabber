/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fbgrabber.model;

import fbgrabber.bean.FbUserInfo;
import fbgrabber.common.CommonUtils;
import fbgrabber.common.DebugLog;
import static fbgrabber.model.BaseModel.HTTP_GET;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Kido
 */
public class FbUserModel extends BaseModel {

    private Map<String, FbUserInfo> mUserCache = new HashMap<>();

    private FbUserModel() {
    }

    public static FbUserModel getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {

        private static final FbUserModel INSTANCE = new FbUserModel();
    }

    public void clearCache() {
        mUserCache.clear();
    }

    public FbUserInfo login(String email, String pass) throws Exception {
        Response initResponse = requestBody(Urls.LOGIN, HTTP_GET, null);

        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("email", email);
        loginParams.put("pass", pass);

        Response loginResponse = requestBody(Urls.LOGIN, HTTP_POST, loginParams);
        Document loginDoc = loginResponse.parse();

        DebugLog.log("startLoginInit->loginResponse.cookies()=" + loginResponse.cookies());
        DebugLog.log("startLoginInit->loginResponse.statusMessage()=" + loginResponse.statusMessage());
//        DebugLog.log("startLoginInit->loginDoc= " + loginDoc.toString());

        FbUserInfo userInfo = null;
        String userId = loginResponse.cookies().get("c_user");
        if (userId != null && userId.length() > 0) {
            userInfo = new FbUserInfo();
            userInfo.setId(userId);
        }
        return userInfo;
    }

    public FbUserInfo getUserInfo(String userId) throws Exception {
        if (mUserCache.containsKey(userId)) {
            return mUserCache.get(userId);
        }

        String url = String.format(Urls.USER_PROFILE, userId);
        Document doc = requestDocument(url, HTTP_GET, null);

        Elements userNameElements = doc.select("div[title=Facebook] div div");
        Elements genderElements = doc.select("div[title=Gender] div div");
        if (genderElements.isEmpty()) {
            genderElements = doc.select("div[title=性别] div div");
        }
        Elements hometownElements = doc.select("h4:contains(Home Town)");
        if (hometownElements.isEmpty()) {
            hometownElements = doc.select("h4:contains(家乡)");
        }
        Elements locationElements = doc.select("h4:contains(Current City)");
        if (locationElements.isEmpty()) {
            locationElements = doc.select("h4:contains(所在地)");
        }

        String userName = userNameElements.size() > 0 ? userNameElements.first().text().trim() : "";
        String gender = genderElements.size() > 0 ? genderElements.first().text().trim() : "";
        String hometown = hometownElements.size() > 0 ? hometownElements.first().firstElementSibling().text().trim() : "";
        String location = locationElements.size() > 0 ? locationElements.first().firstElementSibling().text().trim() : "";

        if (gender == null || "".equals(gender)) {
            //try other way to find gender
            try {
                gender = guessGender(userId);
            } catch (Exception e) {
            }
        }

        FbUserInfo userInfo = new FbUserInfo();
        userInfo.setId(userId);
        userInfo.setUserName(userName);
        userInfo.setGender(gender);
        userInfo.setHometown(hometown);
        userInfo.setLocation(location);

        DebugLog.log("userInfo->" + userInfo.toString());

        mUserCache.put(userName, userInfo);

        return userInfo;
    }

    public String guessGender(String userId) throws Exception {
        String gender = "";
        String url = String.format(Urls.USER_ABOUT_PC, userId);
        Document aboutDoc = requestDocument(url, HTTP_GET, null);
        String aboutDocString = aboutDoc.toString();

        //            Elements placeElements = aboutDoc.select("li[testid=nav_places]");
        List<String> placeTextList = CommonUtils.getMatchList(",\"Places", "Lived\",", aboutDocString);
        if (!placeTextList.isEmpty()) {
            String placeText = placeTextList.get(0).toLowerCase();
            gender = fetchGender(placeText);
            DebugLog.log("placeElements, gender=" + gender);
        }

        if (gender.equals("")) {
            //        Elements addFriendTextElements = aboutDoc.select("span[class=addFriendText]");
            List<String> addFriendTextList = CommonUtils.getMatchList("<spanclass=\"addFriendText\">", ",<", aboutDocString);
            if (!addFriendTextList.isEmpty()) {
                String addFriendText = addFriendTextList.get(0).toLowerCase();
                gender = fetchGender(addFriendText);
                DebugLog.log("addFriendTextElements->gender=" + gender);
            }
        }
        DebugLog.log("guessGender->gender=" + gender);
        return gender;
    }

    private String fetchGender(String text) {
        String gender = "";
        if (text.contains("she") || text.contains("her") || text.contains("she's")) {
            gender = "Female";
        } else if (text.contains("她")) {
            gender = "女";
        } else if (text.contains("he") || text.contains("him") || text.contains("he's")) {
            gender = "Male";
        } else if (text.contains("他")) {
            gender = "男";
        }
        DebugLog.log("fetchGender->text=%s, gender=%s", text, gender);
        return gender;
    }
}
