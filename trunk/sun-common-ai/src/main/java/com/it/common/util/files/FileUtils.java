package com.it.common.util.files;

import com.it.common.util.BaseUtil;
import com.it.common.util.utils.PicUtil;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;


public class FileUtils {

	public static boolean createFileDirectory(String destDirName) {
		File dir = new File(destDirName);  
        if (dir.exists()) {  
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");  
            return false;  
        }  
        if (!destDirName.endsWith(File.separator)) {  
            destDirName = destDirName + File.separator;  
        }  
        //创建目录  
        if (dir.mkdirs()) {  
            System.out.println("创建目录" + destDirName + "成功！");  
            return true;  
        } else {  
            System.out.println("创建目录" + destDirName + "失败！");  
            return false;  
        }  
		
	}

	//根据文件前缀和文件保存路径保存图片，并返回图片路径字符串[保存动态名字的图片]
    public static String fileUpload(String prefix, String filePath, MultipartFile multipartFile){
        String suffix = multipartFile.getContentType().toLowerCase();//图片后缀，用以识别哪种格式数据
        suffix = suffix.substring(suffix.lastIndexOf("/")+1);

        if(suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("png") || suffix.equals("gif") || suffix.equals("mp4") || suffix.equals("flv")) {
            String FileName = prefix + "_" + BaseUtil.GetRmStr(10) + "." + suffix;

            File targetFile = new File(filePath, FileName);
            if (!targetFile.getParentFile().exists()) { //注意，判断父级路径是否存在
                targetFile.getParentFile().mkdirs();
            }
            //保存
            try {
                multipartFile.transferTo(targetFile);
            } catch (Exception e) {}

            return filePath + FileName;
        }else {
            return null;
        }
    }

    //根据文件名字和文件保存路径保存图片，并返回图片路径字符串[保存固定名字的文件]
    public static String fileUpload2(String fileName, String filePath, MultipartFile multipartFile){
        String suffix = multipartFile.getContentType().toLowerCase();//图片后缀，用以识别哪种格式数据
        suffix = suffix.substring(suffix.lastIndexOf("/")+1);

        if(suffix.equals("jpg") || suffix.equals("jpeg") || suffix.equals("png") || suffix.equals("gif") || suffix.equals("mp4") || suffix.equals("flv")) {
            String FileName = fileName +"." + suffix;

            File targetFile = new File(filePath, FileName);
            if (!targetFile.getParentFile().exists()) { //注意，判断父级路径是否存在
                targetFile.getParentFile().mkdirs();
            }
            //保存
            try {
                multipartFile.transferTo(targetFile);
            } catch (Exception e) {}
            return filePath + FileName;
        }else {
            return null;
        }
    }




    //对品牌的图片进行缩小
    public static String toShrinkImg(String imgPath){
	    try {
            String str = imgPath.toString().substring(imgPath.toString().lastIndexOf("."));
            String minImg = imgPath.replace(str, "_min" + str);
            PicUtil.resizePng(new File(imgPath), new File(minImg));
            return minImg;
        }catch (Exception e){}
        return imgPath;
    }

}
