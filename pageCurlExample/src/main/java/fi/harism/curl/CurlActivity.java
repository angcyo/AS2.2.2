/*
   Copyright 2012 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package fi.harism.curl;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Simple Activity for curl testing.
 *
 * @author harism
 */
public class CurlActivity extends Activity {

    private static final String TAG = "test";
    CurlHelper mCurlHelper = new CurlHelper();
    private CurlView mCurlView;
    private MultiTouchImageView mTouchImageView;

    private int[] mBitmapIds = {R.drawable.obama, R.drawable.road_rage,
            R.drawable.taipei_101, R.drawable.world};
    int mIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        int index = 0;
        if (getLastNonConfigurationInstance() != null) {
            index = (Integer) getLastNonConfigurationInstance();
        }
        mCurlView = (CurlView) findViewById(R.id.curl);
        mCurlView.setPageProvider(new PageProvider());
////        final TouchDrawable touchDrawable = new TouchDrawable(mCurlView);
////        mCurlView.setPageProvider(touchDrawable);
////        mCurlView.setSizeChangedObserver(new SizeChangedObserver());
//        mCurlView.setCurrentIndex(index);
//        mCurlView.setBackgroundColor(Color.TRANSPARENT);

//        mCurlView.setBackgroundColor(0xFF202830);
//        mCurlView.post(new Runnable() {
//            @Override
//            public void run() {
//                touchDrawable.setMaxBounds(mCurlView.getMeasuredWidth(), mCurlView.getMeasuredHeight());
//            }
//        });

        // This is something somewhat experimental. Before uncommenting next
        // line, please see method comments in CurlView.
        // mCurlView.setEnableTouchPressure(true);

        mTouchImageView = (MultiTouchImageView) findViewById(R.id.image_view);
        mTouchImageView.setDrawable(getDrawable(R.drawable.obama));
    }

    public void onButton(View view) {
//        int currentIndex = mCurlView.getCurrentIndex();
//        if (currentIndex >= 4) {
//            currentIndex = 0;
//        } else {
//            currentIndex++;
//        }
//        mCurlView.setCurrentIndex(currentIndex);
//        mCurlView.startCurl(2);

        mCurlView.setCurrentIndex(mIndex);

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
                mIndex++;
                mIndex = mIndex % mBitmapIds.length;
                mTouchImageView.setDrawable(getDrawable(mBitmapIds[mIndex]));
                mTouchImageView.animate().alpha(1f).setDuration(300).start();
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
        mTouchImageView.animate().alpha(0f).setDuration(300).start();

//        if (mCurlHelper.isStart()) {
//            return;
//        }
//
////        CurlHelper.startCurl((ViewGroup) findViewById(R.id.layout));
//        mCurlHelper.initCurlView((ViewGroup) findViewById(R.id.layout));
//        mTouchImageView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mTouchImageView.animate().alpha(0f).setDuration(300).start();
//                mCurlHelper.startCurl(new Runnable() {
//                    @Override
//                    public void run() {
//                        mTouchImageView.setDrawable(getDrawable(R.drawable.road_rage));
//                        mTouchImageView.setAlpha(1f);
////                        mTouchImageView.animate().alpha(1f).setDuration(300).start();
//                    }
//                });
//            }
//        }, 100);

    }

    @Override
    public void onPause() {
        super.onPause();
//        mCurlView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mCurlView.onResume();
    }

//    @Override
//    public Object onRetainNonConfigurationInstance() {
//        return mCurlView.getCurrentIndex();
//    }

    /**
     * Bitmap provider.
     */
    private class PageProvider implements CurlView.PageProvider {

        Matrix mMatrix = new Matrix();
        // Bitmap resources.
        private int[] mBitmapIds = {R.drawable.obama, R.drawable.road_rage,
                R.drawable.taipei_101, R.drawable.world};

        @Override
        public int getPageCount() {
            return 5;
        }

        private Bitmap loadBitmap(int width, int height, int index) {
            Log.i(TAG, "loadBitmap: " + index);
            Bitmap b
                    = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
//			b.eraseColor(0xFFFFFFFF);
//            b.eraseColor(Color.BLACK);
            Canvas c = new Canvas(b);
            Drawable d = getResources().getDrawable(mBitmapIds[index]);

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
                // First case is image on front side, solid colored back.
                case 0: {
                    Bitmap front = loadBitmap(width, height, 0);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
                    page.setTexture(front, CurlPage.SIDE_BOTH);
//                    page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);
                    break;
                }
                // Second case is image on back side, solid colored front.
                case 1: {
                    Bitmap back = loadBitmap(width, height, 1);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
                    page.setTexture(back, CurlPage.SIDE_BOTH);
//                    page.setColor(Color.rgb(127, 140, 180), CurlPage.SIDE_FRONT);
                    break;
                }
                // Third case is images on both sides.
                case 2: {
                    Bitmap front = loadBitmap(width, height, 2);
                    Bitmap back = loadBitmap(width, height, 3);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
                    page.setTexture(front, CurlPage.SIDE_BOTH);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
                    break;
                }
                // Fourth case is images on both sides - plus they are blend against
                // separate colors.
                case 3: {
                    Bitmap front = loadBitmap(width, height, 3);
                    Bitmap back = loadBitmap(width, height, 1);
//                    page.setTexture(front, CurlPage.SIDE_FRONT);
                    page.setTexture(front, CurlPage.SIDE_BOTH);
//                    page.setTexture(back, CurlPage.SIDE_BACK);
//                    page.setColor(Color.argb(127, 170, 130, 255),
//                            CurlPage.SIDE_FRONT);
//                    page.setColor(Color.rgb(255, 190, 150), CurlPage.SIDE_BACK);
                    break;
                }
                // Fifth case is same image is assigned to front and back. In this
                // scenario only one texture is used and shared for both sides.
                case 4:
                    Bitmap front = loadBitmap(width, height, 0);
                    page.setTexture(front, CurlPage.SIDE_BOTH);
//                    page.setColor(Color.argb(127, 255, 255, 255),
//                            CurlPage.SIDE_BACK);
                    break;
            }
        }

        public boolean onTouch(MotionEvent event) {
            mMatrix.setScale(2, 2);
            return true;
        }

    }

    /**
     * CurlView size changed observer.
     */
    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {
//            if (w > h) {
//                mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
//                mCurlView.setMargins(.1f, .05f, .1f, .05f);
//            } else {
//                mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
//                mCurlView.setMargins(0f, .4f, .1f, .1f);
//            }
            mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
        }
    }

}
