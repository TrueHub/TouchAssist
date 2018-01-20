package vone.meihuayishu.com.touchassist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by longyang on 2018/1/15.
 */

public class RulerView extends View {
    private static final String TAG = "MSL RulerView";
    private OnRulerTouchListener onTouchListener;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        rulerColor = Color.rgb(35, 35, 35);
        powerColor = Color.argb(80, 255, 100, 0);

        mOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterPaint.setStrokeCap(Paint.Cap.BUTT);
        mPowerPaint = new Paint(mOuterPaint);

        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setColor(rulerColor);
        mOuterPaint.setStrokeWidth(1);
        mOuterPaint.setStrokeJoin(Paint.Join.ROUND);

        mPowerPaint.setStyle(Paint.Style.FILL);
        mPowerPaint.setStrokeWidth(paintWidth * 2);
        float[] positions = new float[]{0, .25f, .5f, .75f, 1};
        int[] colors = new int[]{
                getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color0)
        };
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, getHeight(), colors, positions, Shader.TileMode.CLAMP);
//        mPowerPaint.setShader(linearGradient);
        mPowerPaint.setColor(getResources().getColor(R.color.color4));

        mRulerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRulerPaint.setColor(rulerColor);
    }

    private Paint mOuterPaint;
    private Paint mPowerPaint;
    private Paint mRulerPaint;
    private int percent = 0;
    private int rulerColor;
    private int powerColor;
    private final int paintWidth = 15;
    private boolean start = false;

    public void setPercent(int percent) {
        this.percent = percent;
        postInvalidate();
    }

    public void setRulerColor(int rulerColor) {
        this.rulerColor = rulerColor;
    }

    public void setPowerColor(int powerColor) {
        this.powerColor = powerColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float h = getHeight() / 10;
        float hh = h / 10;
        Rect rect = new Rect(getWidth() / 2 - paintWidth, 0, getWidth() / 2 + paintWidth, getHeight());
        canvas.drawRect(rect, mOuterPaint);

        for (int i = 0; i < 11; i++) {
            if (i % 2 == 0) {
                mRulerPaint.setStrokeWidth(4);
            } else {
                mRulerPaint.setStrokeWidth(2);
            }
            canvas.drawLine(getWidth() / 2 - paintWidth, 2 + h * i, getWidth() / 2 + paintWidth, 2 + h * i, mRulerPaint);
        }
        canvas.drawLine(getWidth() / 2, getHeight(), getWidth() / 2, getHeight() - percent * hh, mPowerPaint);
    }

    private void startAnim() {
        if (runnable == null) runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i++) {
                    setPercent(i);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                setPercent(0);
                start = false;
                thread = null;
                runnable = null;
            }
        };
        if (thread == null) {
            thread = new Thread(runnable);
            thread.start();
        }

    }

    Runnable runnable;

    Thread thread;

    public void start() {
        setPercent(0);
        startAnim();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                onTouchListener.onTouch();
                break;
        }
        return true;
    }

    public interface OnRulerTouchListener{
        void onTouch();
    }

    public void setOnRulerTouchListener(OnRulerTouchListener listener){
        this.onTouchListener = listener;
    }
}
