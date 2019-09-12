package www.com.yuan2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

public class Serve { //主线程负责写消息
    public static void main(String[] args) {
        try {
            ArrayList<String> list = new ArrayList<>();
            HashMap<String, Socket> map = new HashMap<>();
            Properties properties = new Properties();
            ServerSocket socket = new ServerSocket(8888);
            System.out.println("服务等待..........");
            int i = 1;
            while (true) {
                Socket accept = socket.accept(); //监听
                System.out.println((i++) + "个用户已连接");
                //list.add(accept);
                InputStream in = accept.getInputStream();
                //OutputStream stream = accept.getOutputStream();
                //Scanner scanner = new Scanner(System.in);
                new SaveUser(map, accept,list,properties).start();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
