package com.ios.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;

import java.lang.reflect.Method;


public class IosLikeToggleButton extends Button implements GestureDetector.OnGestureListener {
    private static int WIDTH_PX_IPHONE = 105;
    private final static float HEIGHT_PARAM = (float) 62 / WIDTH_PX_IPHONE;
    private final static float FILL_PARAM = (float) 3 / WIDTH_PX_IPHONE;
    private final static float THUMB_X_PARAM = (float) 2 / WIDTH_PX_IPHONE;
    private final static float THUMB_Y_PARAM = (float) 3 / WIDTH_PX_IPHONE;
    private final static float SHADOW_RADIUS_PARAM = (float) 8 / WIDTH_PX_IPHONE;
    private final static float SHADOW_DX_PARAM = (float) 5 / WIDTH_PX_IPHONE;
    private final static float SHADOW_DY_PARAM = (float) 5 / WIDTH_PX_IPHONE;
    private final static float MARGIN_PARAM = (float) 8 / WIDTH_PX_IPHONE;

    // color for back layer
//    private final static int COLOR_BACK_LAYER_ON = Color.rgb(0x4C, 0xD9, 0x64);
    private  static int COLOR_BACK_LAYER_ON = Color.rgb(0x06, 0x94, 0xEB);
    private  static int COLOR_BACK_LAYER_OFF = Color.rgb(0xE6, 0xE6, 0xE6);
    private  static int COLOR_BACK_LAYER_DIFF_RED = Math.abs(Color.red(COLOR_BACK_LAYER_ON)
            - Color.red(COLOR_BACK_LAYER_OFF));
    private  static int COLOR_BACK_LAYER_DIFF_GREEN = Math.abs(Color
            .green(COLOR_BACK_LAYER_ON) - Color.green(COLOR_BACK_LAYER_OFF));
    private  static int COLOR_BACK_LAYER_DIFF_BLUE = Math.abs(Color.blue(COLOR_BACK_LAYER_ON)
            - Color.blue(COLOR_BACK_LAYER_OFF));

    // color for fill layer
    private final static int COLOR_FILL_LAYER = Color.WHITE;

    private final static int COLOR_THUMB_LAYER = Color.WHITE;
    private final static int COLOR_THUMB_LAYER_BORDER = Color.argb((int) (0.2 * 256), 0, 0, 0);
    private final static float COLOR_THUMB_LAYER_BOARD_DEGREE_ON = 90;
    private final static float COLOR_THUMB_LAYER_BOARD_DEGREE_OFF = 0;

    private final static int COLOR_DISABLED = Color.argb((int) (0.4 * 256), 0x89, 0x89, 0x89);

    private final static int DURATION_FILL_LAYER = 200;
    private final static int DURATION_TRANSLATE = 200;

    private final static int FILL_LAYER_STATE_NORMAL = -1;
    private final static int FILL_LAYER_STATE_IN = 0;
    private final static int FILL_LAYER_STATE_OUT = 1;

    private Context mContext = null;
    private int mWidth = 0;
    private int mHeight = 0;

    // for draw
    private final RectF mBackLayerRectF = new RectF();
    private final Path mBackLayer = new Path();
    private int mBackLayerPaintColor = COLOR_BACK_LAYER_ON;

    private final RectF mFillLayerRectF = new RectF();
    private final Path mFillLayer = new Path();
    private float mFillLayerOffset = 0.0f;
    private int mFillLayerState = FILL_LAYER_STATE_NORMAL;

    private final RectF mThumbLayerRectF = new RectF();
    private final Path mThumbLayer = new Path();
    private float mThumbLayerLeftX = 0.0f;
    private float mThumbLayerRightX = 0.0f;
    private float mThumbLayerOffsetX = 0.0f;
    private float mThumbLayerOffsetY = 0.0f;

    private float mThumbLayerPaintX = 0.0f;
    private float mThumbLayerPaintY = 0.0f;
    private final int mThumbLayerColor = COLOR_THUMB_LAYER;
    private final int mThumbLayerBorderColor = COLOR_THUMB_LAYER_BORDER;
    private float mThumbLayerBorderDegree = 0.0f;

    private float mLastDragMotionX = 0.0f;
    private boolean mThumbLayerDrag = false;
    private boolean mThumbLayerHit = false;

    private float mShadowRadius = 0;
    private float mShadowDx = 0;
    private float mShadowDy = 0;
    private float mMargin = 0;

    private final Paint mPaint = new Paint();

    // for anim
    private final Animation mScaleOut = new ScaleAnimation(0f, 1f, 0f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    private final Animation mScaleIn = new ScaleAnimation(1f, 0f, 1f, 0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

    private Animation mFillLayerAnim = null;
    private final Transformation mFillLayerAnimTrans = new Transformation();

    private Animation mTranslateAnim = null;
    private final Transformation mTranslateAnimTrans = new Transformation();

    // gesture
    private GestureDetector mGestureDetector = null;

    // listener
    private boolean mChecked;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private final int ATTR_ANDROID_LAYOUT_WIDTH = 0;

    public IosLikeToggleButton(Context context) {
        super(context);
        init(context);
    }

    public IosLikeToggleButton(Context context, AttributeSet attrs) {
        super(context,attrs);
        TypedArray  attr = context.obtainStyledAttributes(attrs,R.styleable.IosLikeToggleButton);
        COLOR_BACK_LAYER_ON = attr.getColor(R.styleable.IosLikeToggleButton_on_color,COLOR_BACK_LAYER_ON);
        COLOR_BACK_LAYER_OFF = attr.getColor(R.styleable.IosLikeToggleButton_off_color,COLOR_BACK_LAYER_OFF);
        mChecked = attr.getBoolean(R.styleable.IosLikeToggleButton_toggle,mChecked);
        mWidth = attr.getDimensionPixelOffset(ATTR_ANDROID_LAYOUT_WIDTH,WIDTH_PX_IPHONE);

        COLOR_BACK_LAYER_DIFF_RED = Math.abs(Color.red(COLOR_BACK_LAYER_ON)
                - Color.red(COLOR_BACK_LAYER_OFF));
        COLOR_BACK_LAYER_DIFF_GREEN = Math.abs(Color
                .green(COLOR_BACK_LAYER_ON) - Color.green(COLOR_BACK_LAYER_OFF));
        COLOR_BACK_LAYER_DIFF_BLUE = Math.abs(Color.blue(COLOR_BACK_LAYER_ON)
                - Color.blue(COLOR_BACK_LAYER_OFF));
        init(context);
    }


    private void init(Context context) {
        mPaint.setAntiAlias(true);

        setFocusable(true);
        setChecked(true);

        mContext = context.getApplicationContext();

        mHeight = (int) (mWidth * HEIGHT_PARAM);
        mFillLayerOffset = mWidth * FILL_PARAM;
        mThumbLayerOffsetX = mWidth * THUMB_X_PARAM;
        mThumbLayerOffsetY = mWidth * THUMB_Y_PARAM;
        mShadowRadius = mWidth * SHADOW_RADIUS_PARAM;
        mShadowDx = mWidth * SHADOW_DX_PARAM;
        mShadowDy = mWidth * SHADOW_DY_PARAM;
        mMargin = mWidth * MARGIN_PARAM;

        float radius = 0.0f;
        mBackLayerRectF.set(0, 0, mWidth, mHeight);
        radius = mBackLayerRectF.height() / 2;
        mBackLayer.addRoundRect(mBackLayerRectF, radius, radius, Path.Direction.CW);

        mFillLayerRectF.set(mBackLayerRectF);
        mFillLayerRectF.inset(mFillLayerOffset, mFillLayerOffset);
        radius = mFillLayerRectF.height() / 2;
        mFillLayer.addRoundRect(mFillLayerRectF, radius, radius, Path.Direction.CW);

        float diameter = mHeight - mThumbLayerOffsetY * 2;
        mThumbLayerRectF.set(0, 0, diameter, diameter);
        radius = diameter / 2;
        mThumbLayer.addRoundRect(mThumbLayerRectF, radius, radius, Path.Direction.CW);
        mThumbLayerLeftX = mThumbLayerOffsetX;
        mThumbLayerRightX = mWidth - mThumbLayerOffsetX - mThumbLayerRectF.width();

        if (isChecked()) {
            mThumbLayerPaintX = mThumbLayerRightX;
            mThumbLayerPaintY = mThumbLayerOffsetY;
            mThumbLayerBorderDegree = COLOR_THUMB_LAYER_BOARD_DEGREE_ON;
            mBackLayerPaintColor = COLOR_BACK_LAYER_ON;
        } else {
            mThumbLayerPaintX = mThumbLayerLeftX;
            mThumbLayerPaintY = mThumbLayerOffsetY;
            mThumbLayerBorderDegree = COLOR_THUMB_LAYER_BOARD_DEGREE_OFF;
            mBackLayerPaintColor = COLOR_BACK_LAYER_OFF;
        }

        mGestureDetector = new GestureDetector(mContext, this);

        // Log.i("xsm", "sw = " + mScreenWidth + " w = " + mWidth + " h = " + mHeight);
        // Log.i("xsm", "back layer = " + mBackLayerRectF);
        // Log.i("xsm", "fill layer = " + mFillLayerRectF);
        // Log.i("xsm", "thumb layer = " + mThumbLayerRectF + " leftX = " + mThumbLayerLeftX
        // + " RightX = " + mThumbLayerRightX);

        if (Build.VERSION.SDK_INT >= 11) {
            // setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            try {
                Method setLayerType =
                        View.class.getDeclaredMethod("setLayerType", int.class, Paint.class);
                setLayerType.invoke(this, 1, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension((int) (mWidth + mMargin), (int) (mHeight + mMargin));
    }

    private void startTranslateAnim(float from, float to) {
        if (mTranslateAnim != null) {
            mTranslateAnim.cancel();
            mTranslateAnim = null;
        }

        mTranslateAnim = new AlphaAnimation(from, to);
        mTranslateAnim.setDuration(DURATION_TRANSLATE);
        mTranslateAnim.start();

        postInvalidate();
    }

    private boolean computeTranslate(boolean checked, boolean pressed) {
        boolean redraw = false;
        float offset = 0.0f;

        if (mTranslateAnim == null || mTranslateAnim.hasEnded()) {
            mTranslateAnim = null;
            offset = checked ? 1.0f : 0.0f;
            if (mThumbLayerDrag) {
                offset =
                        (mThumbLayerPaintX - mThumbLayerLeftX)
                                / (mThumbLayerRightX - mThumbLayerLeftX);
            }
        } else {
            long currentTime = SystemClock.uptimeMillis();
            boolean more = mTranslateAnim.getTransformation(currentTime, mTranslateAnimTrans);
            if (more) {
                offset = mTranslateAnimTrans.getAlpha();
                redraw = true;
            }
        }

        mThumbLayerPaintX = mThumbLayerLeftX + (mThumbLayerRightX - mThumbLayerLeftX) * offset;
        mThumbLayerBorderDegree =
                (offset >= 0.5)
                        ? COLOR_THUMB_LAYER_BOARD_DEGREE_ON
                        : COLOR_THUMB_LAYER_BOARD_DEGREE_OFF;

        offset = 1 - offset;
        int r = (int) (COLOR_BACK_LAYER_DIFF_RED * offset);
        int g = (int) (COLOR_BACK_LAYER_DIFF_GREEN * offset);
        int b = (int) (COLOR_BACK_LAYER_DIFF_BLUE * offset);

        mBackLayerPaintColor =
                Color.rgb(Color.red(COLOR_BACK_LAYER_ON) + r, Color.green(COLOR_BACK_LAYER_ON) + g,
                        Color.blue(COLOR_BACK_LAYER_ON) + b);

        return redraw;
    }

    @Override
    public void draw(Canvas canvas) {
        boolean checked = isChecked();
        boolean pressed = isPressed();

        boolean redrawTranslate = this.computeTranslate(checked, pressed);
        drawBackLayer(canvas, mBackLayerPaintColor);

        boolean redrawFillLayer = drawFillLayer(canvas, checked, pressed, COLOR_FILL_LAYER);

        drawThumbLayer(canvas, mThumbLayerColor, mThumbLayerBorderColor, mThumbLayerPaintX,
                mThumbLayerPaintY, mThumbLayerBorderDegree);

        if (!isEnabled()) {
            drawBackLayer(canvas, COLOR_DISABLED);
        }

        if (redrawTranslate || redrawFillLayer) {
            postInvalidate();
        }
    }

    private void drawBackLayer(Canvas canvas, int color) {
        mPaint.setColor(color);
        canvas.drawPath(mBackLayer, mPaint);
    }

    private void drawThumbLayer(Canvas canvas, int color, int shadow_color, float x, float y,
            float degree) {
        canvas.save();

        canvas.translate(x, y);
        canvas.rotate(degree, mThumbLayerRectF.width() / 2, mThumbLayerRectF.height() / 2);
        mPaint.setColor(color);
        mPaint.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, shadow_color);
        canvas.drawPath(mThumbLayer, mPaint);
        mPaint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);

        canvas.restore();

    }

    public void cancelFillLayerScaleAnim() {
        if (mFillLayerAnim != null) {
            mFillLayerAnim.cancel();
            mFillLayerAnim.reset();
            mFillLayerAnim = null;
        }
    }

    public void doFillLayerScaleAnim(int state) {
        if (mFillLayerAnim != null) {
            mFillLayerAnim.cancel();
            mFillLayerAnim = null;
        }

        mFillLayerState = state;
        mFillLayerAnim = (mFillLayerState == FILL_LAYER_STATE_IN) ? mScaleIn : mScaleOut;
        if (!mFillLayerAnim.isInitialized()) {
            mFillLayerAnim.initialize((int) mFillLayerRectF.width(),
                    (int) mFillLayerRectF.height(), ((View) getParent()).getWidth(),
                    ((View) getParent()).getHeight());
        }
        mFillLayerAnim.setDuration(DURATION_FILL_LAYER);
        mFillLayerAnim.start();
        invalidate();
    }

    private boolean drawFillLayer(Canvas canvas, boolean checked, boolean pressed, int color) {
        boolean redraw = false;
        boolean drawFillLayerAnim = false;

        canvas.save();
        mPaint.setColor(color);
        if (mFillLayerAnim != null) {
            if (mFillLayerAnim.hasEnded()) {
                mFillLayerAnim.reset();
                mFillLayerAnim = null;
            } else {
                long currentTime = SystemClock.uptimeMillis();
                boolean more = mFillLayerAnim.getTransformation(currentTime, mFillLayerAnimTrans);
                if (more) {
                    canvas.concat(mFillLayerAnimTrans.getMatrix());
                    drawFillLayerAnim = true;
                    redraw = true;
                }
            }
        }

        boolean draw = (!checked && (mFillLayerState != FILL_LAYER_STATE_IN) || drawFillLayerAnim);
        if (draw) {
            canvas.drawPath(mFillLayer, mPaint);
        }
        canvas.restore();

        return redraw;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            // Log.i("xsm", ".......................................... up or cancel : " + action
            // + " drag : " + mThumbLayerDrag);
            boolean pressed = isPressed();
            boolean checked = isChecked();
            boolean draged = mThumbLayerDrag;
            float centerX = (mThumbLayerRightX - mThumbLayerLeftX) / 2;
            if (draged) {
                if (mTranslateAnim == null) {
                    float from =
                            (mThumbLayerPaintX - mThumbLayerLeftX)
                                    / (mThumbLayerRightX - mThumbLayerLeftX);
                    if (mThumbLayerPaintX > centerX) {
                        if (!checked) {
                            performClick();
                        }
                        if (from < 1.0f) {
                            startTranslateAnim(from, 1.0f);
                        }
                    } else {
                        if (checked) {
                            performClick();
                        }
                        if (from > 0.0f) {
                            startTranslateAnim(from, 0.0f);
                        }
                        doFillLayerScaleAnim(FILL_LAYER_STATE_OUT);
                    }
                }
            } else if (mThumbLayerHit) {
                if (pressed && mTranslateAnim == null) {
                    performClick();
                    float from = (mThumbLayerPaintX == mThumbLayerLeftX) ? 0.0f : 1.0f;
                    float to = 1.0f - from;
                    startTranslateAnim(from, to);
                }
                if (!isChecked()) {
                    doFillLayerScaleAnim(FILL_LAYER_STATE_OUT);
                }
            }
            mThumbLayerDrag = false;
            mThumbLayerHit = false;
            mFillLayerState = FILL_LAYER_STATE_NORMAL;
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mLastDragMotionX = e.getX();
        setPressed(true);

        RectF rect = new RectF(mThumbLayerRectF);
        rect.offset(mThumbLayerPaintX, 0);

        if (rect.contains(e.getX(), e.getY())) {
            mThumbLayerHit = true;
            if (!isChecked()) {
                doFillLayerScaleAnim(FILL_LAYER_STATE_IN);
            }
        }
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {}

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float pos = e2.getX();
        float diff = (int) Math.abs(pos - this.mLastDragMotionX);

        if (diff > 10 && mThumbLayerHit) {
            mThumbLayerPaintX =
                    Math.min(mThumbLayerRightX, Math.max(mThumbLayerLeftX,
                            (mThumbLayerPaintX + pos - mLastDragMotionX)));
            this.mLastDragMotionX = pos;
            mThumbLayerDrag = true;
            cancelFillLayerScaleAnim();
            invalidate();
        }

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {}

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(IosLikeToggleButton buttonView, boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;

            if (mOnCheckedChangeListener != null) {
                postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (mOnCheckedChangeListener != null)
                            mOnCheckedChangeListener.onCheckedChanged(IosLikeToggleButton.this,
                                    mChecked);
                    }

                }, DURATION_FILL_LAYER);
            }
            postInvalidate();
        }
    }


}
