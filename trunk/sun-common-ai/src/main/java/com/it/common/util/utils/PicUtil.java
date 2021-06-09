package com.it.common.util.utils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;

public class PicUtil {










    /**
     * 保存网络图片到本地工具类
     *
     * @param url
     *            图片网址
     * @param file
     *            保存本地地址
     */
    public static void readInputStream(URL url,File file) throws Exception {
        // 打开链接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置请求方式为"GET"
        conn.setRequestMethod("GET");
        // 超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        // 通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        // 每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        // 使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        // 关闭输入流
        inStream.close();
        // 创建输出流
        FileOutputStream outStream2 = new FileOutputStream(file);
        // 写入数据
        outStream2.write(outStream.toByteArray());
        // 关闭输出流
        outStream2.close();
    }







    /**
     * 裁剪PNG图片工具类
     *
     * @param fromFile
     *            源文件
     * @param toFile
     *            裁剪后的文件
     */
    public static void resizePng(File fromFile, File toFile) throws Exception {
        String str = fromFile.toString().substring(fromFile.toString().lastIndexOf(".") + 1);
        BufferedImage bi2 = ImageIO.read(fromFile);
        int newWidth;
        int newHeight;
        double rate = 2;
        newWidth = (int) (((double) bi2.getWidth(null)) / rate);
        newHeight = (int) (((double) bi2.getHeight(null)) / rate);

        BufferedImage to = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = to.createGraphics();
        to = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight,
                Transparency.TRANSLUCENT);
        g2d.dispose();
        g2d = to.createGraphics();
        @SuppressWarnings("static-access")
        Image from = bi2.getScaledInstance(newWidth, newHeight, bi2.SCALE_AREA_AVERAGING);
        g2d.drawImage(from, 0, 0, null);
        g2d.dispose();
        ImageIO.write(to, "png", toFile);
    }
    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {
        resizePng(new File("C:/code/图片/efed5f58f6268f0c35cc728e2ab22ff.png"),new File("C:/code/图片/efed5f58f6268f0c35cc728e2ab22ff2.png"));
    }

}
