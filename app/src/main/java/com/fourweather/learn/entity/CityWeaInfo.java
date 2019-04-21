package com.fourweather.learn.entity;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * Create on 2019/01/28
 *
 * @author Four
 * @description
 */
@org.greenrobot.greendao.annotation.Entity
public class CityWeaInfo extends LitePalSupport {
    @Id
    private Long pos;

    public Long getPos() {
        return pos;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public void setPos(Long pos) {
        this.pos = pos;
    }

    @Unique
    @Column(unique = true)
    private String cid;

    @Column(nullable = false)
    private String jsonString;
    @Generated(hash = 562409996)
    public CityWeaInfo(Long pos, String cid, String jsonString) {
        this.pos = pos;
        this.cid = cid;
        this.jsonString = jsonString;
    }

    @Generated(hash = 62415990)
    public CityWeaInfo() {
    }

}
