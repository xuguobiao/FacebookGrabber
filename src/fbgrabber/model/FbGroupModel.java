/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fbgrabber.model;

import fbgrabber.bean.FbGroupInfo;
import fbgrabber.bean.FbGroupUserInfo;
import fbgrabber.common.CommonUtils;
import fbgrabber.common.Constants;
import fbgrabber.common.DebugLog;
import static fbgrabber.model.BaseModel.HTTP_GET;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 *
 * @author Kido
 */
public class FbGroupModel extends BaseModel {

    private Map<String, FbGroupInfo> mGroupCache = new HashMap<>();

    private FbGroupModel() {
    }

    public static FbGroupModel getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {

        private static final FbGroupModel INSTANCE = new FbGroupModel();
    }

    public void clearCache() {
        mGroupCache.clear();
    }

    public FbGroupInfo getGroupInfo(String groupUniqueName, int limit, FbGroupInfoCallback callback) throws Exception {
        if (limit <= 0) {
            limit = Integer.MAX_VALUE / 2;
        }
        String cacheKey = groupUniqueName + "_" + limit;
        if (mGroupCache.containsKey(cacheKey)) {
            return mGroupCache.get(cacheKey);
        }
        String url = String.format(Urls.GROUP_MEMBERS, groupUniqueName);
        Document doc = requestDocument(url, HTTP_GET, null);
        String groupId = CommonUtils.getMatchList("\"entity_id\":\"", "\"", doc.toString()).get(0);
        DebugLog.log("groupId->" + groupId);

        String groupName = doc.getElementById("seo_h1_tag").text().trim();
        DebugLog.log("groupName->" + groupName);

        Element groupsMemberBrowser = null;
        Elements codeElements = doc.getElementsByTag("code");
        groupsMemberBrowser = Jsoup.parse(codeElements.first().data(), "", Parser.xmlParser());
        DebugLog.log("groupsMemberBrowser->" + groupsMemberBrowser.toString());

//        for (Element e : codeElements) {
//            Element div = Jsoup.parse(e.data(), "", Parser.xmlParser());
//            if ("groupsMemberBrowser".equals(div.attr("id"))) {
//                groupsMemberBrowser = div;
//                break;
//            }
//        }
        String totalCountString = groupsMemberBrowser.select("div div div span").first().text().replaceAll(",", "");
        DebugLog.log("totalCountString->" + totalCountString);
        int totalCount = CommonUtils.parse2Int(totalCountString);

        FbGroupInfo groupInfo = new FbGroupInfo();
        groupInfo.setUniqueName(groupUniqueName);
        groupInfo.setGroupId(groupId);
        groupInfo.setGroupName(groupName);
        groupInfo.setTotalCount(totalCount);

        boolean allAdminSuccess = fetchUsers(-1, groupsMemberBrowser, Constants.GROUP_ROLE_ADMIN, groupInfo, callback);
        boolean allMemberSuccess = fetchUsers(limit, groupsMemberBrowser, Constants.GROUP_ROLE_GENERAL, groupInfo, callback);

        if (allAdminSuccess && allMemberSuccess) {
            mGroupCache.put(url, groupInfo);
        }

        DebugLog.log("groupInfo->" + groupInfo.toString());
        return groupInfo;
    }

    private boolean fetchUsers(int limit, Element groupsMemberBrowser, int role,
            FbGroupInfo groupInfo, FbGroupInfoCallback callback) throws Exception {
        boolean allSuccess = true;
        List<FbGroupUserInfo> targetList = role == Constants.GROUP_ROLE_ADMIN
                ? groupInfo.getAdminList() : groupInfo.getMemberList();
        List<FbGroupUserInfo> partList = getMembers(groupsMemberBrowser, role);
        targetList.addAll(partList);
        String moreItemUrl = getMoreItemUrl(groupsMemberBrowser, role);

        while (moreItemUrl != null && !moreItemUrl.equals("") && targetList.size() < limit) {
            if (callback != null) {
                boolean canContinue = callback.onProgress(groupInfo);
                if (!canContinue) {
                    throw new Exception("User attempts to stop the task.");
                }
            }
            String ajaxUrl = Urls.MAIN_HOME_PC + moreItemUrl + "&__a=1";
            String ajaxString = requestBodyString(ajaxUrl, HTTP_GET, null);
            DebugLog.log("ajaxString->" + ajaxString);

            try {
                ajaxString = ajaxString.substring(ajaxString.indexOf("{"));

                ObjectMapper m = new ObjectMapper();
                JsonNode rootNode = m.readValue(ajaxString, JsonNode.class);
                String html = rootNode.get("domops").get(0).get(3).get("__html").getValueAsText();
                Element groupMembersElement = Jsoup.parse(html, "", Parser.xmlParser());

                partList = getMembers(groupMembersElement, role);
                targetList.addAll(partList);
                DebugLog.log("partList.size()->" + partList.size());

                moreItemUrl = getMoreItemUrl(groupMembersElement, role);
                int reqSize = CommonUtils.constait(limit, Constants.GROUP_REQ_SIZE_MIN, Constants.GROUP_REQ_SIZE_MAX);
                int lackSize = limit - targetList.size();
                if (lackSize <= reqSize) {
                    reqSize = lackSize;
                }
                moreItemUrl = CommonUtils.replaceUrlParam(moreItemUrl, "limit", reqSize + "");
            } catch (Exception e) {
                allSuccess = false;
                e.printStackTrace();
                moreItemUrl = null;
            }
        }

        return allSuccess;
    }

    // recently_joined_  admins_moderators_
    private List<FbGroupUserInfo> getMembers(Element groupsMembersElement, int role) {
        String prefix = role == Constants.GROUP_ROLE_ADMIN ? "admins_moderators_" : "recently_joined_";
        List<FbGroupUserInfo> userInfoList = new ArrayList<>();
        Elements memberElements = groupsMembersElement.select(String.format("div[id^=%s]", prefix));
        if (memberElements != null && memberElements.size() > 0) {
            for (Element e : memberElements) {
                String id = e.attr("id").replace(prefix, "");
                String nickName = e.select("a img").first().attr("aria-label");
                String userName = e.select("a").first().attr("href").split("\\?")[0];
                userName = userName.substring(userName.lastIndexOf("/"));
                Elements joinElements = e.getElementsByClass("_60rj");
                String joinDate = joinElements.size() > 0 && role == Constants.GROUP_ROLE_GENERAL
                        ? joinElements.first().text().trim() : "";
                FbGroupUserInfo userInfo = new FbGroupUserInfo();
                userInfo.setId(id);
                userInfo.setNickName(nickName);
                userInfo.setUserName(userName);
                userInfo.setJoinInfo(joinDate);
                userInfo.setRole(role);
                userInfoList.add(userInfo);
                DebugLog.log("userInfo->" + userInfo.toString());
            }
        }
        return userInfoList;
    }

    // group_confirmed_members  group_admins_moderators
    private String getMoreItemUrl(Element groupsMembersElement, int role) {
        String prefix = role == Constants.GROUP_ROLE_ADMIN ? "group_admins_moderators" : "group_confirmed_members";
        String moreItemUrl = "";
        try {
            moreItemUrl = groupsMembersElement.select(String.format("a[href^=/ajax/browser/list/%s/]", prefix))
                    .first().attr("href");
        } catch (Exception e) {
        }
        DebugLog.log("moreItemUrl->" + moreItemUrl);

        return moreItemUrl;
    }

    public String getGroupUniqueName(String groupName) throws Exception {
        String url = String.format(Urls.GROUP_SEARCH, URLEncoder.encode(groupName, "UTF-8"));
        Document doc = requestDocument(url, HTTP_GET, null);
        DebugLog.log("doc->" + doc.toString());
        String href = doc.select("div a[href^=/groups/]").first().attr("href");
        String uniqueName = CommonUtils.getMatchList("/groups/", "/", href).get(0);
        return uniqueName;
    }

}
