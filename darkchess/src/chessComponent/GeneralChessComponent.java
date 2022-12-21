package chessComponent;

import controller.ClickController;
import model.ChessColor;
import model.ChessboardPoint;

import java.awt.*;

/**
 * 表示帅棋子
 */
public class GeneralChessComponent extends ChessComponent {

    public int ChessVal = 30;
    public int ChessType = 1;

    public GeneralChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor,
            ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size);
        if (this.getChessColor() == ChessColor.RED) {
            name = "帅";
        } else {
            name = "将";
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
