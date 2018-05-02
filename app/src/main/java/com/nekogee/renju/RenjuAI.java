package com.nekogee.renju;

import android.graphics.Point;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by hui jie on 2018/4/26.
 */

public class RenjuAI {
    private int [][] chessBoard;
    private int size;//棋盘行或列数
    private boolean isWhite;//黑子先走，则false为先手

    //大概是一个没什么用的优化
    /*private int up = size+8;//
    private int left = size+8;
    private int right = 4;
    private int down = 4;*/

    private int [] dx = {0,1,1,1};//四个方向
    private int [] dy = {1,0,-1,1};
    private int [] power = {1,4,16,64,256,1024,4096,16384,65536};

    //存储特定棋型的评分
    private static Map<Integer,Integer> map = new HashMap<>();
    static {
        map.put(1,1000);
        map.put(2,500);
        map.put(5,2000);
        map.put(6,20);
        map.put(3,30);
        map.put(4,2000);
    }

    private Pair<Integer,Point> pair;
    private PriorityQueue<Pair<Integer,Point>> priorityQueue = new PriorityQueue<>(30, new Comparator<Pair<Integer, Point>>() {
        @Override
        public int compare(Pair<Integer, Point> o1, Pair<Integer, Point> o2) {
            if(o1.first < o2.first) {
                return 1;
            }
            else if(o1.first > o2.first) {
                return -1;
            }
            else {
                return 0;
            }
        }
    });

    private final int WHITE = 1;
    private final int BLACK = 2;
    private final int OUTSIDER = 3; //棋盘外的点的值

    /*
    private final int SCORE0 = 7;//搜9个
    private final int SCORE1 = 35;//o
    private final int SCORE2 = 800;//oo*/


    public RenjuAI(int size,boolean isWhite) {
        this.size = size;
        this.isWhite = isWhite;
        init();
    }

    public void init() {
        //空位默认初始化为0，棋盘外的为OUTSIDER
        chessBoard = new int[size+9][size+9];//？
        for(int i=0;i<4;i++) {
            for(int j=0;j<size+9;j++) {
                chessBoard[i][j] = OUTSIDER;
            }
        }
        for(int i=4;i<size+5;i++) {
            for (int j=0;j<4;j++) {
                chessBoard[i][j] = OUTSIDER;
            }
            for (int j=size+5;j<size+9;j++) {
                chessBoard[i][j] = OUTSIDER;
            }
        }
        for(int i=size+5;i<size+9;i++) {
            for (int j=0;j<size+9;j++) {
                chessBoard[i][j] = OUTSIDER;
            }
        }
        if(!isWhite) {
            chessBoard[size/2+4][size/2+4] = BLACK;
        }
    }

    public void placePiece(Point p) {
        if(!isWhite) {
            chessBoard[p.x+4][p.y+4] = WHITE;
        } else {
            chessBoard[p.x+4][p.y+4] = BLACK;
        }
        /*
        if(p.x+4>right) right = p.x+4;
        if(p.x+4<left) left = p.x+4;
        if(p.y+4>up) up = p.y+4;
        if(p.y+4<down) down = p.y+4;*/

        searchArea();
    }
    //对在有效范围内的点评分
    private void searchArea() {
        //for(int i=up-4;i<=down+4;i++) {
           // for(int j=left-4;j<=right+4;j++) {
        for(int i=4;i<=size+4;i++) {
            for(int j=4;j<=size+4;j++) {
                if(chessBoard[i][j]==0) {
                    Point p = new Point(i,j);
                    pair = new Pair<>(ratePoint(p),p);//给该点打分并存储位置信息
                    Log.d("neww", "rate "+ratePoint(p));
                    priorityQueue.add(pair);//放入优先队列维护
                }
            }
        }
        Log.d("neww", "search end");
    }
    //给该点评分
    private int ratePoint(Point p) {
        int sum = 0;
        for (int dir = 0 ;dir < 4;dir++) {
            sum += checkRow(p,dir);//可考虑其他计算sum的方法
        }
        return sum;
    }
    //匹配棋型
    private int checkRow(Point p,int dir) {
        int x = p.x;
        int y = p.y;
        int sum = 0;
        int count = 0;
        for(int i = x - 4 * dx[dir], j = y - 4 * dy[dir];(i <= x + 4 * dx[dir]) && (j <= y + 4 * dy[dir]);i += dx[dir],j += dy[dir]) {
            //转化为四进制的数比较
            sum += chessBoard[i][j] * power[count];
            count++;
        }
        //将获得的sum与特定棋型比较，返回特定棋型对应的评分
        return getPatternValue(sum);
    }
    //获得特定棋型的评分
    private int getPatternValue(int sum) {
        if (sum > 0 && sum < 7) {
            return map.get(sum);
        } else {
            return 0;
        }
    }

    public Point getPoint() {
        Log.d("neww", "size: "+priorityQueue.size());
        Point p = priorityQueue.peek().second;
        Log.d("neww", "value "+priorityQueue.peek().first);
        if(isWhite) {
            chessBoard[p.x][p.y] = WHITE;
        } else {
            chessBoard[p.x][p.y] = BLACK;
        }
        priorityQueue.clear();
        p.x -= 4;
        p.y -= 4;
        return p;
    }

}
