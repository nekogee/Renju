package com.nekogee.renju;

/**
 * Created by hui jie on 2018/4/15.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class Chess_Panel extends View{
    private int myPanelWidth ;        //棋盘宽度
    private float myLineHeight;    //行宽

    private Paint myPaint;         //画笔
    private Bitmap myWhitePiece;    //白棋子
    private Bitmap myBlackPiece;    //黑棋子
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;  //棋子为行宽的3/4；

    private Canvas canvas;

    private RenjuAI renjuAI;

    private boolean isGameOver;        //游戏结束
    public static int WHITE_WIN = 0;  //胜利为白方标志
    public static int BLACK_WIN = 1;  //胜利为黑方标志
    public boolean isWhite = true;  //判断AI是否为白棋（即后手）

    private List<Point> myWhiteArray = new ArrayList<Point>();  //白棋子位置信息
    public List<Point> myBlackArray = new ArrayList<Point>();  //黑棋子位置信息

    private onGameListener onGameListener;  //回调接口
    private int mUnder;        //dialog的Y坐标

    public Chess_Panel(Context context) {
        this(context, null);
    }

    public Chess_Panel(Context context ,AttributeSet attributeSet){            //构造函数
        super(context , attributeSet);

        init();
    }

    // 用于回调的接口
    public interface onGameListener {
        void onGameOver(int i);
    }

    //自定义接口，用于显示dialog
    public void setOnGameListener(Chess_Panel.onGameListener onGameListener) {
        this.onGameListener = onGameListener;
    }

    //初始化函数
    private void init() {
        myPaint = new Paint();
        myPaint.setColor(0Xffff0000);     //给画笔设置颜色
        myPaint.setAntiAlias(true);      //设置画笔是否使用抗锯齿
        myPaint.setDither(true);            //设置画笔是否防抖动
        myPaint.setStyle(Paint.Style.STROKE);        //设置画笔样式

        myWhitePiece = BitmapFactory.decodeResource(getResources(),R.drawable.white); //设置棋子图片
        myBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.black);
        renjuAI = new RenjuAI(10,isWhite);

    }

    public void update() {
        invalidate();
        Toast.makeText(getContext(),"update",Toast.LENGTH_SHORT);
    }
    //触发事件
    public boolean onTouchEvent(MotionEvent event){
        if (isGameOver) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getVaLidPoint(x,y);

            if (myWhiteArray.contains(p)|| myBlackArray.contains(p)) {
                return false;
            }

            renjuAI.placePiece(p);
            Point AI_Result = renjuAI.getPoint();//AI得出的下棋的位置

            if (!isWhite) {
                myWhiteArray.add(p);
                myBlackArray.add(AI_Result);
            }else {
                myBlackArray.add(p);
                myWhiteArray.add(AI_Result);
            }
            invalidate();
        }
        return true;
    }


    private Point getVaLidPoint(int x , int y){
        return new Point((int)(x/myLineHeight),(int)(y/myLineHeight));
    }

    //计算布局大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {            //MeasureSpec.UNSPECIFIED表示未知大小
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);

    }

    protected void onSizeChanged(int width, int height ,int old_width , int old_height) {         //当View大小发生改变的时候会被系统自动回调
        super.onSizeChanged(width, height, old_width, old_height);
        myPanelWidth = width;
        myLineHeight = myPanelWidth * 0.095f ;
        //myLineHeight = myPanelWidth*1.0f/maxLine;
        mUnder = height - (height - myPanelWidth) / 2;

        int pieceWidth = (int) (myLineHeight*ratioPieceOfLineHeight);  //棋子大小占行宽的3/4
        myWhitePiece = Bitmap.createScaledBitmap(myWhitePiece, pieceWidth, pieceWidth, false);    //以src为原图，创建新的图像，指定新图像的高宽以及是否可变。
        myBlackPiece = Bitmap.createScaledBitmap(myBlackPiece, pieceWidth, pieceWidth, false);
    }

    public Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context,drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, myPanelWidth, myPanelWidth);
       // drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    protected void  onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(getBitmapFromVectorDrawable(getContext(),R.drawable.chess_board), 0, 0, null);//my paint
        drawPiece(canvas);
        checkGameOver();
    }


    //画棋子
    private void drawPiece(Canvas canvas) {
        int n2 = myBlackArray.size();
        int n1 = myWhiteArray.size();

        for(int i =0; i< n2 ;i++){
            Point blackPoint = myBlackArray.get(i);
           //Log.d("neww", blackPoint.x+" x b");
           // Log.d("neww", blackPoint.y+" y b");
            canvas.drawBitmap(myBlackPiece, (blackPoint.x+(1-ratioPieceOfLineHeight)/2)*myLineHeight-0.255f*myLineHeight,
                    (blackPoint.y+(1-ratioPieceOfLineHeight)/2)*myLineHeight-0.255f*myLineHeight, null);
        }

        for(int i =0; i< n1 ;i++){
            Point whitePoint = myWhiteArray.get(i);
           // Log.d("neww", whitePoint.x+" x w");
           // Log.d("neww", whitePoint.y+" y w");
            canvas.drawBitmap(myWhitePiece, (whitePoint.x+(1-ratioPieceOfLineHeight)/2)* myLineHeight-0.255f*myLineHeight,
                    (whitePoint.y+(1-ratioPieceOfLineHeight)/2)*myLineHeight-0.255f*myLineHeight, null);
        }

    }

    //检测游戏是否结束
    private void checkGameOver(){
        boolean whiteWin = checkFiveInLine(myWhiteArray);
        boolean blackWin = checkFiveInLine(myBlackArray);

        if (whiteWin || blackWin) {
            isGameOver = true;
            if (onGameListener != null) {
                onGameListener.onGameOver(whiteWin ? WHITE_WIN : BLACK_WIN);
            }
        }
    }
    //回调一个int数据用于设置Dialog的位置
    public int getUnder() {
        return mUnder;
    }

    //检测是否存在五棋子相连的情况
    private boolean checkFiveInLine(List<Point> myArray){
        for(Point p : myArray){
            int x = p.x;
            int y = p.y;
            //判断是否存在五子相连情况
            boolean win_flag = checkHorizontal(x , y ,myArray)||checkVertical(x,y,myArray)
                            ||checkLeftDiagonal(x,y,myArray)||checkRightDiagonal(x,y,myArray);
            if (win_flag) {
                return true;
            }
        }
        return false;
    }

    //横向检查是否满足五子相连
    private boolean checkHorizontal(int x ,int y ,List<Point> myArray){
        int count = 1;
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x+i,y))) {
                count++;
            }else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x-i,y))) {
                count++;
            }else {
                break;
            }

            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    //纵向检查是否满足五子相连
    private boolean checkVertical(int x ,int y ,List<Point> myArray){
        int count = 1;
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x,y+i))) {
                count++;
            }else {
                break;
            }

        }
        if (count == 5) {
            return true;
        }
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x,y-i))) {
                count++;
            }else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    //左斜向检查是否满足五子相连
    private boolean checkLeftDiagonal(int x ,int y ,List<Point> myArray){
        int count = 1;
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x-i,y+i))) {
                count++;
            }else {
                break;
            }

        }
        if (count == 5) {
            return true;
        }
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x+i,y-i))) {
                count++;
            }else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    //右斜向检查是否满足五子相连
    private boolean checkRightDiagonal(int x ,int y ,List<Point> myArray){
        int count = 1;
        for(int i = 1;i < 5; i++){            //切记，i = 1 开始，否则就会只检测到三个子相连就结束了
            if (myArray.contains(new Point(x-i,y-i))) {
                count++;
            }else {
                break;
            }
        }
        if (count == 5) {
            return true;
        }
        for(int i = 1;i < 5; i++){
            if (myArray.contains(new Point(x+i,y+i))) {
                count++;
            }else {
                break;
            }
            if (count == 5) {
                return true;
            }
        }
        return false;
    }

    //悔棋，将最近一步的棋子路径删除，重新绘制棋盘
    protected void regret() {
        int n1 = myWhiteArray.size();
        int n2 = myBlackArray.size();
        //悔一步
        /*if(!isWhite) {
            myWhiteArray.remove(n1-1);
        } else {
            myBlackArray.remove(n2-1);
        }
        isWhite = !isWhite;*/
        //悔一对
        myWhiteArray.remove(n1-1);
        myBlackArray.remove(n2-1);
        invalidate();
    }

    //重新开始游戏
    protected void restartGame(){
        myBlackArray.clear();
        myWhiteArray.clear();
        isGameOver = false;
        renjuAI.init();
        invalidate();
    }
}