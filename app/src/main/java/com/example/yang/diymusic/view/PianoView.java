package com.example.yang.diymusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.yang.diymusic.LogUtil;
import com.example.yang.diymusic.model.PreferenceService;
import com.example.yang.diymusic.R;
import com.example.yang.diymusic.model.RecodeModel;
import com.example.yang.diymusic.model.SoundModel;

public class PianoView extends View {
    /**
     * 白键的数量
     */
    public final static int WHITE_PIANO_KEY_COUNT = 52;
    /**
     * 黑键的数量
     */
    public final static int BLACK_PIANO_KEY_COUNT = 36;
    /**
     * 白键标识
     */
    private final static int KEY_WHITE = 0;
    /**
     * 黑键标识
     */
    private final static int KEY_BLACK = 1;
    /**
     * 移动进度
     */
    private double mProgress = 0.5f;
    /**
     * mWhiteRect 白色键的矩形
     * mBlackRect 黑色键的矩形
     */
    private Rect mWhiteRect, mBlackRect;
    /**
     * 设置个新的长方形
     */
    private RectF oval;
    /**
     * 按键数量
     */
    private int mWhiteNum;
    /**
     * 按键底图图片
     */
    private Drawable mWhiteDrawable, mBlackDrawable, wSelectDrawable,
            bSelectDrawable;
    /**
     * 自定义View的宽
     */
    private int mSurfaceWidth;
    /**
     * 记录按下去的点
     */
    private PointF[] list = new PointF[2];
    /**
     * 是否选择白色
     */
    private boolean isWselect = false;
    /**
     * 是否选择黑色
     */
    private boolean isBselect = false;
    /**
     * 是否移动
     */
    private boolean isMove = true;
    /**
     * 画标识宽的颜色
     */
    private Paint p;
    /**
     * 字体画笔
     */
    private TextPaint paint;
    /**
     * 首选项
     */
    private PreferenceService pf;
    /**
     * 字体的大小
     */
    private float mTextY;
    /**
     * 标识1
     */
    private String[] mTones = {"do", "re", "mi", "fa", "sol", "la", "si"};
    /**
     * 标识2
     */
    private String[] mDTones = {"C", "D", "E", "F", "G", "A", "B"};
    private String[] mNumbers = {"0", "1", "2", "3", "4", "5", "6", "7", "8"};
    /**
     * 标识矩形的颜色
     */
    private int[] mToneColors = {0xFFCECECE, 0xFFB88886, 0xFFFE7C79,
            0xFFFEA44A, 0xFFEDED3B, 0xFF24FF24, 0xFF36FFFF, 0xFF63AFFA,
            0xFFAE6EEF};
    /**
     * 白色按键恢复默认
     */
    private int wIndex = -1;
    /**
     * 黑色按键恢复默认
     */
    private int bIndex = -1;

    public void setRecord(boolean recorde) {
        isRecorde = recorde;
    }

    private boolean isRecorde = false;

    public PianoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(context);
    }

    private void setupView(Context context) {
        mWhiteDrawable = context.getResources()
                .getDrawable(R.drawable.white_up);
        mBlackDrawable = context.getResources()
                .getDrawable(R.drawable.black_up);
        wSelectDrawable = context.getResources().getDrawable(
                R.drawable.white_down);
        bSelectDrawable = context.getResources().getDrawable(
                R.drawable.black_down);
        pf = new PreferenceService();
        mWhiteNum = pf.getKeyNumber();
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.FILL);

        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLACK);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWhiteNum = pf.getKeyNumber();
        mSurfaceWidth = w - 2;
        Log.i("yang", "" + (getWidth() - mSurfaceWidth - 2));
        if (mWhiteNum == 9 || mWhiteNum > 10 && mWhiteNum < 14) {
            mSurfaceWidth = w - mWhiteNum;
        }
        mWhiteRect = new Rect(0, 0, w / mWhiteNum, h);
        mBlackRect = new Rect(0, 0, w / mWhiteNum * 2 / 3, h * 3 / 5);
        //两排按键标签位置
        if (pf.getMode() == 2) {
            oval = new RectF(0, h * 3 / 5 + mWhiteNum + 8, w / mWhiteNum / 2,
                    h * 4 / 5 + 10);
        } else {
            oval = new RectF(0, h * 5 / 7 + mWhiteNum, w / mWhiteNum / 2,
                    h * 6 / 7 - 4);
        }
        //字体的大小
        paint.setTextSize(oval.width() / 2 - 12 + oval.height() / 4);
        mTextY = oval.centerY() + paint.descent();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //移动的距离
        int offset = calculateOffset();
        Log.e("===>>", "wIndex:" + wIndex + "  bIndex:" + bIndex);
        //设置画布的颜色
        canvas.drawColor(Color.WHITE);
        //绘制白色按键
        drawWhiteKeys(canvas, offset, wIndex);
        //绘制黑色按键
        drawBlackKeys(canvas, offset, bIndex);
    }

    // 画黑色的键
    private void drawBlackKeys(Canvas canvas, int offset, int position) {
        mBlackRect.offsetTo(offset, 0);
        mBlackRect.offset(mWhiteRect.width() - mBlackRect.width() / 2, 0);
        int temp = 0;
        for (int i = 0; i < BLACK_PIANO_KEY_COUNT; i++) {
            //只画屏幕中显示的按键
            if (mBlackRect.left > mSurfaceWidth) {
                break;
            } else if (mBlackRect.right > 0) {
                mBlackDrawable.setBounds(mBlackRect);
                mBlackDrawable.draw(canvas);
                //播放录音按键显示效果
                if (position == i) {
                    bSelectDrawable.setBounds(mBlackRect);
                    bSelectDrawable.draw(canvas);
                }
                //按键的点击效果
                if (isBselect) {
                    for (int j = 0; j < list.length; j++) {
                        if (list[j] != null
                                && mBlackRect.contains((int) list[j].x,
                                (int) list[j].y)) {
                            bSelectDrawable.setBounds(mBlackRect);
                            bSelectDrawable.draw(canvas);
                        }
                    }
                }
            }
            temp = i % 5;
            //绘制黑色按键的位置
            if (temp == 0 || temp == 2) {
                mBlackRect.offset(2 * mWhiteRect.width(), 0);
            } else {
                mBlackRect.offset(mWhiteRect.width(), 0);
            }
        }
    }

    // 画白色的键
    private void drawWhiteKeys(Canvas canvas, int offset, int position) {
        mWhiteRect.offsetTo(offset, 0);
        oval.offsetTo(offset + mWhiteRect.width() / 4, oval.top);
        for (int i = 0; i < WHITE_PIANO_KEY_COUNT; i++) {
            if (mWhiteRect.left > mSurfaceWidth) {
                break;
            } else if (mWhiteRect.right >= 0) {
                mWhiteDrawable.setBounds(mWhiteRect);
                mWhiteDrawable.draw(canvas);
                if (position == i) {
                    Log.i("jjf", "进入^^^^^^");
                    wSelectDrawable.setBounds(mWhiteRect);
                    wSelectDrawable.draw(canvas);
                }
                if (isWselect) {
                    for (int j = 0; j < list.length; j++) {
                        if (list[j] != null
                                && indexOfAtBlackKeys((int) list[j].x,
                                (int) list[j].y) < 0) {
                            if (mWhiteRect.contains((int) list[j].x,
                                    (int) list[j].y)) {
                                wSelectDrawable.setBounds(mWhiteRect);
                                wSelectDrawable.draw(canvas);
                            }
                        }
                    }
                }
//                绘制标签
                if (pf.getTone().equals("Do")) {
                    p.setColor(mToneColors[(i + 5) / 7 % 9]);
                    canvas.drawRoundRect(oval, 20, 15, p);// 第二个参数是x半径，第三个参数是y半径
                    canvas.drawText(mTones[(i + 5) % 7], oval.centerX(),
                            mTextY, paint);
                } else if (pf.getTone().equals("D4")) {
                    p.setColor(mToneColors[(i + 5) / 7 % 9]);
                    canvas.drawRoundRect(oval, 20, 15, p);// 第二个参数是x半径，第三个参数是y半径
                    canvas.drawText(mDTones[(i + 5) % 7]
                                    + mNumbers[(i + 5) / 7 % 9], oval.centerX(),
                            mTextY, paint);
                }
            }
            mWhiteRect.offset(mWhiteRect.width(), 0);
            oval.offset(mWhiteRect.width(), 0);
        }
    }

    //获取滑动的距离
    private int calculateOffset() {
        if (WHITE_PIANO_KEY_COUNT == mWhiteNum) {
            return 0;
        }

        float maxW = WHITE_PIANO_KEY_COUNT / (float) mWhiteNum * mSurfaceWidth
                - mSurfaceWidth;
        if (mProgress < 0) {
            mProgress = 0;
        }
        if (mProgress > 1) {
            mProgress = 1;
        }
        return (int) (maxW * mProgress * (-1));
    }

    //设置滑动的值
    public void setProgress(double progress) {
        mProgress = progress;
        invalidate();
    }

    //设置按键显示的数量
    public void setNumber(int i) {
        mWhiteNum = i;
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) {
            return;
        }
        mSurfaceWidth = w - 2;
        if (mWhiteNum == 9 || mWhiteNum > 10 && mWhiteNum < 14) {
            mSurfaceWidth = w - mWhiteNum;
        }
        mWhiteRect = new Rect(0, 0, w / mWhiteNum, h);
        mBlackRect = new Rect(0, 0, w / mWhiteNum * 2 / 3, h * 3 / 5);
        if (pf.getMode() == 2) {
            oval = new RectF(0, h * 3 / 5 + mWhiteNum + 8, w / mWhiteNum / 2,
                    h * 4 / 5 + 10);
        } else {
            oval = new RectF(0, h * 5 / 7 + mWhiteNum, w / mWhiteNum / 2,
                    h * 6 / 7 - 4);
        }
        paint.setTextSize(oval.width() / 2 - 12 + oval.height() / 4);
        mTextY = oval.centerY() + paint.descent();
        invalidate();
    }

    int touch_x, touch_y, move_x, touch_x1;

    //多点触控
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (SoundModel.getInstance().isQueuePlaying()) {
//            return true;
//        }
        int pointerCount = event.getPointerCount();
        int x1 = 0, y1 = 0;
        int x = (int) event.getX(0);
        int y = (int) event.getY(0);
        if (pointerCount == 2) {
            x1 = (int) event.getX(1);
            y1 = (int) event.getY(1);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // 第一个手指按下事件
                Log.i("jjf", "第一个手指按下事件");
                move_x = touch_x = (int) event.getX(0);
                touch_y = (int) event.getY(0);
                if (isMove) {
                    list[0] = new PointF(touch_x, touch_y);
                }
                keyOnclick(touch_x, touch_y);
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 第二个手指按下事件
                Log.i("jjf", "第二个手指按下事件");
                touch_x1 = (int) event.getX(1);
                list[1] = new PointF(x1, y1);
                keyOnclick(x1, y1);
                break;
            case MotionEvent.ACTION_UP:
                list[0] = null;
                isBselect = false;
                isWselect = false;
                isMove = true;
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // 手指放开事件
                list[1] = null;
                isBselect = false;
                isWselect = false;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                int xt = (int) event.getX(0);
                isMove = false;
                if (pointerCount == 2) {
                    int xt1 = (int) event.getX(1);
                    Log.i("jjf", "二个手指移动事件");
                    if (Math.abs(xt - touch_x) > mWhiteRect.width()) {
                        Log.i("jjf", "第一个手指移动事件");
                        list[0] = new PointF(x, y);
                        keyOnclick(x, y);
                        touch_x = xt;
                    }
                    if (Math.abs(xt1 - touch_x1) > mWhiteRect.width()) {
                        Log.i("jjf", "第二个手指移动事件");
                        // 两个手指滑动
                        list[1] = new PointF(x1, y1);
                        keyOnclick(x1, y1);
                        touch_x1 = xt1;
                    }
                } else {
                    if (Math.abs(xt - move_x) > mWhiteRect.width()) {
                        Log.i("jjf", "一个手指移动事件");
                        list[0] = new PointF(x, y);
                        keyOnclick(x, y);
                        move_x = xt;
                    }
                }
                break;

        }

        return true;
    }

    SoundModel soundModel = SoundModel.getInstance(getContext());

    // 手指滑动点击事件
    private void keyOnclick(int x, int y) {
        //获取黑色点击按键的位置
        int index = indexOfAtBlackKeys(x, y);
        if (index < 0) {
            //获取白色点击按键的位置
            index = indexOfAtWhiteKeys(x, y);
            if (index >= 0) {
                isWselect = true;
                //播放白色按键音乐
                soundModel.play(KEY_WHITE, index);
                //保存白色按键位置
                if (isRecorde) {
                    RecodeModel.getInstance().addEvent(KEY_WHITE, index);
                }
                LogUtil.i("白色" + index);
                invalidate();
            } else {
                return;
            }
        } else {
            isBselect = true;
            isBselect = true;
            //播放黑色按键音乐
            soundModel.play(KEY_BLACK, index);
            //保存黑色按键位置
            if (isRecorde) {
                RecodeModel.getInstance().addEvent(KEY_BLACK, index);
            }
            LogUtil.i("黑色" + index);
            invalidate();
        }
    }

    //获取黑色点击按键的位置
    private int indexOfAtBlackKeys(int x, int y) {
        mBlackRect.offsetTo(calculateOffset(), 0);
        mBlackRect.offset(mWhiteRect.width() - mBlackRect.width() / 2, 0);
        int temp = 0;
        for (int i = 0; i < BLACK_PIANO_KEY_COUNT; i++) {
            if (mBlackRect.left > mSurfaceWidth) {
                break;
            } else if (mBlackRect.right >= 0) {
                if (mBlackRect.contains(x, y)) {
                    return i;
                }
            }
            temp = i % 5;
            if (temp == 0 || temp == 2) {
                mBlackRect.offset(2 * mWhiteRect.width(), 0);
            } else {
                mBlackRect.offset(mWhiteRect.width(), 0);
            }
        }
        return -1;
    }

    //获取白色点击按键的位置
    private int indexOfAtWhiteKeys(int x, int y) {
        mWhiteRect.offsetTo(calculateOffset(), 0);
        for (int i = 0; i < WHITE_PIANO_KEY_COUNT; i++) {
            if (mWhiteRect.left > mSurfaceWidth) {
                break;
            } else if (mWhiteRect.right >= 0) {
                if (mWhiteRect.contains(x, y)) {
                    return i;
                }
            }
            mWhiteRect.offset(mWhiteRect.width(), 0);
        }
        return -1;
    }

    //恢复按键默认状态
    public void Recover() {
        wIndex = -1;
        bIndex = -1;
        invalidate();
    }

    public void setPf(PreferenceService pf) {
        this.pf = pf;
        invalidate();
    }
}