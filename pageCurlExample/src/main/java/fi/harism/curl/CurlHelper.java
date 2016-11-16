package fi.harism.curl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import static android.content.ContentValues.TAG;

/**
 * Created by angcyo on 2016-11-16.
 */

public class CurlHelper {

    boolean isStart = false;
    CurlView mCurlView;
    ViewGroup mParent;

    public boolean isStart() {
        return isStart;
    }

    public void initCurlView(ViewGroup parent) {
        if (mCurlView != null) {
            return;
        }
        mParent = parent;
        mCurlView = new CurlView(parent.getContext());
        parent.addView(mCurlView, new ViewGroup.LayoutParams(-1, -1));
        mCurlView.setPageProvider(new PageProvider());
    }

    public void startCurl(final Runnable endRunnable) {
        if (mCurlView == null || isStart) {
            return;
        }

        isStart = true;

        final int measuredWidth = mCurlView.getMeasuredWidth();
        final int measuredHeight = mCurlView.getMeasuredHeight();
        Log.i(TAG, "onButton: " + measuredWidth + "  " + measuredHeight);
//        mCurlView.onTouch(mCurlView, MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(),
//                MotionEvent.ACTION_DOWN, measuredWidth - 100, measuredHeight - 200, 0));
//        mCurlView.onTouch(mCurlView, MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 3000,
//                MotionEvent.ACTION_UP, 100, 200, 0));

        mCurlView.onTouch(mCurlView, MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN, measuredWidth, measuredHeight, 0));
        final ValueAnimator animator = ObjectAnimator.ofInt(measuredWidth, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
//                Log.e(TAG, "onAnimationUpdate: " + value);
                if (value <= 0) {
                    mCurlView.onTouch(mCurlView, MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(),
                            MotionEvent.ACTION_UP, value, measuredHeight * (measuredWidth - value) / measuredWidth, 0));
                    animation.cancel();
                } else {
                    mCurlView.onTouch(mCurlView, MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(),
                            MotionEvent.ACTION_MOVE, value, measuredHeight * (1 - (measuredWidth - value) / (measuredWidth * 1f)), 0));
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                endRunnable.run();
                if (mParent != null) {
                    mParent.removeView(mCurlView);
                    mCurlView = null;
                    mParent = null;
                    isStart = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(1000);
        animator.start();
    }


    private class PageProvider implements CurlView.PageProvider {

        Matrix mMatrix = new Matrix();
        // Bitmap resources.
        private int[] mBitmapIds = {R.drawable.obama, R.drawable.road_rage,
                R.drawable.taipei_101, R.drawable.world};

        @Override
        public int getPageCount() {
            return 2;
        }

        private Bitmap loadBitmap(int width, int height, int index) {
            Log.i(TAG, "loadBitmap: " + index);
            Bitmap b = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
//			b.eraseColor(0xFFFFFFFF);
//            b.eraseColor(Color.BLACK);
            Canvas c = new Canvas(b);
            Drawable d = mCurlView.getResources().getDrawable(mBitmapIds[index]);

            int margin = 7;
            int border = 3;
            Rect r = new Rect(margin, margin, width - margin, height - margin);

            int imageWidth = r.width() - (border * 2);
            int imageHeight = imageWidth * d.getIntrinsicHeight()
                    / d.getIntrinsicWidth();
            if (imageHeight > r.height() - (border * 2)) {
                imageHeight = r.height() - (border * 2);
                imageWidth = imageHeight * d.getIntrinsicWidth()
                        / d.getIntrinsicHeight();
            }

            r.left += ((r.width() - imageWidth) / 2) - border;
            r.right = r.left + imageWidth + border + border;
            r.top += ((r.height() - imageHeight) / 2) - border;
            r.bottom = r.top + imageHeight + border + border;

            Paint p = new Paint();
//            p.setColor(0xFFC0C0C0);
//            c.drawRect(r, p);
            r.left += border;
            r.right -= border;
            r.top += border;
            r.bottom -= border;
//            d.setBounds(width / 2 - d.getIntrinsicWidth() / 2, height / 2 - d.getIntrinsicHeight() / 2,
//                    width / 2 + d.getIntrinsicWidth() / 2, height / 2 + d.getIntrinsicHeight() / 2);
//			d.setBounds(r);
//            c.concat(mMatrix);
//            c.drawColor(Color.RED);
            d.setBounds(0, 0, width, height);
            d.draw(c);

            return b;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {
            Log.i(TAG, "updatePage: " + index);
            switch (index) {
                case 0: {
                    Bitmap front = loadBitmap(width, height, 0);
                    page.setTexture(front, CurlPage.SIDE_BOTH);
                    break;
                }
                case 1: {
                    Bitmap back = loadBitmap(width, height, 1);
                    page.setTexture(back, CurlPage.SIDE_BOTH);
                    break;
                }
            }
        }
    }
}
