package com.caiyi.lottery.tradesystem.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxy
 * @create 2018-03-30 17:58
 **/
@Data
public class TopicConfigBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String order;
    private String evid;
    private String src;
    private String link;
    private String linkUrl;
    private String newsrc;
    private String adsrc;
    private String adlink;
    private String iOSsrc;
    private String iOSsrc2X;
    private String iOSsrc3X;
    private String iOSlink;
    private String newadsrc;
    private String newadlink;
    private String newiOSsrc;
    private String newiOSlink;
    private String haveSpace;
    private String haveTitle;
    private String titleAdSrc;
    private String titleIOSsrc;
    private String titleIOSsrc2X;
    private String titleIOSsrc3X;
    private String titleHeightAD;
    private String titleHeightIOS;
    private String adImgHeight;
    private String adImgWidth;
    private String path;
}
