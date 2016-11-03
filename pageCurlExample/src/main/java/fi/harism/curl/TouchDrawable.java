package fi.harism.curl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by angcyo on 2016-10-30.
 */

public class TouchDrawable implements CurlView.PageProvider {
    private static final String TAG = "angcyo";
    private View mTarget;
    private Drawable mDrawable;
    /**
     * 平移,旋转, 放大的 矩阵
     */
    private Matrix mMatrix;
    private Matrix mDownMatrix;
    /**
     * 最大绘制范围
     */
    private RectF mMaxBounds;
    /**
     * 实际绘制范围
     */
    private Rect mRealBounds;

    private Mode mCurrentMode = Mode.NONE;

    private float mDownX;
    private float mDownY;

    /**
     * 2个手指按下时的距离
     */
    private float mOldDistance = 0f;
    /**
     * 2个手指按下时旋转的角度
     */
    private float mOldRotation = 0f;
    /**
     * 2个手指中间点的坐标
     */
    private PointF mMidPoint;

    private int mCurIndex = -1;

    private int[] mBitmapIds = {R.drawable.obama, R.drawable.road_rage,
            R.drawable.taipei_101, R.drawable.world};

    public TouchDrawable(View target) {
        mTarget = target;
        mMatrix = new Matrix();
        mDownMatrix = new Matrix();
        mMaxBounds = new RectF();
    }

    public void setMaxBounds(int width, int height) {
        mMaxBounds.right = width;
        mMaxBounds.bottom = height;
        resetRealBound(0);
    }

    @Override
    public int getPageCount() {
        return mBitmapIds.length;
    }

    @Override
    public void updatePage(CurlPage page, int width, int height, int index) {
        Bitmap front = loadBitmap(width, height, index);
        page.setTexture(front, CurlPage.SIDE_BOTH);
    }

    private Bitmap loadBitmap(int width, int height, int index) {
        Bitmap b = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        b.eraseColor(Color.BLACK);//图片以外区域的颜色
        Canvas c = new Canvas(b);
        mDrawable = mTarget.getResources().getDrawable(mBitmapIds[index]);
        resetRealBound(index);
        mDrawable.setBounds(mRealBounds);
        c.concat(mMatrix);
        mDrawable.draw(c);
        return b;
    }

    @Override
    public boolean onTouch(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN://第一个手指按下
                mDownX = event.getX();
                mDownY = event.getY();
                if (canMove()) {
                    mCurrentMode = Mode.DRAG;
                }
                mDownMatrix.set(mMatrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN://第二个手指按下
                mOldDistance = calculateDistance(event);
                mOldRotation = calculateRotation(event);
                mMidPoint = calculateMidPoint(event);
                mCurrentMode = Mode.ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mCurrentMode = Mode.NONE;
                if (getScaleFactor() > 2f) {
                    //最大允许放大2倍
                    mMatrix.postScale(2f / getScaleFactor(), 2f / getScaleFactor(), mMidPoint.x, mMidPoint.y);
                } else if (getScaleFactor() < 1.0f) {
                    //最小的倍数
                    mMatrix.postScale(1f / getScaleFactor(), 1f / getScaleFactor(), mMidPoint.x, mMidPoint.y);
                }

                //修正4个边
                final RectF bounds = getRealBounds();
                float dx = 0f, dy = 0f;
                if (bounds.top > 0 && bounds.bottom > mMaxBounds.bottom) {
                    dy = -bounds.top;
                } else if (bounds.bottom < mMaxBounds.bottom && bounds.top < 0) {
                    dy = mMaxBounds.bottom - bounds.bottom;
                }

                if (bounds.left > 0 && bounds.right > mMaxBounds.right) {
                    dx = -bounds.left;
                } else if (bounds.right < mMaxBounds.right && bounds.left < 0) {
                    dx = mMaxBounds.right - bounds.right;
                }
                mMatrix.postTranslate(dx, dy);
                break;
        }
        return true;
    }

    private void onMove(MotionEvent event) {
        switch (mCurrentMode) {
            case NONE:
                break;
            case DRAG:
                mMatrix.set(mDownMatrix);
                mMatrix.postTranslate(event.getX() - mDownX, event.getY() - mDownY);
                break;
            case ZOOM:
                /**两点之间的距离*/
                float newDistance = calculateDistance(event);
                float newRotation = calculateRotation(event);
                final float scale = newDistance / mOldDistance;
                final float rotate = newRotation - mOldRotation;
                mMatrix.set(mDownMatrix);
                mMatrix.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
//                    mMatrix.postRotate(rotate, mMidPoint.x, mMidPoint.y);//注释此行, 禁用旋转
                Log.w(TAG, "onMove: old:" + mOldDistance + " new:" + newDistance);
                Log.i(TAG, "onMove: 放大:" + scale + " 旋转:" + rotate);
                //zoomPiece(mHandlingPiece, event);
                break;
            case MOVE:
                //moveLine(event);
                //mPuzzleLayout.update();
                //updatePieceInBorder(event);
                break;

            case SWAP:
                //mReplacePiece = findReplacePiece(event);
                //dragPiece(mHandlingPiece, event);
                break;
        }
        //Log.i(TAG, "onMove: 缩放倍数:" + getScaleFactor());
    }


    private void resetRealBound(int index) {
        if (mCurIndex == index) {
            return;
        }

        if (mDrawable == null) {
            return;
        }
        if (mRealBounds == null) {
            mRealBounds = new Rect();
        }
        final int maxW = (int) (mMaxBounds.width() / 2);
        final int maxH = (int) (mMaxBounds.height() / 2);
        final int realW = mDrawable.getIntrinsicWidth() / 2;
        final int realH = mDrawable.getIntrinsicHeight() / 2;
        mRealBounds.set(maxW - realW, maxH - realH, maxW + realW, maxH + realH);
//            mRealBounds.set(0, 0, (int) mMaxBounds.width(), (int) mMaxBounds.height());
        mCurIndex = index;
     }

    /**
     * 如果图片完全显示在容器内,则不可以移动
     */
    private boolean canMove() {
        return !mMaxBounds.contains(getRealBounds());
    }

    /**
     * 获取当前缩放的倍数
     */
    private float getScaleFactor() {
        return getRealBounds().width() / mRealBounds.width();
    }

    private RectF getRealBounds() {
        RectF dst = new RectF();
        mMatrix.mapRect(dst, new RectF(mRealBounds));
        return dst;
    }

    /**
     * 计算两个手指之间的距离
     */
    private float calculateDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两个手指中间点的坐标
     */
    private PointF calculateMidPoint(MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }

    /**
     * 计算两点形成的直线与x轴的旋转角度
     */
    private float calculateRotation(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return 0f;
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    private enum Mode {
        NONE,
        DRAG,
        ZOOM,
        MOVE,
        SWAP
    }
}
