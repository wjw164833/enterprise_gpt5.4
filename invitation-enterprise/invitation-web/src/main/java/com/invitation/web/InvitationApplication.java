package com.invitation.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 多端邀请函系统 - 企业级版本 启动类
 */
@SpringBootApplication(scanBasePackages = "com.invitation")
@EnableScheduling
public class InvitationApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvitationApplication.class, args);
    }
}
