package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import view.Chessboard;

public class NetController {
    ServerSocket server;
    private Chessboard chessboard;
    public OutputStream outputStream = null;

    public NetController(Chessboard chessboard) throws IOException {
        this.chessboard = chessboard;
        int port = (int) (Math.random() * 2000);
        server = new ServerSocket(port);
        System.out.println("port:" + port);
        new Thread(() -> {
            try {
                // 监听端口（堵塞，等待他人连接）
                while (true) {
                    Socket socket = server.accept();
                    outputStream = socket.getOutputStream();
                    System.out.println("con!!!!!");
                    // 获取收到的资源
                    InputStream inputStream = socket.getInputStream();
                    byte[] buf = new byte[1024 * 1024];
                    int len;
                    while ((len = inputStream.read(buf)) != -1) {
                        var mid = new String(buf, 0, len);
                        // System.out.println(mid);
                        ArrayList<String> data = new ArrayList<>();
                        for (int i = 0; i < mid.length(); i++) {
                            int ll = 0;
                            for (int j = i; j < mid.length(); j++) {
                                if (mid.charAt(j) == '\n') {
                                    data.add(mid.substring(i, i + ll));
                                    break;
                                } else {
                                    ll++;
                                }
                            }
                            i += ll;
                        }
                        chessboard.loadGame(data);
                    }
                    inputStream.close();
                    socket.close();
                    outputStream = null;
                }
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("net init falied!!!");
            }
        }).start();
    }

    public void connetOther(String path) throws IOException {
        var iperf = path.split(":");
        InetSocketAddress other = new InetSocketAddress(InetAddress.getByName(iperf[0]), Integer.valueOf(iperf[1]));
        Socket socket = new Socket();
        // 使用socket进行连接（套接字：IP + 端口号），三次握手底层已帮我们实现
        socket.connect(other);
        outputStream = socket.getOutputStream();
        // 发送消息
        new Thread(() -> {
            InputStream inputStream;
            try {
                inputStream = socket.getInputStream();
                byte[] buf = new byte[1024 * 1024];
                int len;
                while ((len = inputStream.read(buf)) != -1) {
                    var mid = new String(buf, 0, len);
                    ArrayList<String> data = new ArrayList<>();
                    for (int i = 0; i < mid.length(); i++) {
                        int ll = 0;
                        for (int j = i; j < mid.length(); j++) {
                            if (mid.charAt(j) == '\n') {
                                data.add(mid.substring(i, i + ll));
                                break;
                            } else {
                                ll++;
                            }
                        }
                        i += ll;
                    }
                    chessboard.loadGame(data);
                }
                inputStream.close();
                socket.close();
                outputStream = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                outputStream = null;
                e.printStackTrace();
            }
        }).start();
    }

    public void change() throws IOException {
        if (outputStream == null)
            return;
        var val = chessboard.saveGame();
        StringBuffer c = new StringBuffer();
        for (var item : val) {
            c.append(item);
            c.append("\n");
        }
        outputStream.write(c.toString().getBytes());
    }
}
