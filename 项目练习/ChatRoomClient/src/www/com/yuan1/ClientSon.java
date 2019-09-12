package www.com.yuan1;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ClientSon extends Thread { //子线程负责读消息
    InputStream in;
    Properties properties;

    public ClientSon(InputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] bytes = new byte[1024];
                int read = in.read(bytes);
                String s = new String(bytes, 0, read);
                String[] divide = s.split(":");
                String sender = divide[0];
                String message = divide[1];
                int mesTypy = Integer.parseInt(divide[2]);
                long l = Long.parseLong(divide[3]);
                Date date = new Date(l);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(date);
                if (mesTypy == MsgType.MSG_PRIVATE) {
                    System.out.println(time);
                    System.out.println(sender + ":" + "对你说" + " " + message);
                    File file = new File("E://b.txt");
                    properties = new Properties();
                    properties.setProperty(sender, message);
                    properties.store(new FileOutputStream("E://b.txt",true),"私聊记录");

                } else if (mesTypy == MsgType.MSG_PUBLIC) {
                    System.out.println(time);
                    System.out.println(sender + ":" + "对大家说" + " " + message);
                    File file = new File("E://c.txt");
                    properties = new Properties();
                    properties.setProperty(sender, message);
                    properties.store(new FileOutputStream("E://c.txt",true),"群聊记录");


                } else if (mesTypy == MsgType.MSG_ONLINE) {
                    System.out.println(time);
                    System.out.println(sender + ":" + message);

                } else if (mesTypy == MsgType.MSG_ONLINELIST) {
                    System.out.println(time);
                    System.out.println(message);
                } else if (mesTypy == MsgType.MSG_LEAVE) {
                    System.out.println(time);
                    System.out.println(sender + ":" + message);
                } else if (mesTypy == MsgType.MSG_SWITCH) {
                    System.out.println(time);
                    System.out.println(sender + ":" + message);
                }else if(mesTypy == MsgType.MSG_SENDFILE) {
                    System.out.println(time);
                    String[] mesg = message.split("$");
                    String fileName = mesg[0];
                    long fileLens = Long.parseLong(mesg[1]);
                    System.out.println(sender + "给你发来一个文件-" + fileName + " 大小" + fileLens / 1024);
                }else if(mesTypy == MsgType.MSG_FINDPRIVATE){
                    System.out.println(time);
                    properties.load(new FileReader("E://b.txt"));
                    System.out.println(properties);

                }else if(mesTypy == MsgType.MSG_FINDPUBLIC){
                    System.out.println(time);
                    properties.load(new FileReader("E://c.txt"));
                    System.out.println(properties);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}