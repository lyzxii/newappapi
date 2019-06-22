package com.caiyi.lottery.tradesystem.task;

import com.caiyi.lottery.tradesystem.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 专题首页任务
 * @author wxy
 * @create 2018-03-28 17:02
 **/
@Slf4j
@Component
public class TopicTask {
    @Autowired
    private TopicService topicService;
    // @Scheduled(cron="0/1 * *  * * ? ")
    @Scheduled(fixedRate =5 * 60 * 1000 ,initialDelay=61 * 1000)
    public void topicHomePageTask() {
        try {
            log.info("专题首页任务开始");
            topicService.createTopicHomePage();
            log.info("专题首页任务结束");
        } catch (Exception e) {
            log.error("专题首页任务失败", e);
        }

    }
}
