package com.it.common.util.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

/**
 * 获取中文笔画数
 */
public class GetChineseStrokeUtils {

    public static JSONObject chineseCode = null;

    /**
     * 从文件中获取汉字中文编码JSON对象
     * @return
     */
    public static JSONObject getStrokeJO(){
        //判断汉字编码JSON数据是否初始化，
        // 若未初始化进行初始化后返回
        if (null == chineseCode){
            BufferedReader reader = null;
            try {
                InputStream in = GetChineseStrokeUtils.class.getClassLoader().getResourceAsStream("stroke.json");
                reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                String codeStr;
                StringBuilder sb = new StringBuilder();
                while ((codeStr = reader.readLine()) != null){
                    sb.append(codeStr);
                }
                chineseCode = JSONObject.parseObject(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (null != reader){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return chineseCode;
    }

    /**
     * 传入一个汉字的 hashCode，返回当前汉字的笔画数
     * @param hashCode
     * @return
     */
    public static int getChineseStroke(int hashCode){

        JSONObject chineseJo = getStrokeJO();
        int stroke = chineseJo.getJSONObject(hashCode+"").getIntValue("strokeSum");
        return stroke;
    }
}
