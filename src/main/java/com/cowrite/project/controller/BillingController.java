package com.cowrite.project.controller;

import com.cowrite.project.common.ApiResponse;
import com.cowrite.project.model.entity.BillingInvoice;
import com.cowrite.project.model.entity.BillingWebhook;
import com.cowrite.project.model.entity.BillingAlert;
import com.cowrite.project.service.BillingService;
import com.cowrite.project.service.WebhookService;
import com.cowrite.project.service.AlertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 计费相关Controller
 *
 * @author heathcetide
 */
@Api(tags = "计费管理")
@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private AlertService alertService;

    @ApiOperation("生成月度账单")
    @PostMapping("/invoice/monthly")
    public ApiResponse<Long> generateMonthlyInvoice(@RequestParam Long userId,
                                                   @RequestParam int year,
                                                   @RequestParam int month) {
        try {
            Long invoiceId = billingService.generateMonthlyInvoice(userId, year, month);
            return ApiResponse.success(invoiceId);
        } catch (Exception e) {
            return ApiResponse.error("生成月度账单失败: " + e.getMessage());
        }
    }

    @ApiOperation("生成年度账单")
    @PostMapping("/invoice/yearly")
    public ApiResponse<Long> generateYearlyInvoice(@RequestParam Long userId,
                                                  @RequestParam int year) {
        try {
            Long invoiceId = billingService.generateYearlyInvoice(userId, year);
            return ApiResponse.success(invoiceId);
        } catch (Exception e) {
            return ApiResponse.error("生成年度账单失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取用户账单列表")
    @GetMapping("/invoice/user/{userId}")
    public ApiResponse<List<BillingInvoice>> getUserInvoices(@PathVariable Long userId,
                                                            @RequestParam(required = false) String status) {
        try {
            List<BillingInvoice> invoices = billingService.getUserInvoices(userId, status);
            return ApiResponse.success(invoices);
        } catch (Exception e) {
            return ApiResponse.error("获取账单列表失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取账单详情")
    @GetMapping("/invoice/{invoiceId}")
    public ApiResponse<BillingInvoice> getInvoiceDetail(@PathVariable Long invoiceId) {
        try {
            BillingInvoice invoice = billingService.getInvoiceDetail(invoiceId);
            if (invoice == null) {
                return ApiResponse.error("账单不存在");
            }
            return ApiResponse.success(invoice);
        } catch (Exception e) {
            return ApiResponse.error("获取账单详情失败: " + e.getMessage());
        }
    }

    @ApiOperation("支付账单")
    @PostMapping("/invoice/{invoiceId}/pay")
    public ApiResponse<Boolean> payInvoice(@PathVariable Long invoiceId,
                                          @RequestParam BigDecimal amount) {
        try {
            boolean success = billingService.payInvoice(invoiceId, amount);
            if (success) {
                return ApiResponse.success(true);
            } else {
                return ApiResponse.error("支付失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("支付失败: " + e.getMessage());
        }
    }

    @ApiOperation("创建Webhook配置")
    @PostMapping("/webhook")
    public ApiResponse<Long> createWebhook(@RequestBody BillingWebhook webhook) {
        try {
            Long webhookId = webhookService.createWebhookConfig(webhook);
            return ApiResponse.success(webhookId);
        } catch (Exception e) {
            return ApiResponse.error("创建Webhook配置失败: " + e.getMessage());
        }
    }

    @ApiOperation("更新Webhook配置")
    @PutMapping("/webhook/{webhookId}")
    public ApiResponse<Boolean> updateWebhook(@PathVariable Long webhookId,
                                             @RequestBody BillingWebhook webhook) {
        try {
            webhook.setId(webhookId);
            boolean success = webhookService.updateWebhookConfig(webhook);
            return ApiResponse.success(success);
        } catch (Exception e) {
            return ApiResponse.error("更新Webhook配置失败: " + e.getMessage());
        }
    }

    @ApiOperation("删除Webhook配置")
    @DeleteMapping("/webhook/{webhookId}")
    public ApiResponse<Boolean> deleteWebhook(@PathVariable Long webhookId) {
        try {
            boolean success = webhookService.deleteWebhookConfig(webhookId);
            return ApiResponse.success(success);
        } catch (Exception e) {
            return ApiResponse.error("删除Webhook配置失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取Webhook配置列表")
    @GetMapping("/webhook")
    public ApiResponse<List<BillingWebhook>> getWebhooks(@RequestParam(required = false) Long organizationId) {
        try {
            List<BillingWebhook> webhooks = webhookService.getWebhookConfigs(organizationId);
            return ApiResponse.success(webhooks);
        } catch (Exception e) {
            return ApiResponse.error("获取Webhook配置失败: " + e.getMessage());
        }
    }

    @ApiOperation("创建预警配置")
    @PostMapping("/alert")
    public ApiResponse<Long> createAlert(@RequestBody BillingAlert alert) {
        try {
            Long alertId = alertService.createAlert(alert);
            return ApiResponse.success(alertId);
        } catch (Exception e) {
            return ApiResponse.error("创建预警配置失败: " + e.getMessage());
        }
    }

    @ApiOperation("更新预警配置")
    @PutMapping("/alert/{alertId}")
    public ApiResponse<Boolean> updateAlert(@PathVariable Long alertId,
                                           @RequestBody BillingAlert alert) {
        try {
            alert.setId(alertId);
            boolean success = alertService.updateAlert(alert);
            return ApiResponse.success(success);
        } catch (Exception e) {
            return ApiResponse.error("更新预警配置失败: " + e.getMessage());
        }
    }

    @ApiOperation("删除预警配置")
    @DeleteMapping("/alert/{alertId}")
    public ApiResponse<Boolean> deleteAlert(@PathVariable Long alertId) {
        try {
            boolean success = alertService.deleteAlert(alertId);
            return ApiResponse.success(success);
        } catch (Exception e) {
            return ApiResponse.error("删除预警配置失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取用户预警配置")
    @GetMapping("/alert/user/{userId}")
    public ApiResponse<List<BillingAlert>> getUserAlerts(@PathVariable Long userId) {
        try {
            List<BillingAlert> alerts = alertService.getUserAlerts(userId);
            return ApiResponse.success(alerts);
        } catch (Exception e) {
            return ApiResponse.error("获取预警配置失败: " + e.getMessage());
        }
    }
}
