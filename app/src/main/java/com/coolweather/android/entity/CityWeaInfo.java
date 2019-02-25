package com.coolweather.android.entity;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Create on 2019/01/28
 *
 * @author Four
 * @description
 */
@org.greenrobot.greendao.annotation.Entity
public class CityWeaInfo {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String cid;
    private String jsonString;
    @Generated(hash = 1490819659)
    public CityWeaInfo(Long id, String cid, String jsonString) {
        this.id = id;
        this.cid = cid;
        this.jsonString = jsonString;
    }
    @Generated(hash = 62415990)
    public CityWeaInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCid() {
        return this.cid;
    }
    public void setCid(String cid) {
        this.cid = cid;
    }
    public String getJsonString() {
        return this.jsonString;
    }
    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
}
