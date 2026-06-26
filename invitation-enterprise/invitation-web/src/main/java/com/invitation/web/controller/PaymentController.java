package com.invitation.web.controller;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.payment.PaymentCreateDTO;
import com.invitation.model.dto.payment.PaymentNotifyDTO;
import com.invitation.model.entity.Payment;
import com.invitation.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 支付控制器
 */
@Tag(name = "支付管理")
@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "创建支付订单")
    @PostMapping("/create")
    public R<Payment> createPayment(@RequestBody PaymentCreateDTO dto) {
        return paymentService.createPayment(dto);
    }

    @Operation(summary = "微信支付回调")
    @PostMapping("/notify/wechat")
    public String wechatNotify(@RequestBody String body,
                                HttpServletRequest request) {
        log.info("收到微信支付回调: {}", body);
        try {
            log.warn("微信支付SDK未配置，跳过签名验证");

            PaymentNotifyDTO notifyDTO = new PaymentNotifyDTO();
            notifyDTO.setOrderNo(extractOrderNo(body));
            notifyDTO.setTransactionId(extractTransactionId(body));
            paymentService.handleNotify(notifyDTO);
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
        }
    }

    @Operation(summary = "查询支付状态")
    @GetMapping("/status/{orderNo}")
    public R<Payment> queryStatus(@PathVariable String orderNo) {
        return paymentService.queryPaymentStatus(orderNo);
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/{orderNo}")
    public R<Void> refund(@PathVariable String orderNo, @RequestParam String reason) {
        return paymentService.refund(orderNo, reason);
    }

    @Operation(summary = "支付记录列表")
    @GetMapping("/records")
    public R<PageResult<Payment>> pageRecords(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "20") Integer size,
                                               @RequestParam(required = false) Integer status) {
        return paymentService.pagePayment(null, status, page, size);
    }

    private String extractOrderNo(String body) {
        try {
            int start = body.indexOf("<out_trade_no><![CDATA[");
            int end = body.indexOf("]]></out_trade_no>");
            if (start >= 0 && end >= 0) {
                return body.substring(start + "<out_trade_no><![CDATA[".length(), end);
            }
        } catch (Exception e) {
            log.error("解析订单号失败", e);
        }
        return "";
    }

    private String extractTransactionId(String body) {
        try {
            int start = body.indexOf("<transaction_id><![CDATA[");
            int end = body.indexOf("]]></transaction_id>");
            if (start >= 0 && end >= 0) {
                return body.substring(start + "<transaction_id><![CDATA[".length(), end);
            }
        } catch (Exception e) {
            log.error("解析交易号失败", e);
        }
        return "";
    }
}
