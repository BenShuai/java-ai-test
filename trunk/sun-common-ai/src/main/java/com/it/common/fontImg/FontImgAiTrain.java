package com.it.common.fontImg;

import com.alibaba.fastjson.JSONObject;
import com.it.common.util.BaseUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

/**
 * 文字图像识别
 *
 * 对特征进行提取并保存到文件
 *
 * 训练器
 */
public class FontImgAiTrain {

    /**
     * 创建文字图片
     */
    public static void CreFontImg(){
        long startTime = System.currentTimeMillis();
        BaseUtil.mkdirToPath(Configs.creImgBasePath);//根目录不存在就创建
        //汉字的 Unicode 编码范围   https://www.qqxiuzi.cn/zh/hanzi-unicode-bianma.php
        String start="4e00";//定义一个字符串变量为4e00
        String end="9fa5";//定义一个字符串变量为9fa5
        int s=Integer.parseInt(start, 16);//将16进制字符start转换为10进制整数
        int e=Integer.parseInt(end, 16);//将16进制字符end转换为10进制整数
        for (int i=s;i<=e;i++){//for循环实现汉字的输出
            String str=(char)i+ "";//类型转换
            System.out.println(str);
            try{
                //得到图片缓冲区
                BufferedImage bi = new BufferedImage(Configs.imgSize, Configs.imgSize, BufferedImage.TYPE_INT_RGB);
                //得到它的绘制环境(这张图片的笔)
                Graphics2D g2 = (Graphics2D) bi.getGraphics();
                //白色绘制全图
                g2.setColor(Color.WHITE);
                g2.fillRect(0,0,Configs.imgSize,Configs.imgSize);//填充整张图片(其实就是设置背景颜色)

                g2.setFont(new Font("宋体",Font.BOLD,Configs.fontSize)); //设置字体:字体、字号、大小
                g2.setColor(Color.BLACK);//设置背景颜色
                g2.drawString(str,2,16); //向图片上写字符串
                ImageIO.write(bi,"JPEG",new FileOutputStream(Configs.creImgBasePath+str+".jpg"));//保存图片 JPEG表示保存格式
                g2.dispose();
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
        System.out.println("一共："+(e-s)+" 个汉字");
        long stopTime = System.currentTimeMillis();
        System.out.println("CreFontImg方法共计用时："+(stopTime-startTime)/1000+" 秒");
    }

    /**
     * 文字笔画特征提取的方法[类卷积神经网络]
     */
    public static void CnnImgToFeatureExtraction(){
        long startTime = System.currentTimeMillis();
        BaseUtil.mkdirToPath(Configs.creClassifyFilePath);//根目录不存在就创建
        //汉字的 Unicode 编码范围   https://www.qqxiuzi.cn/zh/hanzi-unicode-bianma.php
        String start="4e00";//定义一个字符串变量为4e00
        String end="9fa5";//定义一个字符串变量为9fa5
        int s=Integer.parseInt(start, 16);//将16进制字符start转换为10进制整数
        int e=Integer.parseInt(end, 16);//将16进制字符end转换为10进制整数
        for (int i=s;i<=e;i++){//for循环实现汉字的输出
            String str=(char)i+ "";//类型转换
            System.out.println(str);
            try{
                //根据文字得到图片位置
                String fontImgPath=Configs.creImgBasePath+str+".jpg";
                //然后进行特征提取
                JSONObject classifyJo=ImageAi.getFeature(fontImgPath,str);
                BaseUtil.WriteTxtAppend(Configs.creClassifyFilePath,classifyJo.toJSONString().replaceAll("null","\"\"").replaceAll("\" \"","\"\"")+",");
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("CnnImgToFeatureExtraction方法共计用时："+(stopTime-startTime)/1000+" 秒");
    }

    public static void main(String[] args) {
        CreFontImg();//创建文字图像
        CnnImgToFeatureExtraction();//开始训练文字特征识别
    }
}
