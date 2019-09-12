package www.com.yuan2;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

public class SaveUser extends Thread {
    HashMap<String, Socket> map;
    Socket accept;
    String username;
    ArrayList<String> list;
    Properties properties;


    public SaveUser(HashMap<String, Socket> map, Socket accept, ArrayList<String> list, Properties properties) {
        this.map = map;
        this.accept = accept;
        this.list = list;
        this.properties= properties;
    }

    @Override
    public void run() {
        try {
            while (true) {
                InputStream in = accept.getInputStream();
                OutputStream out = accept.getOutputStream();
                //读取注册用户名
                byte[] bytes = new byte[1024];
                int read = in.read(bytes);
                String sumUsername = new String(bytes, 0, read);
                String[] split = sumUsername.split(":");
                this.username = split[0];
                String password = split[1];
                int i = Integer.parseInt(split[2]);
                if (i == MsgType.MSG_RESIGTRE) {
                    File file = new File("E://a.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    //properties = new Properties();
                    properties.load(new FileReader(file));
                    String property = properties.getProperty(username, "null");

                    if (property.equals("null")) {
                        map.put(username, accept);
                        list.add(username);
                        properties.setProperty(username, password);
                        properties.store(new FileOutputStream("E://a.txt"), "用户名与密码");

                        //反馈给客户端
                        out.write("yes".getBytes());
                        break;
                    } else {
                        out.write("no".getBytes());
                    }
                }else if(i==MsgType.MSG_DENGLU){
                    File file = new File("E://a.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    //properties = new Properties();
                    properties.load(new FileReader(file));
                    String property = properties.getProperty(username);
                    if (password.equals(property)) {
                        out.write("yes".getBytes());
                        map.put(username, accept);
                        list.add(username);
                        break;
                    } else {
                        out.write("no".getBytes());
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //上线提醒
        try {
            Set<String> strings = map.keySet();
            for (String string : strings) {
                if (string.equals(username)) {
                    continue;
                }
                    Socket socket = map.get(string);
                    OutputStream out1 = socket.getOutputStream();
                    out1.write((username + ":"+"上线了"+":"+MsgType.MSG_ONLINE+":"+System.currentTimeMillis()).getBytes());//转发格式  发送者:内容:消息类型:时间
                }


                //开启服务端聊天线程
            new ServeSon(map,accept,username,list).start();



        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
