package chessComponent;

import controller.ClickController;
import model.ChessColor;
import model.ChessboardPoint;

import java.awt.*;

/**
 * 表示黑红车
 */
public class ChariotChessComponent extends ChessComponent {

    public int ChessVal = 5;
    public int ChessType = 4;

    public ChariotChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor,
            ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size);
        if (this.getChessColor() == ChessColor.RED) {
            name = "俥";
        } else {
            name = "車";
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
