package chessComponent;

import controller.ClickController;
import model.ChessColor;
import model.ChessboardPoint;

import java.awt.*;

/**
 * 表示士棋子
 */
public class AdvisorChessComponent extends ChessComponent {
    public int ChessVal = 10;
    public int ChessType = 2;

    public AdvisorChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor,
            ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size);
        if (this.getChessColor() == ChessColor.RED) {
            name = "仕";
        } else {
            name = "士";
        }
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