/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fbgrabber.model;

import fbgrabber.bean.FbGroupInfo;

/**
 *
 * @author Kido
 */
public interface FbGroupInfoCallback {
    boolean onProgress(FbGroupInfo groupInfo);
}
