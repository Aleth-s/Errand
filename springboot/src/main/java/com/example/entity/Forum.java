package com.example.entity;

import java.math.BigDecimal;

public class Forum {
    /** ID */
    private Integer id;
    /** 用户id */

    private String title;
    private String content;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getOtherinfo() {
        return otherinfo;
    }

    public void setOtherinfo(Integer otherinfo) {
        this.otherinfo = otherinfo;
    }
    /** 金额 */
    /** 用户 */
    private Integer userId;
    /** 时间 */
    private String time;
    /** 类型 */
    private Integer otherinfo;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String role;


}

