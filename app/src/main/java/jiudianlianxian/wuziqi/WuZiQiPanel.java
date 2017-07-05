package jiudianlianxian.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/15.
 */

public class WuZiQiPanel extends View {
    private int panelWidth;
    //每一个格子的宽度和高度
    private float lineHeight;
    //行数
    private int MAX_LINE = 10;
    //五点一线
    private int MAX_COUNT_IN_LINE = 5;
    //画笔
    private Paint paint = new Paint();
    //白棋
    private Bitmap whitePiece;
    //黑棋
    private Bitmap blackPiece;
    //棋子的大小是行高的3/4
    private float ratiopiece = 1.0f * 3 / 4;
    private boolean ISWHITE = true;//白棋先手或轮到白棋
    //记录下到棋盘上的白棋的集合
    private ArrayList<Point> whiteArray = new ArrayList<>();
    //记录下到棋盘上的黑棋的集合
    private ArrayList<Point> blackArray = new ArrayList<>();
    //棋局结束
    private boolean ISGameOver;
    //判断是颜色的棋赢
    private boolean ISWhitePiece;

    public WuZiQiPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        //初始化画笔
        paint.setColor(0x88000000);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        //初始化棋子
        whitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        blackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        panelWidth = w;
        lineHeight = panelWidth * 1.0f / MAX_LINE;
        int piexeWidth = (int) (lineHeight * ratiopiece);
        whitePiece = Bitmap.createScaledBitmap(whitePiece, piexeWidth, piexeWidth, false);
        blackPiece = Bitmap.createScaledBitmap(blackPiece, piexeWidth, piexeWidth, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ISGameOver)
            return false;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x, y);
            if (whiteArray.contains(p) || blackArray.contains(p)) {
                return false;
            }
            if (ISWHITE) {
                whiteArray.add(p);
            } else {
                blackArray.add(p);
            }
            invalidate();
            ISWHITE = !ISWHITE;

        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / lineHeight), (int) (y / lineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPiece(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(whiteArray);
        boolean blackWin = checkFiveInLine(blackArray);
        if (whiteWin || blackWin) {
            ISGameOver = true;
            ISWhitePiece = whiteWin;
            String text = ISWhitePiece ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
            if (whiteArray.size()+blackArray.size()==100){
                Toast.makeText(getContext(), "平局", Toast.LENGTH_SHORT).show();
            }
    }

    //五子连线
    private boolean checkFiveInLine(List<Point> point) {
        for (Point point1 : point) {
            int X = point1.x;
            int Y = point1.y;
            boolean win = checkHorizontal(X, Y, point);
            if (win)
                return true;
            win = checkVertical(X, Y, point);
            if (win)
                return true;
            win = checkLeftDiagnoal(X, Y, point);
            if (win)
                return true;
            win = checkRightDiagnoal(X, Y, point);
            if (win)
                return true;
        }
        return false;
    }

    /**
     * 判断 x，y位置的棋子，是否横向有相邻的五个一致
     *
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> point) {
        int count = 1;
        //左边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (point.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        //右边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (point.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //竖向
    private boolean checkVertical(int x, int y, List<Point> point) {
        int count = 1;
        //上边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (point.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        //下边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (point.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //左斜
    private boolean checkLeftDiagnoal(int x, int y, List<Point> point) {
        int count = 1;
        //上边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (point.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        //下边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (point.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //右斜
    private boolean checkRightDiagnoal(int x, int y, List<Point> point) {
        int count = 1;
        //上边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (point.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        //下边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (point.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //绘制棋子
    private void drawPiece(Canvas canvas) {
        for (int i = 0, n = whiteArray.size(); i < n; i++) {
            Point whitePoint = whiteArray.get(i);
            canvas.drawBitmap(whitePiece, (whitePoint.x + (1 - ratiopiece) / 2) * lineHeight,
                    (whitePoint.y + (1 - ratiopiece) / 2) * lineHeight, null);
        }
        for (int i = 0, n = blackArray.size(); i < n; i++) {
            Point blackPoint = blackArray.get(i);
            canvas.drawBitmap(blackPiece, (blackPoint.x + (1 - ratiopiece) / 2) * lineHeight,
                    (blackPoint.y + (1 - ratiopiece) / 2) * lineHeight, null);
        }
    }

    //绘制棋盘
    private void drawBoard(Canvas canvas) {
        int w = panelWidth;
        float lineHeight = this.lineHeight;
        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, paint);
            canvas.drawLine(y, startX, y, endX, paint);
        }
    }

    public void start() {
        whiteArray.clear();
        blackArray.clear();
        ISGameOver = false;
        ISWhitePiece = false;
        invalidate();
    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //对棋子的位置进行存储
    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle build = new Bundle();
        build.putParcelable(INSTANCE, super.onSaveInstanceState());
        build.putBoolean(INSTANCE_GAME_OVER, ISGameOver);
        build.putParcelableArrayList(INSTANCE_WHITE_ARRAY, whiteArray);
        build.putParcelableArrayList(INSTANCE_BLACK_ARRAY, blackArray);
        return build;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            ISGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            whiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            blackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
