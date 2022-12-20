package chessComponent;

import controller.ClickController;
import model.ChessColor;
import model.ChessboardPoint;

import java.awt.*;

/**
 * 表示象棋子
 */
public class MinisterChessComponent extends ChessComponent {

    public int ChessVal = 5;
    public int ChessType = 3;

    public MinisterChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor,
            ClickController clickController, int size) {
        super(chessboardPoint, location, chessColor, clickController, size);
        if (this.getChessColor() == ChessColor.RED) {
            name = "相";
        } else {
            name = "象";
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