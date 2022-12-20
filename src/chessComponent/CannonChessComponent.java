package chessComponent;

import controller.ClickController;
import model.ChessColor;
import model.ChessboardPoint;

import java.awt.*;

/**
 * 表示炮棋子
 */
public class CannonChessComponent extends ChessComponent {
    public int ChessVal = 5;
    public int ChessType = 7;

    public CannonChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor,
            ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size);
        if (this.getChessColor() == ChessColor.RED) {
            name = "炮";
        } else {
            name = "狍";
        }
    }

    @Override
    public boolean canMoveTo(SquareComponent[][] chessboard, ChessboardPoint destination) {
        if (chessboard[destination.getX()][destination.getY()] instanceof EmptySlotComponent)
            return false;
        if (destination.getX() != this.chessboardPoint.getX() && destination.getY() != this.chessboardPoint.getY())
            return false;
        if (destination.getX() == this.chessboardPoint.getX()) {
            if (destination.getY() < this.chessboardPoint.getY()) {
                int cnt = 0;
                for (int i = this.chessboardPoint.getY() - 1; i > destination.getY(); i--) {
                    if (!(chessboard[destination.getX()][i] instanceof EmptySlotComponent))
                        cnt++;
                }
                if (cnt == 1)
                    return true;
                else
                    return false;
            } else {
                int cnt = 0;
                for (int i = this.chessboardPoint.getY() + 1; i < destination.getY(); i++) {
                    if (!(chessboard[destination.getX()][i] instanceof EmptySlotComponent))
                        cnt++;
                }
                if (cnt == 1)
                    return true;
                else
                    return false;
            }
        } else {
            if (destination.getX() < this.chessboardPoint.getX()) {
                int cnt = 0;
                for (int i = this.chessboardPoint.getX() - 1; i > destination.getX(); i--) {
                    if (!(chessboard[i][destination.getY()] instanceof EmptySlotComponent))
                        cnt++;
                }
                if (cnt == 1)
                    return true;
                else
                    return false;
            } else {
                int cnt = 0;
                for (int i = this.chessboardPoint.getX() + 1; i < destination.getX(); i++) {
                    if (!(chessboard[i][destination.getY()] instanceof EmptySlotComponent))
                        cnt++;
                }
                if (cnt == 1)
                    return true;
                else
                    return false;
            }
        }
        // todo: complete this method
    }

    @Override
    public int getType() {
        return ChessType;
    }

    @Override
    public int getVal() {
        return ChessVal;
    }
}
