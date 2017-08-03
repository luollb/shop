package com.xincai.retrofitdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by luo on 2017/8/3.
 */

public class FileUtils {

    /**
     * 创建文件夹
     *
     * @param filePath
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    /**
     * 创建文件
     *
     * @param filePath
     * @param fileName
     */
    public static void makeFilePath(String filePath, String fileName) {
        File file = null;
        try {
            makeRootDirectory(filePath);
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入内容
     *
     * @param filePath
     * @param fileName
     * @param content
     * @param flag
     */
    public static void writerFile(String filePath, String fileName, String content, boolean flag) {
        makeFilePath(filePath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(filePath + fileName, flag);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");
            writer.write(content);
            writer.flush();
            writer.close();
            fos.close();
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static String readerFile(String filePath, String fileName) {
        makeFilePath(filePath, fileName);
        String str = "";
        try {
            FileInputStream fis = new FileInputStream(filePath + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String len = null;
            while ((len = br.readLine()) != null) {
                sb.append(len);
            }
            str = sb.toString();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
