package com.it.common.util;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileReader;

public class DESUtil {
    /**
     * 加密,调用js的方法
     * @param str
     * @param key
     * @return
     */
    public static String Encrypt(String str,String key){
        try {
            String jsPath=System.getProperty("user.dir");
            // 获取JS执行引擎
            ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
            // 获取变量
            Bindings bindings = se.createBindings();
            se.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            se.eval(new FileReader(jsPath+"/CDES.js"));
            // 是否可调用
            if (se instanceof Invocable) {
                Invocable in = (Invocable) se;
                String result = in.invokeFunction("Encrypt", str, key).toString();
                return result;
            }
        }catch (Exception e){e.printStackTrace();}
        return "授权码生成失败";
    }

    /**
     * 解密,调用js的方法
     * @param str
     * @param key
     * @return
     */
    public static String Decrypt(String str,String key){
        try {
            String jsPath=System.getProperty("user.dir");
            // 获取JS执行引擎
            ScriptEngine se = new ScriptEngineManager().getEngineByName("javascript");
            // 获取变量
            Bindings bindings = se.createBindings();
            se.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            se.eval(new FileReader(jsPath+"/CDES.js"));
            // 是否可调用
            if (se instanceof Invocable) {
                Invocable in = (Invocable) se;
                String result = in.invokeFunction("Decrypt", str, key).toString();
                return result;
            }
        }catch (Exception e){e.printStackTrace();}
        return "授权码解析失败";
    }

}
