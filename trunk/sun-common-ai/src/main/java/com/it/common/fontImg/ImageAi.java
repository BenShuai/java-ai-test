package com.it.common.fontImg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 文字图片的特征提取器【公共的类】
 */
public class ImageAi {
//    /**
//     * 读取一张图片的RGB值
//     *
//     * @throws Exception
//     */
//    private static void getImagePixel(String image) throws Exception {
//        int[] rgb = new int[3];
//        File file = new File(image);
//        BufferedImage bi = ImageIO.read(file);
//        int width = bi.getWidth();
//        int height = bi.getHeight();
//        int minx = bi.getMinX();
//        int miny = bi.getMinY();
//        System.out.println("width=" + width + ",height=" + height + ".");
//        System.out.println("minx=" + minx + ",miniy=" + miny + ".");
//        for (int i = minx; i < width; i++) {
//            for (int j = miny; j < height; j++) {
//                int pixel = bi.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
//                rgb[0] = (pixel & 0xff0000) >> 16;
//                rgb[1] = (pixel & 0xff00) >> 8;
//                rgb[2] = (pixel & 0xff);
//                System.out.println("i=" + i + ",j=" + j + ":(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")");
//            }
//        }
//    }

    public static void main(String[] args) {
        try{
//            getImagePixel("f:/hua.jpg");
            JSONObject classifyJo=getFeature(Configs.creImgBasePath+"江.jpg","江");
//            System.out.println(classifyJo.toJSONString());
        }catch (Exception e){}
    }

    /**
     * 返回文字图片的特征
     * @param filePath  文字的图片位置
     * @param fontStr   训练时，需要提供当前图像是什么文字  【  如：getFeature("/opt/fontsImg/王.jpg","王")   】
     * @return
     * @throws Exception
     */
    public static JSONObject getFeature(String filePath,String fontStr) throws Exception {
        //用来保存特征的JSON
        JSONObject classifyJo=new JSONObject();
        classifyJo.put("font",fontStr);
        classifyJo.put("classifyArr",new JSONArray());

        int[] rgb = new int[3];
        File file = new File(filePath);
        BufferedImage bi = ImageIO.read(file);
        int width = bi.getWidth();
        int height = bi.getHeight();

        String[][] classify = new String[width][height];//图片的二级化数组【二维数组】
        //先将图片二级化
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pixel = bi.getRGB(i, j); // 下面三行代码将一个十六进制数字转换为RGB数字
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);

                String num=" ";//像素极值
                if(rgb[0]<=127 || rgb[1]<=127 || rgb[2]<=127){
                    num="0";
                }
                System.out.print(num);
                classify[i][j]=num;
            }
            System.out.println();
        }

        JSONObject classifyJoBase = new JSONObject();
        classifyJoBase.put("jo",classify);
        String classifyJoBaseStr = classifyJoBase.toJSONString();
        classifyJo.put("basic",JSONObject.parseObject(classifyJoBaseStr).getJSONArray("jo"));//最原始的特征记录[复制的]

        //开始提取 二维数组 classify 中的特征
        getArrayHVNum(classify,classifyJo);
        getArrayFeature(classify,classifyJo);

        return classifyJo;
    }

    /**
     * 提取 二维数组 中的特征
     * 根据像素的连续性来提取
     * @param classify 二维的二级化特征值
     * @param classifyJo  对应的文字的特征
     */
    private static void getArrayFeature(String[][] classify,JSONObject classifyJo){
        String[][] classifyNew = new String[classify.length][classify[0].length];//创建一个新的二维数组

        //从[0][0] 开始分析,寻找一个最左上方有特征的点的坐标
        int x=0,y=0;
        try {
            while (!classify[x][y].equals("0")) {//从[0][0] 开始过滤没有特征的点  ,遇到有特征的点就退出while循环
                x++;
                if (x == classify.length - 1) {
                    x = 0;
                    y++;
                }
            }

            List<int[]> arr = new ArrayList<>();//待扫描的有特征的点
            int[] a = {x, y};
            arr.add(a);

            for (int i = 0; i < arr.size(); i++) {
                int[] b = arr.get(i);
                classifyNew[b[0]][b[1]] = "0";//将当前有特征的点放到新数组
                classify[b[0]][b[1]] = " ";//特征提取后，从原图中删除,避免

                //寻找当前点的上下左右，是否有特征，有就放到 arr 集合
                try {
                    if (classify[b[0] - 1][b[1]].equals("0")) {//左面
                        int[] c = {b[0] - 1, b[1]};
                        if (searchForArr(c, arr)) {
                            arr.add(c);
                            classifyNew[c[0]][c[1]] = "0";//将当前有特征的点放到新数组
                            classify[c[0]][c[1]] = " ";
                        }
                    }
                } catch (Exception e) { }
                try {
                    if (classify[b[0] + 1][b[1]].equals("0")) {//右面
                        int[] c = {b[0] + 1, b[1]};
                        if (searchForArr(c, arr)) {
                            arr.add(c);
                            classifyNew[c[0]][c[1]] = "0";//将当前有特征的点放到新数组
                            classify[c[0]][c[1]] = " ";
                        }
                    }
                } catch (Exception e) { }
                try {
                    if (classify[b[0]][b[1] - 1].equals("0")) {//上面
                        int[] c = {b[0], b[1] - 1};
                        if (searchForArr(c, arr)) {
                            arr.add(c);
                            classifyNew[c[0]][c[1]] = "0";//将当前有特征的点放到新数组
                            classify[c[0]][c[1]] = " ";
                        }
                    }
                } catch (Exception e) { }
                try {
                    if (classify[b[0]][b[1] + 1].equals("0")) {//下面
                        int[] c = {b[0], b[1] + 1};
                        if (searchForArr(c, arr)) {//c 不在 arr 中的时候，才将 c 放到 arr 中
                            arr.add(c);
                            classifyNew[c[0]][c[1]] = "0";//将当前有特征的点放到新数组
                            classify[c[0]][c[1]] = " ";
                        }
                    }
                } catch (Exception e) { }
            }

            //将特征缩小
            //先寻找到范围
            //up：首次出现有效特征的行下标,先给一个超大的值，当循环得到的 有效特征行下标  小于up  时，则替换 up
            //down：最后出现有效特征的行下标
            //left：首次出现有效特征的列下标，同 up 类似
            //right：最后出现有效特征的列下表
            int up=100000,down=0,left=100000,right=0;
            for (int i=0;i<classifyNew.length;i++){
                for (int j=0;j<classifyNew[i].length;j++){
                    if(StringUtils.isNotEmpty(classifyNew[i][j]) && classifyNew[i][j].equals("0")){
                        if(i>right){
                            right=i;
                        }
                        if(i<left){
                            left=i;
                        }

                        if(j>down){
                            down=j;
                        }
                        if(j<up){
                            up=j;
                        }
                    }
                }
            }
            //然后将范围内的数据转移到一个新的二维数组
            System.out.println("up:"+up+"  down:"+down+"  left:"+left+"  right:"+right);
            String[][] classifyNewOver = new String[right-left+1][down-up+1];//创建一个新的二维数组，过滤后的特征数组
            for (int j = up; j <= down; j++) {
                for (int i = left; i <= right; i++) {
                    classifyNewOver[i-left][j-up]=classifyNew[i][j];
                }
            }

            /**
             * 这里打印过滤后的特征
             */
            for (int j = 0; j < classifyNewOver[0].length; j++) {
                for (int i = 0; i < classifyNewOver.length; i++) {
                    if (StringUtils.isNotEmpty(classifyNewOver[i][j])) {
                        System.out.print(classifyNewOver[i][j]);
                    } else {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
            //将特征保存到文件或数据库【至少两个点的特征才保存到特征文件】
            if(classifyNewOver.length>1 || classifyNewOver[0].length>1) {
                JSONObject jo = new JSONObject();
                jo.put("classify", classifyNewOver);
                classifyJo.getJSONArray("classifyArr").add(jo);
            }
            getArrayFeature(classify,classifyJo);//递归调用
        }catch (Exception eee){}//只有在已经没有特征点的情况下才会进入到catch
    }

    /**
     * 寻找 数组c 是否在集合 arr 中
     * 如果在集合中就返回 false
     * @param c
     * @param arr
     */
    private static boolean searchForArr(int[] c,List<int[]> arr){
        for (int i=0;i<arr.size();i++){
            if(arr.get(i)[0]==c[0] && arr.get(i)[1]==c[1]){
                return false;
            }
        }
        return true;
    }

    /**
     * 获取文字中的 横 和 竖 的个数
     * @param classify
     * @param classifyJo
     */
    private static void getArrayHVNum(String[][] classify,JSONObject classifyJo){
        Map<Integer,Integer> Hnum=new HashMap<>();//横的个数
        Map<Integer,Integer> Vnum=new HashMap<>();//竖的个数

        for (int i=0;i<classify.length;i++){
            for (int j=0;j<classify[i].length;j++){
                if(StringUtils.isNotEmpty(classify[i][j]) && classify[i][j].equals("0")){//要连续6个以上才算
                    try {
                        if (StringUtils.isNotEmpty(classify[i + 1][j]) && classify[i+1][j].equals("0") &&
                                StringUtils.isNotEmpty(classify[i + 2][j]) && classify[i+2][j].equals("0") &&
                                StringUtils.isNotEmpty(classify[i + 3][j]) && classify[i+3][j].equals("0") &&
                                StringUtils.isNotEmpty(classify[i + 4][j]) && classify[i+4][j].equals("0") &&
                                StringUtils.isNotEmpty(classify[i + 5][j]) && classify[i+5][j].equals("0")) {
                            if(j!=0 && !Hnum.containsKey(j-1)){
                                Hnum.put(j,1);
                            }else if(j==0){
                                Hnum.put(j,1);
                            }
                        }
                    }catch (Exception e){}
                    try {
                        if (StringUtils.isNotEmpty(classify[i][j+1]) && classify[i][j+1].equals("0") &&
                                StringUtils.isNotEmpty(classify[i][j+2]) && classify[i][j+2].equals("0") &&
                                StringUtils.isNotEmpty(classify[i][j+3]) && classify[i][j+3].equals("0") &&
                                StringUtils.isNotEmpty(classify[i][j+4]) && classify[i][j+4].equals("0") &&
                                StringUtils.isNotEmpty(classify[i][j+5]) && classify[i][j+5].equals("0")) {
                            if(i!=0 && !Vnum.containsKey(i-1)) {
                                Vnum.put(i, 1);
                            }else if (i==0){
                                Vnum.put(i, 1);
                            }
                        }
                    }catch (Exception e){}
                }
            }
        }
        classifyJo.put("Hnum",Hnum.size());
        classifyJo.put("Vnum",Vnum.size());
        System.out.println("横线数:"+Hnum.size()+" 竖线数:"+Vnum.size());
    }

}
