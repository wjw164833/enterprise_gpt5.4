package com.invitation.service.template;

import com.invitation.common.model.PageResult;
import com.invitation.common.model.R;
import com.invitation.model.dto.template.TemplateQueryDTO;
import com.invitation.model.dto.template.TemplateVO;

public interface TemplateService {
    PageResult<TemplateVO> list(TemplateQueryDTO query);
    TemplateVO getDetail(Long id);
    void incrementUsage(Long id);

    default R<PageResult<TemplateVO>> pageTemplate(TemplateQueryDTO query) {
        return R.ok(list(query));
    }

    default R<TemplateVO> getTemplateDetail(Long id) {
        return R.ok(getDetail(id));
    }

    default void incrementUsageCount(Long id) {
        incrementUsage(id);
    }
}
