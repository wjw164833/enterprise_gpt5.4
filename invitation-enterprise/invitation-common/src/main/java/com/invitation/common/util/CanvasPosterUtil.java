package com.invitation.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Canvas海报渲染工具 - 基于Java 2D绘制邀请函分享海报
 */
@Slf4j
@Component
public class CanvasPosterUtil {

    /**
     * 生成邀请函分享海报
     * @param coverUrl 封面图URL
     * @param title 标题
     * @param date 日期
     * @param address 地址
     * @param qrcodeUrl 二维码URL
     * @param width 海报宽度
     * @param height 海报高度
     * @return 海报图片字节数组
     */
    public byte[] generatePoster(String coverUrl, String title, String date,
                                  String address, String qrcodeUrl,
                                  int width, int height) {
        try {
            BufferedImage poster = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = poster.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            // 绘制背景
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // 绘制封面图
            if (coverUrl != null && !coverUrl.isEmpty()) {
                try {
                    BufferedImage coverImg = ImageIO.read(new URL(coverUrl));
                    if (coverImg != null) {
                        int coverHeight = (int) (height * 0.6);
                        g2d.drawImage(coverImg.getScaledInstance(width, coverHeight, Image.SCALE_SMOOTH),
                                0, 0, null);
                    }
                } catch (Exception e) {
                    log.warn("封面图加载失败，使用默认背景: {}", e.getMessage());
                    g2d.setColor(new Color(240, 240, 240));
                    g2d.fillRect(0, 0, width, (int) (height * 0.6));
                }
            }

            // 绘制标题
            int contentY = (int) (height * 0.65);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 28));
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g2d.drawString(title, (width - titleWidth) / 2, contentY);

            // 绘制日期
            if (date != null && !date.isEmpty()) {
                contentY += 45;
                g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 18));
                g2d.setColor(new Color(102, 102, 102));
                fm = g2d.getFontMetrics();
                int dateWidth = fm.stringWidth(date);
                g2d.drawString(date, (width - dateWidth) / 2, contentY);
            }

            // 绘制地址
            if (address != null && !address.isEmpty()) {
                contentY += 35;
                g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
                g2d.setColor(new Color(153, 153, 153));
                fm = g2d.getFontMetrics();
                int addrWidth = fm.stringWidth(address);
                g2d.drawString(address, (width - addrWidth) / 2, contentY);
            }

            // 绘制二维码
            if (qrcodeUrl != null && !qrcodeUrl.isEmpty()) {
                try {
                    BufferedImage qrImg = ImageIO.read(new URL(qrcodeUrl));
                    if (qrImg != null) {
                        int qrSize = 100;
                        int qrX = width - qrSize - 30;
                        int qrY = height - qrSize - 30;
                        g2d.drawImage(qrImg.getScaledInstance(qrSize, qrSize, Image.SCALE_SMOOTH),
                                qrX, qrY, null);
                    }
                } catch (Exception e) {
                    log.warn("二维码加载失败: {}", e.getMessage());
                }
            }

            // 绘制底部提示文字
            g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            g2d.setColor(new Color(153, 153, 153));
            String tip = "长按识别二维码查看邀请函";
            fm = g2d.getFontMetrics();
            int tipWidth = fm.stringWidth(tip);
            g2d.drawString(tip, (width - tipWidth) / 2, height - 15);

            g2d.dispose();

            // 输出为PNG
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(poster, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("生成海报失败: {}", e.getMessage(), e);
            return null;
        }
    }
}
