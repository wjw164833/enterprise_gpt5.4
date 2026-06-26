package com.invitation.web.controller;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.template.TemplateQueryDTO;
import com.invitation.model.dto.template.TemplateVO;
import com.invitation.service.template.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 模板控制器
 */
@Tag(name = "模板管理")
@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @Operation(summary = "分页查询模板列表")
    @GetMapping
    public R<PageResult<TemplateVO>> page(TemplateQueryDTO queryDTO) {
        return templateService.pageTemplate(queryDTO);
    }

    @Operation(summary = "获取模板详情")
    @GetMapping("/{id}")
    public R<TemplateVO> detail(@PathVariable Long id) {
        return templateService.getTemplateDetail(id);
    }

    @Operation(summary = "使用模板创建邀请函")
    @PostMapping("/{id}/use")
    public R<Long> useTemplate(@PathVariable Long id) {
        templateService.incrementUsageCount(id);
        return R.ok(id);
    }
}
