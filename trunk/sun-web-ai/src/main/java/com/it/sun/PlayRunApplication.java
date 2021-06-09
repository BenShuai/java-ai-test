package com.it.sun;

import com.it.common.util.GetService;
import com.it.sun.service.SysConfigService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 程序的入口
 */
@SpringBootApplication
@EnableScheduling		//启用定时器的注解
@EnableAsync			//开启程序异步调用功能
@ServletComponentScan   //请求参数忽略大小写
//@EnableDiscoveryClient  //支持服务发现
public class PlayRunApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlayRunApplication.class, args);

		//从数据库读取任务调度并启动
		try{Thread.sleep(10000L);}catch (Exception e){}//10秒后再启动Job
		SysConfigService sysConfigService = GetService.getBean(SysConfigService.class);//获得spring容器中的SysConfigService对象
		sysConfigService.startQuartzJob();//在这个方法中启动调度
	}
}
