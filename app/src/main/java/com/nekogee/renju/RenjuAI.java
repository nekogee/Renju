package com.nekogee.renju;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by hui jie on 2018/4/26.
 */

public class RenjuAI {
    private int [][] chessBoard;
    private int size;//棋盘行或列数
    private boolean isWhite;//黑子先走，则false为先手

    private final int WHITE = 1000;
    private final int BLACK = -100;
    private int tmp;

    public RenjuAI(int size,boolean isWhite) {
        this.size = size;
        this.isWhite = isWhite;
        init();
    }

    public void init() {
        chessBoard = new int[size+1][size+1];//空位默认初始化为0
        tmp = 1;
    }

    public void placePiece(Point p) {
        if(!isWhite) {
            chessBoard[p.x][p.y] = WHITE;
        } else {
            chessBoard[p.x][p.y] = BLACK;
        }
    }

    public Point getPoint() {
        Point p = new Point(tmp,tmp);
        tmp++;
        return p;
    }

}
