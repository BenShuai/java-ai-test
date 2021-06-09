package com.it.common.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

/**
 * 工具类
 *
 * @author 孙帅
 */
public class BaseUtil {
    private static String encoding = "UTF-8";

    /**
     * 返回json文件的内容
     *
     * @param jsonFileName
     * @return
     * @throws Exception
     */
    public static String jsonCon(String jsonFileName) throws Exception {
		BaseUtil cu = new BaseUtil();
        InputStream in = cu.getClass().getClassLoader().getResourceAsStream(jsonFileName);
        byte[] b = new byte[10240000];
        int n;
        StringBuffer content = new StringBuffer();
        while ((n = in.read(b)) != -1) {
            content.append(new String(b, 0, n, encoding));
        }
        in.close();
        return content.toString();
    }

    /**
     * @param conn 通过get方式获取StringBuffer(内部方法)
     * @return
     */
    private static StringBuffer getJsonString(URLConnection conn) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            isr = new InputStreamReader(conn.getInputStream(), encoding);
            br = new BufferedReader(isr);
            String line = null;
            sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
        } catch (Exception e) {
            System.out.println("读取流异常\r\n" + e.getMessage());
        } finally {
            try {
                if (isr != null) {
					isr.close();
				}
            } catch (IOException e) {
                System.out.println("流关闭异常\r\n" + e.getMessage());
            }
        }
        return sb;
    }

    /**
     * 调用远端接口返回数据(Get)
     *
     * @param urlStr 远端数据接口地址
     * @return
     */
    public static String getHttpClentsJson(String urlStr,Map<String,Object> headParams){
    	try{
	    	URL u = new URL(urlStr);
			URLConnection conn = u.openConnection();// 打开网页链接
//			conn.setReadTimeout(10000);//读取超时
//			conn.setConnectTimeout(10000);//连接超时

			if(headParams!=null && headParams.size()>0){
				for (String headKey : headParams.keySet()) {
					conn.addRequestProperty(headKey,(String)headParams.get(headKey));
				}
			}

			//返回结果
			String cloudJson = getJsonString(conn).toString();
	    	return cloudJson;
    	}catch (Exception e) {e.printStackTrace();
    		System.out.println( "连接【"+urlStr+"】失败\r\n"+e.getMessage());
    		return "请求或数据接口出现错误";
    	}

    }


    /**
     * 有参数的httpClent请求(POST)
     * @param urlStr    请求地址
     * @param params    参数[Map类型] Map<String,Object>
	 * @param headParams    头参数[Map类型] Map<String,Object>
     * @return  请求结果
     */
    public static String postHttpClentsJson(String urlStr,Map<String,Object> params,Map<String,Object> headParams){
    	HttpClient clients = new HttpClient();
//    	clients.getHostConfiguration().setProxy(proxy.get("ip"),Integer.parseInt(proxy.get("port")));//设置代理
//    	clients.getParams().setAuthenticationPreemptive(true);//使用抢先认证
    	
    	PostMethod connPost = new PostMethod();
    	try{
			URI u=new URI(urlStr);
    		connPost.setURI(u);
    		if(headParams!=null && headParams.size()>0){
				for (String headKey : headParams.keySet()) {
					connPost.addRequestHeader(headKey,(String)headParams.get(headKey));
				}
			}
    		if(params!=null && params.size()>0){
				for (String key : params.keySet()) {
					connPost.setParameter(key, params.get(key).toString());
					connPost.getParams().setContentCharset(encoding);//参数转码
				}
    		}
			clients.getHttpConnectionManager().getParams().setConnectionTimeout(10000);//默认10秒链接超时
			clients.getHttpConnectionManager().getParams().setSoTimeout(10000);//默认10秒读取超时
			int status = clients.executeMethod(connPost);
			if(status==200){
				BufferedReader buReader = new BufferedReader(new InputStreamReader(connPost.getResponseBodyAsStream(),encoding));
				StringBuffer cloudJson = new StringBuffer();
				String line;
				while((line=buReader.readLine())!=null){
					cloudJson.append(line);
				}
				buReader.close();
		    	return cloudJson.toString();
			}else{
				BufferedReader buReader = new BufferedReader(new InputStreamReader(connPost.getResponseBodyAsStream(),encoding));
				StringBuffer cloudJson = new StringBuffer();
				String line;
				while((line=buReader.readLine())!=null){
					cloudJson.append(line);
				}
				buReader.close();
				System.out.println("请求接口错误=====错误码："+status+"\r\n请求地址："+urlStr+" 返回结果:"+cloudJson.toString());
				return "HTTPCLIENT_CONNECTION_ERROR";
			}
    		
    	}catch (Exception e) {
    		System.out.println( "连接【"+urlStr+"】失败\r\n"+e.getMessage());
    		return "HTTPCLIENT_CONNECTION_ERROR";
    	}finally {
    		//释放掉HTTP连接
    		connPost.releaseConnection();
    		clients.getHttpConnectionManager().closeIdleConnections(0);
		}
    }


    /**
     * 没有参数名，只有参数值的httpClent请求(POST)
     *
     * @param urlStr 请求地址
     * @param params 参数[JSONArray类型(数据格式模拟：/---[{"key":"username","value":"sunshuai"},{"key","password","value":"123456"}]---/ )]
     * @return 结果
     */
    public static String postHttpClentsJsonNoEntity(String urlStr,String params,Map<String,Object> headParams){
    	HttpClient clients = new HttpClient();
    	PostMethod connPost = new PostMethod();
    	try{
			URI u=new URI(urlStr);
    		connPost.setURI(u);
    		if(params!=null && !params.equals("")){
				connPost.setRequestBody(params);
    		}

			if(headParams!=null && headParams.size()>0){
				for (String headKey : headParams.keySet()) {
					connPost.addRequestHeader(headKey,(String)headParams.get(headKey));
				}
			}

			clients.getHttpConnectionManager().getParams().setConnectionTimeout(15000);//默认15秒链接超时
			clients.getHttpConnectionManager().getParams().setSoTimeout(15000);//默认15秒读取超时
			int status = clients.executeMethod(connPost);
			if(status==200){
				BufferedReader buReader = new BufferedReader(new InputStreamReader(connPost.getResponseBodyAsStream(),encoding));
				StringBuffer cloudJson = new StringBuffer();
				String line;
				while((line=buReader.readLine())!=null){
					cloudJson.append(line);
				}
				buReader.close();
		    	return cloudJson.toString();
			}else{
                BufferedReader buReader = new BufferedReader(new InputStreamReader(connPost.getResponseBodyAsStream(),encoding));
                StringBuffer cloudJson = new StringBuffer();
                String line;
                while((line=buReader.readLine())!=null){
                    cloudJson.append(line);
                }
                buReader.close();
				System.out.println("请求接口错误=====错误码："+status+"\r\n请求地址："+urlStr+" 返回结果："+cloudJson.toString());
				return "请求或数据接口出现错误";
			}
    		
    	}catch (Exception e) {
    		System.out.println( "连接【"+urlStr+"】失败\r\n"+e.getMessage());
    		return "请求或数据接口出现错误";
    	}finally {
    		//释放掉HTTP连接
    		connPost.releaseConnection();
    		clients.getHttpConnectionManager().closeIdleConnections(0);
		}
    }


	/**
	 * 模仿表单提交上传文件
	 * @author SunShuai
	 * @param urlStr
	 * @param textMap
	 * @param filepath
	 * @return
	 */
	public static String formUpload(String urlStr, Map<String, String> textMap,String fileParam,String filepath,Map<String,Object> headParams) {
		String res = "";
		HttpURLConnection conn = null;
		// boundary就是request头和上传文件内容的分隔符
		String BOUNDARY = "---------------------------"+System.currentTimeMillis();
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();

			if(headParams!=null && headParams.size()>0){
				for (String headKey : headParams.keySet()) {
					conn.addRequestProperty(headKey,(String)headParams.get(headKey));
				}
			}

			conn.setConnectTimeout(30000);//提交超时30秒
			conn.setReadTimeout(10000);//读取超时10秒
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// text
			if (textMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator iter = textMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY)
							.append("\r\n");
					strBuf.append("Content-Disposition:form-data;name=\""
							+ inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}
			// file
			if (StringUtils.isNotEmpty(filepath)) {
				File file = new File(filepath);
				String filename = file.getName();

				//没有传入文件类型，同时根据文件获取不到类型，默认采用application/octet-stream
				String contentType = new MimetypesFileTypeMap().getContentType(file);
				//contentType非空采用filename匹配默认的图片类型
				if(!"".equals(contentType)){
					if (filename.endsWith(".png")) {
						contentType = "image/png";
					}else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".jpe")) {
						contentType = "image/jpeg";
					}else if (filename.endsWith(".gif")) {
						contentType = "image/gif";
					}else if (filename.endsWith(".ico")) {
						contentType = "image/image/x-icon";
					}
				}
				if (contentType == null || "".equals(contentType)) {
					contentType = "application/octet-stream";
				}
				StringBuffer strBuf = new StringBuffer();
				strBuf.append("\r\n").append("--").append(BOUNDARY)
						.append("\r\n");
				strBuf.append("Content-Disposition:form-data;name=\""
						+ fileParam + "\";filename=\"" + filename
						+ "\"\r\n");
				strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
				out.write(strBuf.toString().getBytes());
				DataInputStream in = new DataInputStream(
						new FileInputStream(file));
				int bytes = 0;
				byte[] bufferOut = new byte[1024];
				while ((bytes = in.read(bufferOut)) != -1) {
					out.write(bufferOut, 0, bytes);
				}
				in.close();
			}
			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();
			// 读取返回数据
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
		} catch (Exception e) {
			System.out.println("发送POST请求出错。" + urlStr);
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}






	/**
     * 获取系统当前时间组成的字符串，( GeShi : 显示格式 )
     *
     * @return 当前时间组成的字符串
     */
    public static String GetDataStr(String GeShi) {
        SimpleDateFormat sdf = new SimpleDateFormat(GeShi);
        String getDate = sdf.format(new Date());
        return getDate;
    }


    /**
     * 将指定的时间转为字符串格式
     *
     * @param date  指定的时间
     * @param GeShi 转换后显示的格式
     * @return
     */
    public static String GetDataStrs(Date date, String GeShi) {
        SimpleDateFormat sdf = new SimpleDateFormat(GeShi);
        String getDate = sdf.format(date);
        return getDate;
    }


	/**
	 * 将指定的时间转为字符串格式
	 *
	 * @param date  指定的时间
	 * @param GeShi 转换后显示的格式
	 * @return
	 */
	public static Date GetDataStrsToData(Date date, String GeShi) {
		SimpleDateFormat sdf = new SimpleDateFormat(GeShi);
		String getDate = sdf.format(date);
		return GetStringToDate(GeShi,getDate);
	}


	/**
	 * 将指定的时间转为i天之后的时间
	 *
	 * @param date  指定的时间
	 * @param afterDay 加i天
	 * @return
	 */
	public static Date GetDataStrsAfterI(Date date, int afterDay) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, afterDay);// +i天
		Date afterDate = c.getTime();
		return afterDate;
	}
    
    /**
     * 将指定的时间的Long值转为对应格式的字符串格式时间
     *
     * @param dateLong  指定的时间(Long值)
     * @param GeShi 转换后显示的格式
     * @return
     */
    public static String GetDataStrs(Long dateLong, String GeShi) {
        Date date = new Date(dateLong);
        SimpleDateFormat sdf = new SimpleDateFormat(GeShi);
        String getDate = sdf.format(date);
        return getDate;
    }

    /**
     * 计算两个时间的差值(秒数)
     *
     * @param date
     * @param date2
     * @return
     */
    public static double GetDataMinis(Date date, Date date2) {
        long t1 = date.getTime();
        long t2 = date2.getTime();
        double tnum = (t2 - t1) / 1000;
        return tnum;
    }

    /**
     * 将字符串时间转换成对应格式的DATA类型
     *
     * @param GeShi 格式
     * @param times 字符串时间
     * @return
     */
    public static Date GetStringToDate(String GeShi, String times) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(GeShi);
            return sdf.parse(times);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回当前时间的LONG值
     *
     * @return
     */
    public static Long GetTimeLong() {
        Date dt = new Date();
        return dt.getTime();
    }
    
    /**
     * 将对应的时间格式转换成String类型
     * @param geShi
     * @param times
     * @return
     */
    public static String getTimeStr(String geShi, String times){
    	try{
    		SimpleDateFormat sdf = new SimpleDateFormat(geShi);
    		Date time = sdf.parse(times);
    		return sdf.format(time);
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return "";
	}
	
	
    /**
     * 返回当天0点0分0秒的时间的Long值
     *
     * @return
     */
    public static Long GetZeroTimeLong() {
        String ZeroTimeStr = GetDataStr("yyyy-MM-dd 00:00:00");
        Date ZeroTimeDt = GetStringToDate("yyyy-MM-dd 00:00:00", ZeroTimeStr);
        return ZeroTimeDt.getTime();
    }

	/**
	 * 返回当天0点0分0秒的时间Date
	 * @return
	 */
	public static Date GetZeroTimeDate() {
		String ZeroTimeStr = GetDataStr("yyyy-MM-dd 00:00:00");
		Date ZeroTimeDt = GetStringToDate("yyyy-MM-dd 00:00:00", ZeroTimeStr);
		return ZeroTimeDt;
	}

    /**
     * 获取UUID
     *
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString().replace("-", "");
        return str;
    }

    /**
     * 返回随机字符串
     *
     * @param StrLength
     * @return
     */
    public static String GetRmStr(int StrLength) {
        String[] list = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuffer strB = new StringBuffer();
        for (int i = 0; i < StrLength; i++) {
            Random rd = new Random();
            int Ind = rd.nextInt(list.length);
            strB.append(list[Ind]);
        }
        return strB.toString();
    }

	/**
	 * 返回随机长度的数字
	 *
	 * @param StrLength
	 * @return
	 */
	public static String GetRmNumStr(int StrLength) {
		String[] list = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		StringBuffer strB = new StringBuffer();
		for (int i = 0; i < StrLength; i++) {
			Random rd = new Random();
			int Ind = rd.nextInt(list.length);
			strB.append(list[Ind]);
		}
		return strB.toString();
	}

	/**
	 * 返回一个类似UUID的编号
	 * @return
	 */
	public static String getObjIdNum(){
		Random rd = new Random();
		int ind = rd.nextInt(4)+4;//生成4-8位数
		StringBuilder uid = new StringBuilder(GetRmStr(ind)).append("-");

		Random rd2 = new Random();
		int ind2 = rd2.nextInt(4)+4;//生成4-8位数
		uid.append(GetRmStr(ind2)).append("-");

		Random rd3 = new Random();
		int ind3 = rd3.nextInt(4)+4;//生成4-8位数
		uid.append(GetRmStr(ind3));
		return uid.toString();
	}

    
    /**
     * 剪切成正方形缩略图
     * @param src   原图位置        如   /image/a.jpg
     * @param dest  剪切图位置    如   /image_thumbnail/a.jpg
     * @param Dw    最后的宽度
     * @param Dh    最后的高度
     * @throws IOException
     */
    public static void CutZoomImage(String src,String dest,int Dw,int Dh) throws IOException{
    	float Dratio=Float.parseFloat(Dw+"")/Float.parseFloat(Dh+"");//结果图的宽高的比例
        Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = (ImageReader)iterator.next();
        InputStream in=new FileInputStream(src);
        ImageInputStream iis = ImageIO.createImageInputStream(in);
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
        int w=reader.getWidth(0);//获得图片的宽度
        int h=reader.getHeight(0);//获得图片的高度
        float ratio=Float.parseFloat(w+"")/Float.parseFloat(h+"");//原图的宽高比例
        //将宽高调整为一样比例的大小
        int x=0;//剪切坐标X
    	int y=0;//剪切坐标Y
        if(ratio>Dratio){//原图过宽(以高度为基本大小截取)
        	int NewW=Integer.parseInt(new DecimalFormat("###").format(h*Dratio));//按比例计算的原图宽度
        	x=(w-NewW)/2;
        	w=NewW;
        	y=0;
        }else{//原图过高(以宽度为基本大小截取)
        	int NewH=Integer.parseInt(new DecimalFormat("###").format(w/Dratio));//按比例计算的原图高度
        	x=0;
        	y=(h-NewH)/2;
        	h=NewH;
        }
        
        Rectangle rect = new Rectangle(x, y, w,h);
        param.setSourceRegion(rect);
        BufferedImage bi = reader.read(0,param);
        
        //开始缩小=============================================
        Image Itemp = bi.getScaledInstance(w, h, bi.SCALE_SMOOTH);
        double wr=Dw*1.0/bi.getWidth();
        double hr=Dh*1.0/bi.getHeight();
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bi, null);
        
        //将剪切缩略的图片最后保存到指定的地址
        try {
        	File destFile = new File(dest);
            ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
	 * 字符串混淆
	 * @param strS   
	 * @return
	 */
	public static String getEncode(String strS){
		StringBuilder EncodePass=new StringBuilder(GetRmStr(5));
		for (int i = 0; i < strS.length(); i++) {
			EncodePass.append(strS.substring(i, i+1)+GetRmStr(5));
		}
		return EncodePass.toString();
	}
	/**
	 * 还原字符串
	 * @param Str   需要还原的字符串
	 * @return
	 */
	public static String getDecode(String Str){
		StringBuilder EncodePass=new StringBuilder();
		for (int i = 5; i < Str.length(); i++) {
			EncodePass.append(Str.substring(i, i+1));
			i=i+5;
		}
		return EncodePass.toString();
	}
	
	/**
	 * 写入本地文件,覆盖式
	 * @param path
	 * @param value
	 * @return
	 */
	public static String WriteTxt(String path,String value){
		BufferedWriter writer=null;
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(path);
			writer = new BufferedWriter(new OutputStreamWriter(fos,encoding));
			writer.write(value);
        } catch (Exception e) {
        	System.out.println("写入文件失败,地址："+path);
        	return "ERROR";
        }finally{
        	if(writer!=null){
				try {
					writer.close();
					writer=null;
				} catch (Exception e) {
					System.out.println("文件读写关闭输入流失败");
				}
			}
        	if(fos!=null){
				try {
					fos.close();
					fos=null;
				} catch (Exception e) {
					System.out.println("文件读写关闭输入流失败");
				}
			}
        }
		return "OK";
	}
	/**
	 * 写入本地文件,追加式
	 * @param path
	 * @param value
	 * @return
	 */
	public static String WriteTxtAppend(String path,String value) {
		FileWriter fw = null;
		try {
			//如果文件存在，则追加内容;如果文件不存在，则创建文件
			File f = new File(path);
			fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println(value);
		pw.flush();
		try {
			fw.flush();
			pw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "OK";
	}
	/**
	 * 读取本地文件
	 * @param path
	 * @return
	 */
	public static String ReadTxt(String path){
		FileInputStream fs=null;
		InputStreamReader isr=null;
		BufferedReader br=null;
		try {
			fs = new FileInputStream(path);
			isr = new InputStreamReader(fs,encoding);
			br = new BufferedReader(isr);
			StringBuilder value=new StringBuilder();
			while (true) {
				String DataLong=br.readLine();
				if(DataLong!=null){
					value.append(DataLong).append("\r\n");
				}else{
					break;
				}
			}
			return value.toString();
		} catch (Exception e) {
//			System.out.println("文件读取失败，地址："+path);
		}finally{
			if(br!=null){
				try {
					br.close();
					br=null;
				} catch (Exception e) { }
			}
			if(isr!=null){
				try {
					isr.close();
					isr=null;
				} catch (Exception e) { }
			}
			if(fs!=null){
				try {
					fs.close();
					fs=null;
				} catch (Exception e) { }
			}
		}
		return "";
	}

	/**
	 * 读取上传的文件
	 * @param file
	 * @return
	 */
	public static String ReadMultipartFile(MultipartFile file){
		Reader reader=null;
		BufferedReader br=null;
		try {
			reader = new InputStreamReader(file.getInputStream(), "utf-8");
			br = new BufferedReader(reader);
			StringBuilder value=new StringBuilder();
			while (true) {
				String DataLong=br.readLine();
				if(DataLong!=null){
					value.append(DataLong).append("\r\n");
				}else{
					break;
				}
			}
			return value.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
					br=null;
				} catch (Exception e) { }
			}
			if(reader!=null){
				try {
					reader.close();
					reader=null;
				} catch (Exception e) { }
			}
		}
		return "";
	}



	

	/**
	 * 删除文件,只支持删除文件,不支持删除目录
	 * @param file
	 * @throws Exception
	 */
	public static void delFile(File file) throws Exception {
		if (!file.exists()) {
			throw new Exception("文件" + file.getName() + "不存在,请确认!");
		}
		if (file.isFile()) {
			if (file.canWrite()) {
				file.delete();
			} else {
				throw new Exception("文件" + file.getName() + "只读,无法删除,请手动删除!");
			}
		} else {
			throw new Exception("文件" + file.getName() + "不是一个标准的文件,有可能为目录,请确认!");
		}
	}

	/**
	 * 删除文件夹
	 * @param dir
	 * @throws Exception
	 */
	public static void delDir(File dir) throws Exception {
		if (!dir.exists()) {
			throw new Exception("文件夹" + dir.getName() + "不存在,请确认!");
		}
		if (dir.isDirectory()) {
			if (dir.canWrite()) {
				dir.delete();
			} else {
				throw new Exception("文件" + dir.getName() + "只读,无法删除,请手动删除!");
			}
		} else {
			throw new Exception("文件" + dir.getName() + "不是一个标准的文件夹,,请确认!");
		}
	}

	/**
	 * 将文件转换成BYTE字符串(用于网络传输)
	 * @param path
	 * @return
	 */
	public static String FileToByte(String path){
		try{
			File audio=new File(path);
			FileInputStream inputFile = new FileInputStream(audio);
			byte[] buffer = new byte[(int) audio.length()];
			inputFile.read(buffer);
			inputFile.close();
			String ByteStr = new String(Base64.encodeBase64(buffer));
//			try{
//				delFile(audio);//读取完之后就删除临时文件
//			}catch (Exception e) { }
			return ByteStr;
		}catch (Exception e) { }
		return "";
	}


	/**
	 * 将图片流转成base64编码格式
	 * @param in
	 * @return
	 */
	public static String getBase64FromInputStream(InputStream in) {
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		byte[] data = null;
		// 读取图片字节数组
		try {
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[100];
			int rc = 0;
			while ((rc = in.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
			data = swapStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return new String(Base64.encodeBase64(data));
	}


	/**
	 * 转义正则特殊字符 （$()*+.[]?\^{},|）
	 *
	 * @param keyword
	 * @return
	 */
	public static String escapeExprSpecialWord(String keyword) {
		if (StringUtils.isNotBlank(keyword)) {
			String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" ,"#","@","%","_","`","~","!","&","=","-"};
			for (String key : fbsArr) {
				if (keyword.contains(key)) {
					keyword = keyword.replace(key, "\\" + key);
				}
			}
		}
		return keyword;
	}


	/**
	 * 判断是否含有特殊字符
	 *
	 * @param str
	 * @return true为包含，false为不包含
	 */
	public static boolean isSpecialChar(String str) {
		String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.find();
	}

	/**
	 * 返回字符串的MD5加密
	 * @param str
	 * @return
	 */
	public static String getMD5String(String str) {
		try {
			// 生成一个MD5加密计算摘要
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			md.update(str.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			//一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
			return new BigInteger(1, md.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 网络图片下载到本地
	 * @param imgUrl    网络图片地址
	 * @param savePath  本地地址，全路径，包含文件名
	 */
	public static void downloadPicture(String imgUrl,String savePath) {
		DataInputStream dataInputStream=null;
		FileOutputStream fileOutputStream=null;
		ByteArrayOutputStream output=null;
		try {
			URL url = new URL(imgUrl);
			dataInputStream = new DataInputStream(url.openStream());
			fileOutputStream = new FileOutputStream(new File(savePath));
			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = dataInputStream.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			fileOutputStream.write(output.toByteArray());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}finally {
			try{
				output.close();
			}catch (Exception e){}
			try{
				fileOutputStream.close();
			}catch (Exception e){}
			try{
				dataInputStream.close();
			}catch (Exception e){}
		}
	}

	/**
	 * 删除指定位置的 指定前缀的图片
	 * @param rootPath
	 * @param prefix
	 */
	public static void delImage(String rootPath,String prefix){
		File file = new File(rootPath);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				//System.out.println(files[i].getName());
				if(files[i].getName().indexOf(prefix)!=-1){
					files[i].delete();
				}
			}
		}
	}

	/**
	 * 将base64字符串转成图片
	 * @param base64Str
	 * @param imgPath
	 */
	public static void base64ToImg(String base64Str,String imgPath){
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			OutputStream out = null;
			try {
				out = new FileOutputStream(imgPath);
				// Base64解码
				byte[] b = decoder.decodeBuffer(base64Str);
				for (int i = 0; i < b.length; ++i) {
					if (b[i] < 0) {// 调整异常数据
						b[i] += 256;
					}
				}
				out.write(b);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				out.flush();
				out.close();
			}
		}catch (Exception e){}
	}

	/**
	 * 字符串转16进制
	 *
	 * @param str
	 * @return
	 */
	public static String str2HexStr(String str) {
		char[] chars = "0123456789abcdef".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			// sb.append(' ');
		}
		return sb.toString().trim();
	}


	/**
	 * 16进制直接转换成为字符串(无需Unicode解码)
	 *
	 * @param hexStr
	 * @return
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789abcdef";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * 创建文件夹
	 * @param path
	 */
	public static void mkdirToPath(String path){
		File file=new File(path);
		if(!file.exists()){//如果文件夹不存在
			file.mkdir();//创建文件夹
		}
	}



//    public static void main(String[] args) {
//    	ImgWatermark("Z:/0.jpg");//添加水印
    	
    	
//    	try{//调用本地的VBS文件
//	    	String[] cpCmd  = new String[]{"wscript", "G:/孙帅C#软件/TextToVoice/TextToVoice/bin/Debug/sp/2017323/201732316409.vbs"};    
//	        Process process = Runtime.getRuntime().exec(cpCmd);    
//	        // wait for the process end    
//	        int val = process.waitFor();//val 是返回值  
//    	}catch(Exception e){}
    	
    	
    	
//		System.out.println(CorewareUtil.GetRmStr(100));
    	
    	
    	

//		CorewareUtil cu=new CorewareUtil();
//		//没有参数的请求方式
//		String jsonStr=getHttpClentsJson("http://10.132.5.24:8080/objectserver/restapi/alerts/status?collist=ServerSerial");
//		System.out.println(jsonStr);

        //有参数的请求方式
//		JSONArray params=new JSONArray();
//		JSONObject param1=new JSONObject();
//		param1.put("key", "name");
//		param1.put("value", "sunshuai");
//		params.add(param1);
//		
//		JSONObject param2=new JSONObject();
//		param2.put("key", "password");
//		param2.put("value", "123456");
//		params.add(param2);
//		
//		String jsonStr=postHttpClentsJson("http://localhost:8888/som-mobile/comroom/getmenulist",params);
//		System.out.println(jsonStr);
    	
    	
//    	Map a=new HashMap();
//    	a.put("bizEnName", "BIZ_1JZJY");
//    	System.out.println(postHttpClentsJson("http://192.168.1.5/cw-cmdb/getaction/getBizConfig",a));

    	
    	
//    	try{
//    		CutZoomImage("Z:/IMG_20150810_190921.jpg","Z:/suo123456.jpg",640,366);
//    	}catch(Exception ep){ep.printStackTrace();}
    	
//    	try{
//	    	File audio=new File("Z:\\1.jpg");
//	    	FileInputStream inputFile = new FileInputStream(audio);
//	    	byte[] buffer = new byte[(int) audio.length()];
//	    	inputFile.read(buffer);
//			inputFile.close();
//			String json = new String(Base64.encodeBase64(buffer));  
//	        System.out.println(json);
//
//	        BufferedOutputStream bos = null;
//	        FileOutputStream fos = null;
//	        File file = null;
//      	}catch(Exception ep){ep.printStackTrace();}

       /* String json="{\"body\":{\"organ_id\":null,\"phone_number\":\"13580235004\",\"user_type\":\"1\",\"team_name\":\"稽核中心南部区域中心\",\"dept_type\":\"0\",\"rolenames\":\"测试审批角色A,区域中心经责部负责人,常规培训一级审批人,区域副职,测试一下,测,测试测试,6-19测试 ,常规部会签,新增审批角色B,testone,测试666\",\"isleader\":\"0\",\"user_name\":\"tt1\",\"company_name\":null,\"email\":\"tt1@ac.cntaiping.com\",\"real_name\":\"tt1\",\"approvalroleids\":\"208,84,141,64,283,282,281,280,279,268,242,218,217\",\"user_id\":\"20000\",\"team_id\":\"23\"},\"head1\":{\"api_id\":\"\",\"service_ip\":\"10.225.15.9:8080\",\"request_type\":\"02\",\"sender_code\":\"030001\",\"sendreqtime\":\"2017-06-28 10:16:00\",\"version\":\"1.0\"},\"head2\":{\"message\":\"登陆成功！\",\"isflag\":\"1\",\"action_type\":\"\",\"action_name\":\"AppLogin\",\"user_id\":\"20000\",\"pjcode\":\"\"}}";
        JSONObject JA=JSONObject.fromObject(json);
        JSONObject body=JA.optJSONObject("body");
        JSONObject head2=JA.optJSONObject("head2");
        String userId= body .optString("user_id");
        userId=EncryptUtil.encryptAES(userId,"");
        body.put("user_id",userId);
        head2.put("user_id",userId);
        String u=EncryptUtil.decrypt(userId,"");
        System.out.println(JA.toString());*/
//        System.out.println(getEncode("asd1234-"));
//    }
}
