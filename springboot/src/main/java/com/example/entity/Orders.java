package com.example.entity;

import java.util.List;

public class Orders {
    /** ID */
    private Integer id;
    /** 订单编号 */
    private String orderNo;
    /** 物品名称 */
    private String name;
    private String descr;
    /** 物品图片 */
    private String img;
    /** 物品类型 */
    private String type;
    /** 物品重量 */
    private Double weight;
    /** 小费 */
    private Double price;
    /** 发起人ID */
    private Integer userId;
    /** 接单人ID */
    private Integer acceptId;
    /** 创建时间 */
    private String time;
    /** 接单时间 */
    private String acceptTime;
    /** 订单状态 */
    private String status;
    /** 发起人联系ID */
    private Integer addressId;
    /** 到达处联系ID */
    private Integer targetId;
    /** 订单备注 */
    private String comment;
    // 下单人名称
    private String userName;
    // 接单人名称
    private String acceptName;

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    private Integer tid;

    public Certification getCertification() {
        return certification;
    }

    public void setCertification(Certification certification) {
        this.certification = certification;
    }

    private Certification certification;

    public List<Comment>  getCommentc() {
        return commentc;
    }

    public void setCommentc(List<Comment>  commentc) {
        this.commentc = commentc;
    }

    private List<Comment> commentc;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(Address targetAddress) {
        this.targetAddress = targetAddress;
    }

    private Address address;

    private Address targetAddress;

    public Integer getRange() {
        return range;
    }

    public void setRange(Integer range) {
        this.range = range;
    }

    private Integer range;

    public byte[] getWordcloudImage() {
        return wordcloudImage;
    }

    public void setWordcloudImage(byte[] wordcloudImage) {
        this.wordcloudImage = wordcloudImage;
    }

    private byte[] wordcloudImage;



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAcceptId() {
        return acceptId;
    }

    public void setAcceptId(Integer acceptId) {
        this.acceptId = acceptId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String longitude;

    public String latitude;
}
