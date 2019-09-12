package www.com.yuan2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ServeSon extends Thread {  //子线程负责读消息

    //ArrayList<Socket> list;
    HashMap<String, Socket> map;
    Socket accept;
    String username;
    boolean isHidden = true;
    ArrayList<String> list;

    public ServeSon(HashMap<String, Socket> map, Socket accept, String username,ArrayList<String> list) {
        this.map = map;
        this.accept = accept;
        this.username = username;
        this.list = list;
    }

    @Override
    public void run() {
        try {
            InputStream in = accept.getInputStream();
            OutputStream out = accept.getOutputStream();
            while (true) {

                byte[] bytes = new byte[1024];
                int read = in.read(bytes);
                String s = new String(bytes, 0, read); //转发格式  发送者:内容:消息类型:时间
                String[] divide = s.split(":");
                String receiver = divide[0];
                String message = divide[1];
                int mesTypy = Integer.parseInt(divide[2]);
                if (mesTypy == MsgType.MSG_PRIVATE) {
                    String reply = username + ":" + message + ":" + mesTypy + ":" + System.currentTimeMillis();
                    Socket socket = map.get(receiver);
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(reply.getBytes());

                } else if (mesTypy == MsgType.MSG_PUBLIC) {
                    Set<String> strings = map.keySet();
                    for (String string : strings) {
                        if (string.equals(username)) {
                            continue;
                        }
                        Socket socket = map.get(string);
                        String reply1 = username + ":" + message + ":" + mesTypy + ":" + System.currentTimeMillis();
                        OutputStream out1 = socket.getOutputStream();
                        out1.write(reply1.getBytes());
                    }
                } else if (mesTypy == MsgType.MSG_ONLINELIST) {
                    //Set<String> strings = map.keySet();
                    StringBuffer buffer = new StringBuffer();
                    int i = 1;
                    for (String string : list) {
                        if (string.equals(username)) {
                            continue;
                        }
                        buffer.append(i++).append(".").append(string).append("\n");
                    }
                    String reply2 = username + ":" + buffer.toString() + ":" + mesTypy + ":" + System.currentTimeMillis();
                    Socket socket = map.get(username);
                    OutputStream out2 = socket.getOutputStream();
                    out2.write(reply2.getBytes());

                } else if (mesTypy == MsgType.MSG_LEAVE) {
                    Set<String> strings = map.keySet();
                    for (String string : strings) {
                        if (string.equals(username)) {
                            continue;
                        }
                        Socket socket = map.get(string);
                        OutputStream out1 = socket.getOutputStream();
                        out1.write((username + ":" + "下线了" + ":" + mesTypy + ":" + System.currentTimeMillis()).getBytes());//转发格式  发送者:内容:消息类型:时间
                    }
                    break;
                } else if (mesTypy == MsgType.MSG_SWITCH) {
                    if (isHidden) { //在线----隐身

                        list.remove(username);

                    } else {
                        //隐身------在线
                        list.add(username);
                        Set<String> strings = map.keySet();
                        for (String string : strings) {
                            if (string.equals(username)) {
                                continue;
                            }
                            Socket socket = map.get(string);
                            OutputStream out1 = socket.getOutputStream();
                            out1.write((username + ":" + "回归了" + ":" + mesTypy + ":" + System.currentTimeMillis()).getBytes());//转发格式  发送者:内容:消息类型:时间
                        }
                    }
                    isHidden = !isHidden; //开关

                } else if (mesTypy == MsgType.MSG_SENDFILE) { //#########
                    String[] mesg = message.split("$");
                    String name = mesg[0];
                    long l = Long.parseLong(mesg[1]);
                    String zf = username + ":" + message + ":" + mesTypy + ":" + System.currentTimeMillis();
                    byte[] bytes1 = zf.getBytes();
                    byte[] bytes2 = new byte[1024 * 10 - bytes1.length];//空字节数组

                    ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                    int len = 0;
                    byte[] bytes3 = new byte[1024 * 8];
                    while (true) {
                        int len1 = in.read(bytes3);
                        outputStream1.write(bytes3, 0, len1);
                        len += len1;
                        if (len == l) {
                            break;
                        }

                    }

                    //把文件字节数组取出来
                    byte[] fileBytes = outputStream1.toByteArray();
                    outputStream1.reset();//重置一下

                    outputStream1.write(bytes1);
                    outputStream1.write(bytes2);
                    outputStream1.write(fileBytes);
                    //取出大的字节数组
                    byte[] allBytes = outputStream1.toByteArray();
                    //转发回去
                    map.get(receiver).getOutputStream().write(allBytes);


                }else if(mesTypy==MsgType.MSG_FINDPRIVATE) {
                    String notes = username + ":"+ message + ":"+ mesTypy + ":" + System.currentTimeMillis();
                    out.write(notes.getBytes());
                }else if(mesTypy==MsgType.MSG_FINDPUBLIC){
                    String notes = username + ":"+ message + ":"+ mesTypy + ":" + System.currentTimeMillis();
                    out.write(notes.getBytes());
                }
            }

           map.get(username).close();
            map.remove(username);
           list.remove(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
// Socket socket = list.get(Integer.parseInt(divide[0]));
// OutputStream outputStream = socket.getOutputStream();
// outputStream.write((divide[2] + ":" + "对你说" + ":" + divide[1]).getBytes());
// System.out.println(s);