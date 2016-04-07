package testb.hello.pic;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Edgar.Qi on 2016/4/5.
 */
public class GesTurePwdView extends ViewGroup {

    private static final int NUM_POINT = 9;

    private static final int NUM_CLOUMN = 3;

    private static final float TOUCH_TOLERANCE = 4;

    private LayoutParams LP_WW = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    private LayoutParams LP_MM = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    private ChildPoint[] mNode;

    private float padding = 0;

    private Bitmap mBitmap;

    private Canvas mCanvas;

    private Path mPath;

    private Paint mBitmapPaint;

    private Paint mPaint/*,mClearPaint*/;

    private MaskFilter mEmboss;

    private MaskFilter mBlur;
    //每个点的宽和高
    private static final int LAYOUT_ATTR_VALUE = 120;

    private ArrayList<ChildPoint> pwdObj;

    private Drawable mNormalBg, mFocusBg;

    private LastLine mLastline;

    private PasswordInputListener mPwdInputListener;

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1},
                0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

        /*mClearPaint = new Paint();
        mClearPaint.setAntiAlias(true);
        mClearPaint.setDither(true);
        mClearPaint.setColor(0x000000FF);
        mClearPaint.setStyle(Paint.Style.STROKE);
        mClearPaint.setStrokeJoin(Paint.Join.ROUND);
        mClearPaint.setStrokeCap(Paint.Cap.ROUND);
        Xfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP);
        mClearPaint.setXfermode(mode);
        mClearPaint.setStrokeWidth(15);*/

    }

    private void initLayout() {
        mPath = new Path();
        mNode = new ChildPoint[9];
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mNormalBg = getContext().getResources().getDrawable(R.drawable.password_bao1);
        mFocusBg = getContext().getResources().getDrawable(R.drawable.password_bao8);
        for (int i = 0; i < mNode.length; i++) {
            ChildPoint child = new ChildPoint();
            child.view = new TextView(getContext());
            child.view.setLayoutParams(LP_WW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                child.view.setBackground(mNormalBg);
            } else {
                child.view.setBackgroundDrawable(mNormalBg);
            }
            child.focus = mFocusBg;
            child.normal = mNormalBg;
            child.text = String.valueOf(i);
            child.rect = new ChildRect();
            mNode[i] = child;
            addView(mNode[i].view);
//            mNode[i].view.setText(child.text);
        }
    }

    private void init() {
        pwdObj = new ArrayList<ChildPoint>();
        setWillNotDraw(false);
        setBackgroundColor(0x00000000);
        mLastline = new LastLine();

        initPaint();
        initLayout();
    }

    private void initPadding(int width) {
        int totalPadding = width - LAYOUT_ATTR_VALUE * NUM_CLOUMN;
        padding = totalPadding / 4;
    }

    public GesTurePwdView(Context context) {
        super(context);
        init();
    }

    public GesTurePwdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public GesTurePwdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GesTurePwdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width_parent = MeasureSpec.getSize(widthMeasureSpec);
        int height_parent = MeasureSpec.getSize(heightMeasureSpec);
        int childWidth = LAYOUT_ATTR_VALUE;
        int childHeight = LAYOUT_ATTR_VALUE;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(childWidth, childHeight);
        }
        initPadding(width_parent);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int index = 0;
        for (int i = 1; i <= NUM_CLOUMN; i++) {
            for (int j = 1; j <= count / NUM_CLOUMN; j++) {
                int left = (int) (padding * j + LAYOUT_ATTR_VALUE * (j - 1));
                int top = (int) (padding * i + LAYOUT_ATTR_VALUE * (i - 1));
                int right = (int) ((padding + LAYOUT_ATTR_VALUE) * j);
                int bottom = (int) ((padding + LAYOUT_ATTR_VALUE) * i);
                mNode[index].view.layout(left, top, right, bottom);
                mNode[index].rect.setRect(left, top, right, bottom);
                mNode[index].rect.index = index;
                index++;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0x00000000);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
//        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;

    private boolean isNodeIn(final int index) {
        synchronized (GesTurePwdView.class) {
            if (pwdObj.size() == 0) {
//                pwdObj.add(mNode[index]);
                return false;
            }
            for (int i = 0; i < pwdObj.size(); i++) {
                int node = pwdObj.get(i).getIndex();
                if (node == index) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        mX = x;
        mY = y;
        setMovePath(x, y);
        //覆盖之前的画线
    /*    if(getLastPoint()!=null) {
            float xTemp = getLastPoint().rect.center_x;
            float yTemp = getLastPoint().rect.center_y;
            mCanvas.drawLine(mLastline.x,mLastline.y,mLastline.mx,mLastline.my,mClearPaint);
            mCanvas.drawLine(xTemp, yTemp, mX, mY, mPaint);
            mLastline.x = xTemp;
            mLastline.y = yTemp;
            mLastline.mx = mX;
            mLastline.my = mY;
            invalidate();
        }*/


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                mPath.moveTo(mNode[i].rect.center_x,mNode[i].rect.center_y);
//                touch_start(x, y);
//                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
//                touch_move(x, y);
//                invalidate();
                for (int i = 0; i < mNode.length; i++) {
                    if (!mNode[i].isChecked()) {
                        boolean isFingerIn = mNode[i].isFingerIn(x, y);
                        mNode[i].setBgState(isFingerIn);
                        if (isFingerIn) {
                            boolean isRecord = isNodeIn(i);
                            if (!isRecord) {
                                pwdObj.add(mNode[i]);
//                                mPath.moveTo(mNode[i].rect.center_x,mNode[i].rect.center_y);
//                                if(pwdObj.size()!=1){
                                mPath.lineTo(mNode[i].rect.center_x, mNode[i].rect.center_y);
                                mCanvas.drawPath(mPath, mPaint);
//                                }

                                invalidate();
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
//                touch_up();
//                invalidate();
                ArrayList<Integer> list = getPwdList();
                mPwdInputListener.setPassword(list);
                initializeState();
                break;
        }
        return true;
    }

    private void setMovePath(float x, float y) {
        Log.d("Gesturepwd", "pwdObj.size():" + pwdObj.size());
        if (pwdObj.size() != 0) {
            return;
        }
        for (int i = 0; i < mNode.length; i++) {
            boolean isFingerIn = mNode[i].isFingerIn(x, y);
            Log.d("Gesturepwd", "isFingerIn:" + isFingerIn);
            if (isFingerIn == true) {
                int xx = mNode[i].rect.center_x;
                int yy = mNode[i].rect.center_y;
                Log.d("Gesturepwd", "Path move to X:" + xx + " ---Y:" + yy);
                mPath.moveTo(xx, yy);
                break;
            }
        }
    }

    private ArrayList<Integer> getPwdList() {
        if (pwdObj != null && pwdObj.size() != 0) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < pwdObj.size(); i++) {
                int content = pwdObj.get(i).getIndex();
                list.add(content);
            }
            return list;
        }
        return null;
    }

    private void initializeState() {
        mPath.reset();
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        pwdObj.clear();
        for (int i = 0; i < mNode.length; i++) {
            mNode[i].setBgState(false);
        }
    }

    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    class ChildPoint {
        String text = null;
        Drawable normal = null;
        Drawable focus = null;
        TextView view = null;
        ChildRect rect = null;
        boolean selected = false;

        ChildPoint() {
            rect = new ChildRect();
        }

        public void setBgState(boolean selected) {
            this.selected = selected;
            if (selected) {
                //防止重复设置
                if (view.getBackground() == focus)
                    return;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(focus);
                } else {
                    view.setBackgroundDrawable(focus);
                }
            } else {
                //防止重复设置
                if (view.getBackground() == normal)
                    return;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackground(normal);
                } else {
                    view.setBackgroundDrawable(normal);
                }
            }
        }

        public boolean isChecked() {
            return selected;
        }

        private boolean isFingerIn(float x, float y) {
            return rect.getRectIn(x, y);
        }

        public int getIndex() {
            return rect.index;
        }

    }

    class ChildRect {
        int left;
        int top;
        int right;
        int bottom;
        int index = -1;
        int center_x, center_y;


        public boolean getRectIn(float x, float y) {
            if (x > left && x < right && y > top && y < bottom) {
                return true;
            }
            return false;
        }

        public void setRect(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            initCenter();
        }

        private void initCenter() {
            center_x = Math.abs(left + (right - left) / 2);
            center_y = Math.abs(top + (bottom - top) / 2);
        }

        public int getIndex() {
            return index;
        }
    }

    class LastLine {
        float x;
        float y;
        float mx;
        float my;
        boolean isInit = false;
    }

    private ChildPoint getLastPoint() {
        if (pwdObj.size() == 0) {
            return null;
        }
        return pwdObj.get(pwdObj.size() - 1);
    }


    public void setPwdListener(PasswordInputListener listener) {
        this.mPwdInputListener = listener;
    }


}
