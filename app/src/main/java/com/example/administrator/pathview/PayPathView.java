package com.example.administrator.pathview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.administrator.pathview.PayPathView.payState.EXIT;
import static com.example.administrator.pathview.PayPathView.payState.FAILURE;
import static com.example.administrator.pathview.PayPathView.payState.NONE;
import static com.example.administrator.pathview.PayPathView.payState.PAYING;
import static com.example.administrator.pathview.PayPathView.payState.SUCCESS;

/**
 * Created by Administrator on 2017/9/15.
 */

public class PayPathView extends View {

    Paint mPaint;

    public static final int NEED_PAY_COUNT = 3;//执行支付动画需要的次数

    private boolean isPayingEnd = false;//支付动画是否结束，根据payingCount这个参数决定

    private int payingCount;//执行支付动画的次数

    private int mViewWidth;//view的宽度

    private int mViewHeight;//view的高度

    private int animationDuration = 1000;//动画时间

    private int animatorType = -1;//动画类型 0代表播放成功动画，1代表播放失败动画

    private int playType = 0;//播放类型，分开播放或者一起播放 0是分开播放，1是一起播放

    private boolean autoExit = false;//是否播放自动退出动画

    Path circlePath;//圆形路径

    Path rightPath;//勾的路径

    Path firstErrorPath;//叉号的第一条路径

    Path secondErrorPath;//叉号的第二条路径

    Path successPath;//成功路径

    Path failurePath;//失败路径

    PathMeasure mPathMeasure;

    Animator.AnimatorListener mAnimationListener;

    ValueAnimator.AnimatorUpdateListener mUpdateListener;

    ValueAnimator payingAnimation;//支付中的动画

    ValueAnimator successAnimation;//成功支付的动画

    ValueAnimator failureAnimation;//失败支付的动画

    ValueAnimator successExitAnimation;//还原动画

    private float animatorValue = 0f;//记录动画过程中的中间值

    private payState mCurrentState = NONE;//当前动画状态

    private Handler mHandler;

    private ArrayList<Path> pathList = new ArrayList<>();//存储动画路径的List

    private int mCurPathIndex = 0;//播放当前成功或者失败支付的动画路径list的中第几条

    /**
     * 支付状态枚举
     */
    public static enum payState {
        NONE,
        PAYING,
        SUCCESS,
        FAILURE,
        EXIT
    }

    public PayPathView(Context context) {
        this(context, null);
    }

    public PayPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * 初始化参数
     */
    private void initView() {
        initPaint();
        initPath();
        initHandler();
        initListener();
        initAnimation();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.WHITE);
    }

    /**
     * 初始化路径
     */
    private void initPath() {
        mPathMeasure = new PathMeasure();
        circlePath = new Path();
        circlePath.addCircle(0, 0, 100, Path.Direction.CW);

        rightPath = new Path();
        rightPath.moveTo(-55, -10);
        rightPath.lineTo(-5, 40);
        rightPath.lineTo(65, -40);

        successPath = new Path();
        successPath.addPath(circlePath);
        successPath.addPath(rightPath);

        firstErrorPath = new Path();
        firstErrorPath.moveTo(-50, -50);
        firstErrorPath.lineTo(50, 50);

        secondErrorPath = new Path();
        secondErrorPath.moveTo(50, -50);
        secondErrorPath.lineTo(-50, 50);

        failurePath = new Path();
        failurePath.addPath(circlePath);
        failurePath.addPath(firstErrorPath);
        failurePath.addPath(secondErrorPath);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {

        mAnimationListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i("test","end...");
                mHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };

        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
    }

    /**
     * 初始化所有动画
     */
    private void initAnimation() {
        payingAnimation = ValueAnimator.ofFloat(0, 1).setDuration(animationDuration);
        successAnimation = ValueAnimator.ofFloat(0, 1).setDuration(animationDuration);
        failureAnimation = ValueAnimator.ofFloat(0, 1).setDuration(animationDuration);
        successExitAnimation = ValueAnimator.ofFloat(1, 0).setDuration(animationDuration);

        payingAnimation.addListener(mAnimationListener);
        successAnimation.addListener(mAnimationListener);
        failureAnimation.addListener(mAnimationListener);
        successExitAnimation.addListener(mAnimationListener);

        payingAnimation.addUpdateListener(mUpdateListener);
        successAnimation.addUpdateListener(mUpdateListener);
        failureAnimation.addUpdateListener(mUpdateListener);
        successExitAnimation.addUpdateListener(mUpdateListener);

    }

    /**
     * 初始化handler
     */
    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (mCurrentState) {
                    case NONE:
                        mCurrentState = PAYING;
                        break;
                    case PAYING:
                        if (!isPayingEnd) {
                            Log.i("test","count==" + payingCount);
                            initAnimation();
                            payingAnimation.start();
                            payingCount++;
                            if (payingCount >= NEED_PAY_COUNT - 1) {
                                isPayingEnd = true;
                            }
                        } else {
                            payingAnimation.removeAllListeners();
                            if (0 == animatorType) {
                                mCurrentState = SUCCESS;
                                successAnimation.start();
                            } else if (1 == animatorType) {
                                mCurrentState = FAILURE;
                                failureAnimation.start();
                            }
                        }
                        break;
                    case SUCCESS:
                    case FAILURE:
                        if (mCurPathIndex < pathList.size() - 1 && playType == 0) {//拆分的路径动画还没播放完
                            mCurPathIndex++;
                            initAnimation();
                            if (0 == animatorType) {//成功
                                successAnimation.start();
                            } else if (1 == animatorType) {//失败
                                failureAnimation.start();
                            }
                        } else {//拆分的路径动画播放完或者是一起播放类型
                            initAnimation();
                            if (autoExit) {
                                mCurrentState = EXIT;
                                successExitAnimation.start();
                            }
                        }
                        break;
                    case EXIT:
                        if (mCurPathIndex > 0) {
                            mCurPathIndex--;
                            initAnimation();
                            successExitAnimation.start();
                        } else {
                            Toast.makeText(getContext(), "支付成功~", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        };
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas.isHardwareAccelerated()) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        drawPayPath(canvas);
    }

    /**
     * 画路径
     *
     * @param canvas
     */
    private void drawPayPath(Canvas canvas) {

        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        canvas.drawColor(Color.parseColor("#0082D7"));
        if (mCurrentState == FAILURE || (mCurrentState == EXIT && animatorType == 1)) {
            mPaint.setColor(Color.RED);
        } else {
            mPaint.setColor(Color.WHITE);
        }

        switch (mCurrentState) {
            case NONE:
                //canvas.drawPath(circlePath, mPaint);
                break;
            case PAYING:
                mPathMeasure.setPath(circlePath, false);
                Path dst = new Path();
                float end = mPathMeasure.getLength() * animatorValue;
                float start = (float) (end - ((0.5 - Math.abs(animatorValue - 0.5)) * 200f));
                mPathMeasure.getSegment(start, end, dst, true);
                canvas.drawPath(dst, mPaint);
                break;
            case SUCCESS:
                if (0 == playType) {//分开播放
                    drawAnimatorValuePartPath(canvas);
                } else if (1 == playType) {//一起播放
                    drawAnimatorValueTogetherPath(canvas,successPath);
                }
                break;
            case FAILURE:
                if (0 == playType) {
                    drawAnimatorValuePartPath(canvas);
                } else if (1 == playType) {
                    drawAnimatorValueTogetherPath(canvas,failurePath);
                }
                break;
            case EXIT:
                if (0 == playType) {
                    drawAnimatorValuePartPath(canvas);
                } else if (1 == playType) {
                    if (0 == animatorType) {//成功
                        drawAnimatorValueTogetherPath(canvas,successPath);
                    } else if (1 == animatorType) {//失败
                        drawAnimatorValueTogetherPath(canvas,failurePath);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 画路径
     *
     * @param canvas
     */
    private void drawAnimatorValuePartPath(Canvas canvas) {
        if (mCurPathIndex > 0) {
            for (int i = 0; i < mCurPathIndex; i++) {//画第N条路径时，要先把前面N-1条路径画好
                canvas.drawPath(pathList.get(i), mPaint);
            }
        }
        mPathMeasure.setPath(pathList.get(mCurPathIndex), false);
        Path dst = new Path();
        float start = 0f;
        float end = mPathMeasure.getLength() * animatorValue;
        mPathMeasure.getSegment(start, end, dst, true);
        canvas.drawPath(dst, mPaint);
    }

    private void drawAnimatorValueTogetherPath(Canvas canvas,Path path){
        mPathMeasure.setPath(path, false);
        while (mPathMeasure.nextContour()) {
            Path dsts = new Path();
            float starts = 0f;
            float ends = mPathMeasure.getLength() * animatorValue;
            mPathMeasure.getSegment(starts, ends, dsts, true);
            canvas.drawPath(dsts, mPaint);
        }
    }

    /**
     * 设置动画类型，成功支付或者失败支付的动画
     *
     * @param animatorType
     */
    public void setAnimatorType(int animatorType) {
        resetParams();
        this.animatorType = animatorType;
        if (animatorType == 0) {//成功
            pathList.add(circlePath);
            pathList.add(rightPath);
        } else if (animatorType == 1) {//失败
            pathList.add(circlePath);
            pathList.add(firstErrorPath);
            pathList.add(secondErrorPath);
        }
    }

    /**
     * 设置是否播放还原动画
     *
     * @param exit
     */
    public void setAutoExit(boolean exit) {
        this.autoExit = exit;
    }

    /**
     * 重新播放动画，还原参数
     */
    private void resetParams() {
        mCurPathIndex = 0;
        pathList.clear();
        payingCount = 0;
        isPayingEnd = false;
        animatorValue = 0f;
    }

    /**
     * 播放动画，外部调用
     */
    public void start() {
        mCurrentState = PAYING;
        payingAnimation.start();
    }

    /**
     * 设置播放类型
     */
    public void setPlayType(int playType) {
        this.playType = playType;
    }

}
