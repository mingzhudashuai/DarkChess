package view;

import chessComponent.*;
import model.*;
import controller.ClickController;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 这个类表示棋盘组建，其包含：
 * SquareComponent[][]: 4*8个方块格子组件
 */
public class Chessboard extends JComponent {

    private static final int ROW_SIZE = 8;
    private static final int COL_SIZE = 4;

    private final SquareComponent[][] squareComponents = new SquareComponent[ROW_SIZE][COL_SIZE];
    // todo: you can change the initial player
    private ChessColor currentColor = ChessColor.NONE;

    // all chessComponents in this chessboard are shared only one model controller
    public final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;
    public int RedSum = 0, BlackSum = 0;
    public static boolean cheatMode = false;

    public Chessboard(int width, int height) {
        setLayout(null); // Use absolute layout.
        setSize(width + 2, height);
        CHESS_SIZE = (height - 6) / 8;
        SquareComponent.setSpacingLength(CHESS_SIZE / 12);
        System.out.printf("chessboard [%d * %d], chess size = %d\n", width, height, CHESS_SIZE);

        initAllChessOnBoard();
    }

    public SquareComponent[][] getChessComponents() {
        return squareComponents;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(ChessColor currentColor) {
        this.currentColor = currentColor;
    }

    /**
     * 将SquareComponent 放置在 ChessBoard上。里面包含移除原有的component及放置新的component
     * 
     * @param squareComponent
     */
    public void putChessOnBoard(SquareComponent squareComponent) {
        int row = squareComponent.getChessboardPoint().getX(), col = squareComponent.getChessboardPoint().getY();
        if (squareComponents[row][col] != null) {
            remove(squareComponents[row][col]);
        }
        add(squareComponents[row][col] = squareComponent);
    }

    /**
     * 交换chess1 chess2的位置
     * 
     * @param chess1
     * @param chess2
     */
    public void swapChessComponents(SquareComponent chess1, SquareComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        if (!(chess2 instanceof EmptySlotComponent)) {
            if (currentColor == ChessColor.BLACK) {
                BlackSum += chess2.isReversal() ? chess2.getVal() : 0;
            } else {
                RedSum += chess2.isReversal() ? chess2.getVal() : 0;
            }
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController,
                    CHESS_SIZE));
        }
        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        squareComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        squareComponents[row2][col2] = chess2;

        // 只重新绘制chess1 chess2，其他不变
        chess1.repaint();
        chess2.repaint();
    }

    /**
     * 将棋盘所有的棋子的类型进行初始化
     * 
     * @return 返回所有的棋子的类型的数组，以便随机化打乱
     */
    private ArrayList<Integer> initChessType() {
        ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            data.add(1);
            data.add(-1);
        }
        for (int i = 0; i < 2; i++) {
            data.add(2);
            data.add(3);
            data.add(4);
            data.add(5);
            data.add(7);
            data.add(-2);
            data.add(-3);
            data.add(-4);
            data.add(-5);
            data.add(-7);
        }
        for (int i = 0; i < 5; i++) {
            data.add(6);
            data.add(-6);
        }
        return data;
    }

    // FIXME: Initialize chessboard for testing only.
    private void initAllChessOnBoard() {
        ArrayList<Integer> data = initChessType();
        Random random = new Random();
        for (int i = 0; i < squareComponents.length; i++) {
            for (int j = 0; j < squareComponents[i].length; j++) {
                int index = random.nextInt(data.size());
                ChessColor color = data.get(index) < 0 ? ChessColor.RED : ChessColor.BLACK;
                SquareComponent squareComponent;
                if (data.get(index) == 1 || data.get(index) == -1) {
                    squareComponent = new GeneralChessComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color,
                            clickController, CHESS_SIZE);
                } else if (data.get(index) == 2 || data.get(index) == -2) {
                    squareComponent = new AdvisorChessComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color,
                            clickController, CHESS_SIZE);
                } else if (data.get(index) == 3 || data.get(index) == -3) {
                    squareComponent = new MinisterChessComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color,
                            clickController, CHESS_SIZE);
                } else if (data.get(index) == 4 || data.get(index) == -4) {
                    squareComponent = new ChariotChessComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color,
                            clickController, CHESS_SIZE);
                } else if (data.get(index) == 5 || data.get(index) == -5) {
                    squareComponent = new HorseChessComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color,
                            clickController, CHESS_SIZE);
                } else if (data.get(index) == 6 || data.get(index) == -6) {
                    squareComponent = new SoldierChessComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color,
                            clickController, CHESS_SIZE);
                } else {
                    squareComponent = new CannonChessComponent(new ChessboardPoint(i, j), calculatePoint(i, j), color,
                            clickController, CHESS_SIZE);
                }
                data.remove(index);
                squareComponent.setVisible(true);
                putChessOnBoard(squareComponent);
            }
        }

    }

    /**
     * 绘制棋盘格子
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * 将棋盘上行列坐标映射成Swing组件的Point
     * 
     * @param row 棋盘上的行
     * @param col 棋盘上的列
     * @return
     */
    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE + 3, row * CHESS_SIZE + 3);
    }

    /**
     * 通过GameController调用该方法
     * 
     * @param chessData
     */
    public void loadGame(List<String> chessData) {
        int index = 0;
        if (chessData.get(index).equals("-1")) {
            currentColor = ChessColor.BLACK;
        } else if (chessData.get(index).equals("1")) {
            currentColor = ChessColor.RED;
        } else {
            currentColor = ChessColor.NONE;
        }
        index++;
        var sum = chessData.get(index).split(" ");
        index++;
        BlackSum = Integer.valueOf(sum[0]);
        RedSum = Integer.valueOf(sum[1]);
        for (int i = 0; i < squareComponents.length; i++) {
            for (int j = 0; j < squareComponents[0].length; j++) {
                remove(squareComponents[i][j]);
                var mid = chessData.get(index).split(" ");
                if (mid[0].equals("true")) {
                    squareComponents[i][j] = new EmptySlotComponent(squareComponents[i][j].getChessboardPoint(),
                            squareComponents[i][j].getLocation(), clickController,
                            CHESS_SIZE);
                } else {
                    ChessColor color = mid[1].equals("RED") ? ChessColor.RED : ChessColor.BLACK;
                    int type = Integer.valueOf(mid[2]);
                    if (type == 1) {
                        squareComponents[i][j] = new GeneralChessComponent(new ChessboardPoint(i, j),
                                calculatePoint(i, j), color,
                                clickController, CHESS_SIZE);
                    } else if (type == 2) {
                        squareComponents[i][j] = new AdvisorChessComponent(new ChessboardPoint(i, j),
                                calculatePoint(i, j), color,
                                clickController, CHESS_SIZE);
                    } else if (type == 3) {
                        squareComponents[i][j] = new MinisterChessComponent(new ChessboardPoint(i, j),
                                calculatePoint(i, j), color,
                                clickController, CHESS_SIZE);
                    } else if (type == 4) {
                        squareComponents[i][j] = new ChariotChessComponent(new ChessboardPoint(i, j),
                                calculatePoint(i, j), color,
                                clickController, CHESS_SIZE);
                    } else if (type == 5) {
                        squareComponents[i][j] = new HorseChessComponent(new ChessboardPoint(i, j),
                                calculatePoint(i, j), color,
                                clickController, CHESS_SIZE);
                    } else if (type == 6) {
                        squareComponents[i][j] = new SoldierChessComponent(new ChessboardPoint(i, j),
                                calculatePoint(i, j), color,
                                clickController, CHESS_SIZE);
                    } else {
                        squareComponents[i][j] = new CannonChessComponent(new ChessboardPoint(i, j),
                                calculatePoint(i, j), color,
                                clickController, CHESS_SIZE);
                    }
                }
                if (mid[3].equals("true")) {
                    squareComponents[i][j].setReversal(true);
                } else {
                    squareComponents[i][j].setReversal(false);
                }
                add(squareComponents[i][j]);
                index++;
            }
        }
        repaint();
        ChessGameFrame.getStatusLabel().setText(String.format("%s's TURN", this.getCurrentColor().getName()));
        ChessGameFrame.getStatusScore().setText(String.format("Black %d : Red %d", this.BlackSum, this.RedSum));
    }

    /**
     * 通过GameController调用该方法
     * 
     * @param chessData
     */
    public List<String> saveGame() {
        ArrayList<String> res = new ArrayList<>();
        if (currentColor == ChessColor.BLACK) {
            res.add("-1");
        } else if (currentColor == ChessColor.RED) {
            res.add("1");
        } else {
            res.add("0");
        }
        res.add(BlackSum + " " + RedSum);
        for (int i = 0; i < squareComponents.length; i++) {
            for (int j = 0; j < squareComponents[0].length; j++) {
                var mid = squareComponents[i][j];
                res.add((mid instanceof EmptySlotComponent) + " " + mid.getChessColor() + " "
                        + mid.getType() + " " + mid.isReversal() + " " + mid.isSelected());
            }
        }
        return res;
    }
}
