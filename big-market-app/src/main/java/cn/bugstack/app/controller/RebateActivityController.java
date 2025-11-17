package cn.bugstack.app.controller;

import cn.bugstack.domain.activity.repository.IActivityRepository;
import cn.bugstack.domain.rebate.event.SendRebateMessageEvent;
import cn.bugstack.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.bugstack.domain.rebate.model.entity.BehaviorEntity;
import cn.bugstack.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.bugstack.domain.rebate.repository.IBehaviorRebateRepository;
import cn.bugstack.domain.rebate.service.BehaviorRebateService;

import cn.bugstack.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 返利活动控制层
 * @create 2024-04-30 16:14
 */
@Slf4j
@RestController
@RequestMapping("/api/activity")
public class RebateActivityController {

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private IBehaviorRebateRepository behaviorRebateRepository;

    @Resource
    private BehaviorRebateService behaviorRebateService;

    @Resource
    private IActivityRepository activityRepository;

    /**
     * 日历签到返利接口
     * @param request 请求参数
     * @return 响应结果
     */
    @PostMapping("/calendar_sign_rebate")
    public String calendarSignRebate(@RequestBody CalendarSignRebateRequest request) {
        try {
            log.info("日历签到返利请求 userId: {}", request.getUserId());
            
            // 1. 创建BehaviorEntity实体对象
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            BehaviorEntity behaviorEntity = BehaviorEntity.builder()
                    .userId(request.getUserId())
                    .behaviorTypeVO(BehaviorTypeVO.SIGN)
                    .outBusinessNo(today) // 签到的业务ID是日期字符串
                    .build();
            
            // 2. 限制一个用户一天只能签到一次
            boolean hasSigned = behaviorRebateRepository.queryHasUserRebateRecord(request.getUserId(), BehaviorTypeVO.SIGN, today);
            if (hasSigned) {
                log.error("用户已签到 userId: {} date: {}", request.getUserId(), today);
                return "用户已签到";
            }
            
            // 3. 调用行为返利服务创建返利订单
            List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);
            if (orderIds.isEmpty()) {
                log.error("没有找到签到返利配置 userId: {}", request.getUserId());
                return "签到失败";
            }
            
            // 注意：BehaviorRebateService的createOrder方法已经在内部处理了MQ消息的发送
            // 这里不需要再单独发送MQ消息
            
            log.info("日历签到返利完成 userId: {} orderIds: {}", request.getUserId(), orderIds);
            
            return "签到成功";
        } catch (Exception e) {
            log.error("日历签到返利失败 userId: {}", request.getUserId(), e);
            return "签到失败";
        }
    }

    // 内部请求类
    private static class CalendarSignRebateRequest {
        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

}