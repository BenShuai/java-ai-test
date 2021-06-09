package com.it.common.fontImg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.it.common.util.BaseUtil;

/**
 * 文字的识别测试
 */
public class FontImgRecTest {
    public static void main(String[] args) {
        String imgPath="C:/javaFonts/田.jpg";//待识别的文字的位置         王    天    比     田
        Long startTime=System.currentTimeMillis();//开始时间
        try {
            JSONObject classifyJo = ImageAi.getFeature(imgPath, "");//获取文字的特征

            //加载训练好的特征文件,并按照存入的方式读取成JSONArray
            String features = "["+BaseUtil.ReadTxt(Configs.creClassifyFilePath);
            features=features.substring(0,features.length()-1)+"]";
            JSONArray featuresJa=JSONArray.parseArray(features);
            System.out.println(featuresJa.size());
            for(int i=0;i<featuresJa.size();i++){
                //一个一个比较相似度，并寻找到相似度大于80%的所有的文字
                Double similarity = makeComparisonArr(featuresJa.getJSONObject(i),classifyJo);
                if(similarity>0.8D){//相似度在0.8以上
                    System.out.println(""+featuresJa.getJSONObject(i).getString("font")+" 相似度："+similarity);//输出文字和相似度
                }
            }

        }catch (Exception ee){ee.printStackTrace();}
        Long stopTime=System.currentTimeMillis();//结束时间
        System.out.println("总时间："+(stopTime*1d-startTime*1d)/1000d +" 秒");
    }

    /**
     * 比较两个二维数组的相似度
     * @param featuresJoBase
     * @param classifyJo
     * @return
     */
    private static Double makeComparisonArr(JSONObject featuresJoBase,JSONObject classifyJo){
        Double similarity = 1d;//假设相似度,默认为1，后面比较进行递减
        //先比较 basic 的相似度
        JSONArray basicArr = featuresJoBase.getJSONArray("basic");
        JSONArray classifyJoasicArr = classifyJo.getJSONArray("basic");
        //比较basic的相似度【因为都是固定大小的图像，所以长度是一定相等的，直接比较内容的相似度】
        for(int i=0;i<basicArr.size();i++){
            JSONArray basicBase= basicArr.getJSONArray(i);
            JSONArray classifyJoasicArrBase= classifyJoasicArr.getJSONArray(i);
            //比较 basicBase 和 classifyJoasicArrBase 的相速度
            for (int j=0;j<basicBase.size();j++){
                if(!basicBase.get(j).toString().trim().equals(classifyJoasicArrBase.get(j).toString().trim())){
                    similarity -= (1d / (basicArr.size()*basicBase.size()*1d))*0.3d;
                }
            }
        }

        if(similarity<0.8d){//相似度已经小于0.8，直接返回，节约计算资源
            return similarity;
        }

        //再比较横线数的相似度
        int hnumBase = featuresJoBase.getInteger("Hnum");
        int hnumThis = classifyJo.getInteger("Hnum");
        int diffHunm= Math.abs(hnumBase-hnumThis);
        if(diffHunm!=0) {
            similarity -= ((diffHunm * 1d) / (hnumBase * 1d))*0.5d;//权重0.5
        }

        if(similarity<0.8d){//相似度已经小于0.8，直接返回，节约计算资源
            return similarity;
        }

        //再比较竖线数的相似度
        int vnumBase = featuresJoBase.getInteger("Vnum");
        int vnumThis = classifyJo.getInteger("Vnum");
        int diffVnum = Math.abs(vnumBase-vnumThis);
        if(diffVnum!=0) {
            similarity -= ((diffVnum * 1d) / (vnumBase * 1d))*0.5d;//权重0.5
        }

        if(similarity<0.8d){//相似度已经小于0.8，直接返回，节约计算资源
            return similarity;
        }

        //开始对比 各子特征是否有相似度较高的
        JSONArray classifyArrBase = featuresJoBase.getJSONArray("classifyArr");
        JSONArray thisClassifyArr = classifyJo.getJSONArray("classifyArr");

        //子特征个数差别
        int diffClassifyNum = Math.abs(classifyArrBase.size() - thisClassifyArr.size());
        if(diffClassifyNum!=0){
            similarity -= ((diffClassifyNum * 1d) / (classifyArrBase.size() * 1d))*0.3d;//权重0.3
        }

        int similarNum=0;
        //子特征内容差别
        for(int i=0;i<classifyArrBase.size();i++){
            for (int j=0;j<thisClassifyArr.size();j++){
                Double diffNum=getDiffNum(classifyArrBase.getJSONObject(i).getJSONArray("classify"),thisClassifyArr.getJSONObject(j).getJSONArray("classify"));
                if(diffNum>0.8D){
                    similarNum++;
                }
            }
        }
        int diffSimilarNum = Math.abs(featuresJoBase.getJSONArray("classifyArr").size() - similarNum);
        if(diffSimilarNum!=0){
            similarity-= (diffSimilarNum*1d) / (featuresJoBase.getJSONArray("classifyArr").size()*1d) *0.152d;
        }
        return similarity;
    }

    /**
     * 比较子特征的差别
     * @param classifyBase
     * @param classifyThis
     * @return
     */
    private static Double getDiffNum(JSONArray classifyBase,JSONArray classifyThis){
        Double diffNum=1d;

        //子特征的一元数组长度差别
        int diffLen1=Math.abs(classifyBase.size()-classifyThis.size());
        if(diffLen1!=0){
            diffNum-= (diffLen1*1d)/(classifyBase.size()*1d);
        }
        //子特征的
        int diffLen2=Math.abs(classifyBase.getJSONArray(0).size()-classifyThis.getJSONArray(0).size());
        if(diffLen2!=0){
            diffNum-= (diffLen2*1d)/(classifyBase.getJSONArray(0).size()*1d);
        }

        if(diffNum>0.8D){
            //对子特征的内容进行对比
            for(int i=0;i<classifyBase.size();i++){
                for (int j=0;j<classifyBase.getJSONArray(i).size();j++){
                    try {
                        if (classifyThis.get(i) == null) {
                            diffNum -= (classifyBase.getJSONArray(i).size() * 1d) / (classifyBase.size() * classifyBase.getJSONArray(i).size());
                        } else if (classifyThis.getJSONArray(i).size() < j) {
                            diffNum -= 1d / (classifyBase.size() * classifyBase.getJSONArray(i).size());
                        } else if (classifyThis.getJSONArray(i).size() >= j) {
                            if (classifyBase.getJSONArray(i).getString(j).trim().equals("")) {
                                if (classifyThis.getJSONArray(i).get(j) != null && classifyThis.getJSONArray(i).getString(j).equals("0")) {
                                    diffNum -= 1d / (classifyBase.size() * classifyBase.getJSONArray(i).size());
                                }
                            } else if (classifyBase.getJSONArray(i).getString(j).equals("0")) {
                                if (classifyThis.getJSONArray(i).get(j) == null || classifyThis.getJSONArray(i).getString(j).equals("")) {
                                    diffNum -= 1d / (classifyBase.size() * classifyBase.getJSONArray(i).size());
                                }
                            }
                        }
                    }catch (Exception eee){}
                }
            }
        }

        return diffNum;
    }

}
