package com.invitation.service.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.invitation.common.model.PageResult;
import com.invitation.model.dto.template.TemplateQueryDTO;
import com.invitation.model.dto.template.TemplateVO;
import com.invitation.model.entity.Template;
import com.invitation.model.mapper.TemplateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Override
    public PageResult<TemplateVO> list(TemplateQueryDTO query) {
        LambdaQueryWrapper<Template> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Template::getStatus, 1);
        if (query.getTemplateType() != null) {
            wrapper.eq(Template::getTemplateType, query.getTemplateType());
        }
        if (query.getStyle() != null && !query.getStyle().isEmpty()) {
            wrapper.eq(Template::getStyle, query.getStyle());
        }
        if (query.getIsFree() != null) {
            wrapper.eq(Template::getIsFree, query.getIsFree());
        }
        wrapper.orderByAsc(Template::getSortOrder).orderByDesc(Template::getUsageCount);

        IPage<Template> page = templateMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        List<TemplateVO> records = page.getRecords().stream().map(t -> {
            TemplateVO vo = new TemplateVO();
            BeanUtils.copyProperties(t, vo);
            return vo;
        }).collect(Collectors.toList());
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    public TemplateVO getDetail(Long id) {
        Template template = templateMapper.selectById(id);
        if (template == null) return null;
        TemplateVO vo = new TemplateVO();
        BeanUtils.copyProperties(template, vo);
        return vo;
    }

    @Override
    public void incrementUsage(Long id) {
        templateMapper.incrementUsageCount(id);
    }
}
