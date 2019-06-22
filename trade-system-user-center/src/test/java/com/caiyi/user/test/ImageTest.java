package com.caiyi.user.test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author GJ
 * @create 2017-12-06 14:22
 **/
public class ImageTest {
    public static void main(String[] args) {
        BufferedImage bi = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        Color color = g.getColor();
        System.out.println(color);
        g.fillRect(0, 0, 800, 600);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("方正黑体简体", Font.PLAIN, 30));
        g.setColor(Color.black);
        g.drawString("相见时难别亦难", 200, 50);
        g.setFont(new Font("微软雅黑", Font.BOLD, 30));
        g.setColor(Color.black);
        g.drawString("相见时难别亦难", 200, 85);
        g.setFont(new Font("方正大黑简体", Font.PLAIN, 30));
        g.setColor(Color.black);
        g.drawString("相见时难别亦难", 200, 120);
        g.dispose();
        bi.flush();
        //新名字
        String fname = "abc";
        try {
            ImageIO.write(bi, "jpg", new File("/" + fname + ".jpg"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } //将其保存在C:/imageSort/targetPIC
    }
}
