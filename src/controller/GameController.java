package controller;

import view.Chessboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 这个类主要完成由窗体上组件触发的动作。
 * 例如点击button等
 * ChessGameFrame中组件调用本类的对象，在本类中的方法里完成逻辑运算，将运算的结果传递至chessboard中绘制
 */
public class GameController {
    private Chessboard chessboard;

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    private int check(List<String> x, String path) {
        if (!path.contains("txt")) {
            return 101;
        }
        if (x.size() != 34)
            return 102;
        int[] red = new int[8];
        int[] black = new int[8];
        for (int i = 2; i < x.size(); i++) {
            var mid = x.get(i).split(" ");
            int type = Integer.valueOf(mid[2]);
            if (type > 7 || type < 0)
                return 103;
            if (mid[0].equals("false")) {
                if (mid[1].equals("RED")) {
                    red[type]++;
                } else {
                    black[type]++;
                }
            }
        }
        if (red[1] <= 1 && red[2] <= 2 && red[3] <= 2 && red[4] <= 2 && red[5] <= 2 && red[6] <= 5 && red[7] <= 2) {

        } else {
            return 103;
        }
        red = black;
        if (red[1] <= 1 && red[2] <= 2 && red[3] <= 2 && red[4] <= 2 && red[5] <= 2 && red[6] <= 5 && red[7] <= 2) {

        } else {
            return 103;
        }
        if (x.get(0).equals("0") || x.get(0).equals("1") || x.get(0).equals("-1")) {

        } else {
            return 104;
        }
        return 0;
    }

    public int loadGameFromFile(String path) {
        int res = 0;
        try {
            List<String> chessData = Files.readAllLines(Path.of(path));
            res = check(chessData, path);
            if (res != 0)
                return res;
            chessboard.loadGame(chessData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void saveGameToFile(String path) {
        try {
            FileWriter out = new FileWriter(path);
            List<String> res = chessboard.saveGame();
            for (var item : res) {
                out.append(item);
                out.append("\n");
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
