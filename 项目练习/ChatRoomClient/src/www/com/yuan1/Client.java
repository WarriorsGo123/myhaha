package www.com.yuan1;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client { //主线程负责写消息
    public static InputStream in;
    public static OutputStream out;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket("192.168.11.56", 8888);
            out = socket.getOutputStream();
            in = socket.getInputStream();
            Scanner scanner = new Scanner(System.in);

            //注册用户
                System.out.println("1.注册   2.登录");
                int i = scanner.nextInt();

                    switch (i) {
                        case 1:
                            System.out.println("请注册");
                            Resigter();
                            break;

                        case 2:
                            System.out.println("请登录");
                            Denglu();
                            break;
                    }

                //开启聊天线程
                ClientSon clientSon = new ClientSon(in);
                clientSon.start();


                //提供菜单选项
                boolean flag = true;
                while (flag) {
                    System.out.println("请选择模式：1.私聊 2.群聊 3.在线列表 4.下线 5.隐身/在线 7.查询聊天记录 -q退出当前模式");
                    int i1 = scanner.nextInt();
                    switch (i1) {
                        case 1: //私聊
                            PrivateTalk();
                            break;

                        case 2://公聊
                            PublicTalk();
                            break;

                        case 3: //获取列表
                            GetOnlineList();
                            break;

                        case 4://下线
                            //客户端关闭通道，停线程
                            //服务端关闭下线者通道，移除名字
                            Leave();
                            flag = false;
                            break;

                        case 5: //切换状态
                            SwitchFeel();
                            break;

                        case 6: //发送文件
                            SendFile();
                            break;
                        case 7: //查询聊天记录
                            FindRemember();
                            break;
                    }
                }

                clientSon.stop();
                out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Denglu() throws IOException {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入用户名");
            String s = scanner.nextLine();
            System.out.println("请输入密码");
            String s1 = scanner.nextLine();
            String s2 = s + ":" + s1+":"+MsgType.MSG_DENGLU;
            out.write(s2.getBytes());


            byte[] bytes = new byte[1024];
            int read = in.read(bytes);
            String s3 = new String(bytes, 0, read);
            if (s3.equals("no")) {
                System.out.println("请重新登录");
            } else if (s3.equals("yes")) {
                System.out.println("登录成功");
                break;

            }
        }
    }


    private static void Resigter() throws IOException {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入用户名");
            String s1 = scanner.nextLine();
            System.out.println("请输入密码");
            String s2 = scanner.nextLine();
            String s3 = s1 + ":" + s2 +":"+MsgType.MSG_RESIGTRE;
            out.write(s3.getBytes());

            byte[] bytes = new byte[1024];
            int read = in.read(bytes);
            String s = new String(bytes, 0, read);
            if (s.equals("no")) {
                System.out.println("请重新注册");
            } else if (s.equals("yes")) {
                System.out.println("注册成功");
                break;
            }
        }
    }

    private static void FindRemember() throws IOException {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("1 查看私聊记录   2 查看群聊记录");
            int i = scanner.nextInt();
            switch (i){
                case 1:
                    String s = "null"+":"+"null"+":"+MsgType.MSG_FINDPRIVATE;
                    out.write(s.getBytes());
                    break;

                case 2:
                    String s1 = "null"+":"+"null"+":"+MsgType.MSG_FINDPUBLIC;
                    out.write(s1.getBytes());
                    break;
            }
        }
    }
    private static void SendFile() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入目标");
        String s = scanner.nextLine();
        System.out.println("请输入文件路径");
        String s1 = scanner.nextLine();
        File file = new File(s1);
        String a = s+":"+ file.getName() +"$"+ file.length() + ":" + MsgType.MSG_SENDFILE;
        byte[] bytes = a.getBytes();
        byte[] bytes1 = new byte[1024*10-bytes.length];//空字节数组
        byte[] bytes2 = InputAndOutpuyUtil.readFile(s1);
        //ByteArrayOutputStream  内存操作流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(bytes);
        outputStream.write(bytes1);
        outputStream.write(bytes2);

        byte[] bytes3 = outputStream.toByteArray();
        out.write(bytes3);

    }

    private static void Leave() throws IOException {
        String s = "null" + ":" + "null" + ":" + MsgType.MSG_LEAVE;
        out.write(s.getBytes());

    }

    private static void SwitchFeel() throws IOException {
        String s = "null" + ":" + "null" + ":" + MsgType.MSG_SWITCH;
        out.write(s.getBytes());

    }

    private static void GetOnlineList() throws IOException {
        String s = "null" + ":" + "null" + ":" + MsgType.MSG_ONLINELIST;
        out.write(s.getBytes());
    }

    private static void PublicTalk() throws IOException {
        while (true) {
            System.out.println("已进入群聊聊模式 内容格式 接收者:消息内容:消息类型  -q退出模式");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();     //私聊格式 ：  接收者:消息内容:消息类型
            if ("-q".equals(s)) {
                break;
            } else {
                s = "null" + ":" + s + ":" + MsgType.MSG_PUBLIC;
                out.write(s.getBytes());
            }
        }
    }


    private static void PrivateTalk() throws IOException {
        while (true) {
            System.out.println("已进入私聊模式 内容格式 接收者:消息内容:消息类型  -q退出模式");
            Scanner scanner = new Scanner(System.in);
            String s = scanner.nextLine();     //私聊格式 ：  接收者:消息内容:消息类型
            if ("-q".equals(s)) {
                break;
            } else {
                s = s + ":" + MsgType.MSG_PRIVATE;
                out.write(s.getBytes());
            }
        }
    }
}

