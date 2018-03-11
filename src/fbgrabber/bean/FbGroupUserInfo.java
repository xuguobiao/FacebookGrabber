/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fbgrabber.bean;

/**
 *
 * @author Kido
 */
public class FbGroupUserInfo extends FbUserInfo {

    private int role;
    private String joinInfo;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getJoinInfo() {
        return joinInfo;
    }

    public void setJoinInfo(String joinInfo) {
        this.joinInfo = joinInfo;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + String.format("role=%s", role);
    }

}
