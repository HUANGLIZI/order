package cn.edu.xmu.privilege.util;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;

public class ImgHelper {
    /**
     * 保存文件，直接以multipartFile形式
     *
     * @param multipartFile
     * @param path          文件保存路径
     * @return
     */
    public static ReturnObject saveImg(MultipartFile multipartFile, String path) throws IOException {
        File file = new File(path);
        if(file.exists()&&!file.canWrite())
            return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
        else if (!file.exists()) {
            if(!file.mkdirs()){
                return new ReturnObject(ResponseCode.FILE_NO_WRITE_PERMISSION);
            }
        }
        FileInputStream fileInputStream = (FileInputStream) multipartFile.getInputStream();

        String currentTime = String.valueOf(System.currentTimeMillis());
        String fileName = getMD5(currentTime)+".png";
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + File.separator + fileName));
        byte[] bs = new byte[1024];
        int len;
        while ((len = fileInputStream.read(bs)) != -1) {
            bos.write(bs, 0, len);
        }
        bos.flush();
        bos.close();

        return new ReturnObject(fileName);
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
     * 计算MD5 32位
     *
     * @param inStr
     * @return
     */
    public static String getMD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            char[] charArray = inStr.toCharArray();
            byte[] byteArray = new byte[charArray.length];

            for (int i = 0; i < charArray.length; i++)
                byteArray[i] = (byte) charArray[i];

            byte[] md5Bytes = md5.digest(byteArray);

            StringBuffer hexValue = new StringBuffer();

            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }

            return hexValue.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
    }
}
