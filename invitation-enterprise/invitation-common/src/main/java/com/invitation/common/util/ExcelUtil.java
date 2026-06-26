package com.invitation.common.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Excel导出工具类 - 基于EasyExcel
 */
@Slf4j
@Component
public class ExcelUtil {

    /**
     * 导出Excel到响应流
     * @param response HTTP响应
     * @param fileName 文件名
     * @param sheetName Sheet名
     * @param head 表头类
     * @param data 数据列表
     */
    public <T> void export(HttpServletResponse response, String fileName,
                           String sheetName, Class<T> head, List<T> data) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            EasyExcel.write(response.getOutputStream(), head)
                    .autoCloseStream(Boolean.FALSE)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (IOException e) {
            log.error("导出Excel失败: {}", e.getMessage(), e);
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    /**
     * 生成Excel字节数组
     */
    public <T> byte[] exportToBytes(String sheetName, Class<T> head, List<T> data) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            EasyExcel.write(baos, head)
                    .autoCloseStream(Boolean.TRUE)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName)
                    .doWrite(data);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("生成Excel字节数组失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成Excel失败", e);
        }
    }
}
