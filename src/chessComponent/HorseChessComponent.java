package chessComponent;

import controller.ClickController;
import model.ChessColor;
import model.ChessboardPoint;

import java.awt.*;

/**
 * 表示马棋子
 */
public class HorseChessComponent extends ChessComponent {

    public int ChessVal = 5;
    public int ChessType = 5;

    public HorseChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor,
            ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size);
        if (this.getChessColor() == ChessColor.RED) {
            name = "码";
        } else {
            name = "马";
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
