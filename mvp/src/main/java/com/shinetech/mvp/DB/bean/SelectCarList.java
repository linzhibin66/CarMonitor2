package com.shinetech.mvp.DB.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ljn on 2017-05-31.
 */

@Entity(indexes = {@Index(value = "id DESC", unique = true)})
public class SelectCarList {

    @Id
    private Long id;

    @NotNull
    private String platenumber;

    @NotNull
    private String UserName;

    /**
     * 描述，扩展用，(用于保持多种车辆选择方案，可根据描述获取对应的车辆选择列表)
     */
    private String description;

    @Generated(hash = 914090821)
    public SelectCarList(Long id, @NotNull String platenumber,
            @NotNull String UserName, String description) {
        this.id = id;
        this.platenumber = platenumber;
        this.UserName = UserName;
        this.description = description;
    }

    @Generated(hash = 226603701)
    public SelectCarList() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatenumber() {
        return this.platenumber;
    }

    public void setPlatenumber(String platenumber) {
        this.platenumber = platenumber;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
