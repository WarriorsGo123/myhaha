package www.com.yuan1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class InputAndOutpuyUtil {
    public static byte[] readFile(String path){
        File file = new File(path);
        byte datas[] = null;
        if(!file.exists()){
            datas = null;
        }else{
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                FileInputStream fis = new FileInputStream(file);
                byte data[] = new byte[1024*1024];
                int len = 0;
                while((len = fis.read(data))>0){
                    baos.write(data, 0, len);
                }
                datas = baos.toByteArray();
                baos.flush();
                baos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return datas;
    }

    public static boolean writeFile(String path,byte datas[]){
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(datas);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

