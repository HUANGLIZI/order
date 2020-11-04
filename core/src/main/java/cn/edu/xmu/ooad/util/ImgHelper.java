package cn.edu.xmu.ooad.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 24320182203218
 **/
public class ImgHelper {

    /**
     * 保存文件，直接以multipartFile形式
     *
     * @param multipartFile
     * @param path          文件保存路径
     * @return 返回文件名
     */
    public static String saveImg(MultipartFile multipartFile, String path, Integer id) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();
        //原文件名
        String fileName = multipartFile.getOriginalFilename();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + File.separator + fileName));
        byte[] bs = new byte[1024];
        int len;
        while ((len = fileInputStream.read(bs)) != -1) {
            bos.write(bs, 0, len);
        }
        bos.flush();
        bos.close();

        String newFileName = id + getMD5(path + File.separator + multipartFile.getOriginalFilename())+".png";
        File newFile = new File(path + File.separator + newFileName);
        File oldFile = new File(path + File.separator + fileName);
        if(newFile.exists())
            oldFile.delete();
        else
            oldFile.renameTo(newFile);

        return newFileName;
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     * @param path     文件路径
     */
    public static void deleteImg(String filename, String path) {
        File file = new File(path + File.separator + filename);
        if (file.exists()) {
            file.delete();
        }
        return;
    }


    /**
     * 计算md5
     *
     * @param  path          文件路径及其文件名
     * @return md5
     */
    public static String getMD5(String path) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }

}
