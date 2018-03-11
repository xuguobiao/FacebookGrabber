/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fbgrabber.bean;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kido
 */
public class FbGroupInfo {

    private String groupName;
    private String groupId;
    private String uniqueName;

    private int totalCount;
    private List<FbGroupUserInfo> adminList = new ArrayList<>();
    private List<FbGroupUserInfo> memberList = new ArrayList<>();

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<FbGroupUserInfo> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<FbGroupUserInfo> adminList) {
        this.adminList = adminList;
    }

    public List<FbGroupUserInfo> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<FbGroupUserInfo> memberList) {
        this.memberList = memberList;
    }

    public List<FbGroupUserInfo> getAllUsers() {
        List<FbGroupUserInfo> userInfos = new ArrayList<>();
        userInfos.addAll(this.adminList);
        userInfos.addAll(this.memberList);
        return userInfos;
    }

    @Override
    public String toString() {
        return String.format("groupName=%s, uniqueName=%s, groupId=%s, totalCount=%s, admin.size=%s, member.size=%s",
                groupName, uniqueName, groupId, totalCount, adminList.size(), memberList.size());
    }
}
