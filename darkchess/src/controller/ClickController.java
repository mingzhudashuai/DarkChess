package controller;

import chessComponent.SquareComponent;

import java.io.IOException;

import chessComponent.CannonChessComponent;
import chessComponent.EmptySlotComponent;
import model.ChessColor;
import view.ChessGameFrame;
import view.Chessboard;

public class ClickController {
    private final Chessboard chessboard;
    private SquareComponent first;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(SquareComponent squareComponent) {
        if (first == null && chessboard.getCurrentColor() == ChessColor.NONE) {
            chessboard.setCurrentColor(squareComponent.getChessColor());
            if (handleFirst(squareComponent)) {
                squareComponent.setSelected(true);
                first = squareComponent;
                first.repaint();
            }
            return;
        }
        // 判断第一次点击
        if (first == null) {
            if (handleFirst(squareComponent)) {
                squareComponent.setSelected(true);
                first = squareComponent;
                first.repaint();
            }
        } else {
            if (first == squareComponent) { // 再次点击取消选取
                squareComponent.setSelected(false);
                SquareComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(squareComponent)) {
                // repaint in swap chess method.
                chessboard.swapChessComponents(first, squareComponent);
                chessboard.clickController.swapPlayer();

                first.setSelected(false);
                first = null;
            }
        }
    }

    /**
     * @param squareComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(SquareComponent squareComponent) {
        if (!squareComponent.isReversal()) {
            squareComponent.setReversal(true);
            System.out.printf("onClick to reverse a chess [%d,%d]\n", squareComponent.getChessboardPoint().getX(),
                    squareComponent.getChessboardPoint().getY());
            squareComponent.repaint();
            chessboard.clickController.swapPlayer();
            return false;
        }
        return squareComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param squareComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(SquareComponent squareComponent) {

        // 没翻开或空棋子，进入if
        if (!squareComponent.isReversal()) {
            // 炮单独处理
            SquareComponent[][] cb = chessboard.getChessComponents();
            if (cb[first.chessboardPoint.getX()][first.chessboardPoint.getY()] instanceof CannonChessComponent) {
                if (!(squareComponent instanceof EmptySlotComponent)) {
                    return first.canMoveTo(chessboard.getChessComponents(), squareComponent.getChessboardPoint());
                } else
                    return false;
            }
            // 没翻开且非空棋子不能走
            if (!(squareComponent instanceof EmptySlotComponent)) {
                return false;
            }
        }
        return squareComponent.getChessColor() != chessboard.getCurrentColor() &&
                first.canMoveTo(chessboard.getChessComponents(), squareComponent.getChessboardPoint());
    }

    public void swapPlayer() {
        chessboard
                .setCurrentColor(chessboard.getCurrentColor() == ChessColor.BLACK ? ChessColor.RED : ChessColor.BLACK);
        ChessGameFrame.getStatusLabel().setText(String.format("%s's TURN", chessboard.getCurrentColor().getName()));
        ChessGameFrame.getStatusScore()
                .setText(String.format("Black %d : Red %d", chessboard.BlackSum, chessboard.RedSum));
        if (chessboard.RedSum >= 60) {
            ChessGameFrame.getStatusScore().setText(String.format("Red WIN!!!"));
            ChessGameFrame.reButton.doClick();
        } else if (chessboard.BlackSum >= 60) {
            ChessGameFrame.getStatusScore().setText(String.format("Black WIN!!!"));
            ChessGameFrame.reButton.doClick();
        }
        try {
            ChessGameFrame.OnceChange();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void enter(SquareComponent squareComponent) {
        squareComponent.setReversal(true);
        squareComponent.repaint();
    }

    public void level(SquareComponent squareComponent) {
        squareComponent.setReversal(false);
        squareComponent.repaint();
    }
}
