package com.caiyi.lottery.tradesystem.util.push.bean;

import com.caiyi.lottery.tradesystem.util.push.IPush;
import com.caiyi.lottery.tradesystem.util.push.impl.GtPush;
import com.caiyi.lottery.tradesystem.util.push.impl.MiPush;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum PushChannel {
    GT("GT", GtPush.class), Mi("Mi", MiPush.class);
    private String key;//渠道标识
    private IPush push;//该渠道的推送实例
    private Logger logger = LoggerFactory.getLogger("pushChannel");

    PushChannel(String key, Class<? extends IPush> clazz) {
        this.key = key;
        try {
            this.push = clazz.newInstance();
        } catch (Exception e) {
            logger.error("PushChannel实例化失败,key:" + key, e);
        }
    }

    public String getKey() {
        return key;
    }

    public IPush getPush() {
        return push;
    }
}
