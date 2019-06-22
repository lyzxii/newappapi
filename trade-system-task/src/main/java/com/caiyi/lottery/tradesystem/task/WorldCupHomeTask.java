package com.caiyi.lottery.tradesystem.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.caiyi.lottery.tradesystem.service.JCTopicHomeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 竞彩世界杯首页文件生成
 * 
 * @author ls
 * @2018年3月30日
 */
@Component
@Slf4j
public class WorldCupHomeTask {

	@Autowired
	private JCTopicHomeService jCTopicHomeService ;
	
	// @Scheduled(fixedRate =60000 ,initialDelay=12000)
	@Scheduled(fixedRate =5 * 60 * 1000 ,initialDelay=60 * 1000)
	public void generatorHomeTask() {
		log.info("开始生成竞彩世界杯页面文件");
		jCTopicHomeService.generatorHomeTask();
		log.info("结束生成竞彩世界杯页面文件");
	}

}
