package fi.harism.curl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by angcyo on 2016-10-30.
 */

public class TouchLayout extends RelativeLayout {

    private static final String TAG = "angcyo";
    Matrix mMatrix = new Matrix();

    public TouchLayout(Context context) {
        super(context);
    }

    public TouchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Log.i(TAG, "drawChild: ");
        mMatrix.setScale(5, 5);
        mMatrix.postTranslate(100, 800);
        canvas.concat(mMatrix);
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.i(TAG, "dispatchDraw: ");
        mMatrix.setScale(5, 5);
        mMatrix.postTranslate(100, 800);
        canvas.concat(mMatrix);
        super.dispatchDraw(canvas);
    }

    @Override
    public void draw(Canvas canvas) {
        Log.i(TAG, "draw: ");
//        canvas.save();
//        mMatrix.setScale(2, 2);
//        mMatrix.postTranslate(100, 200);
//        canvas.concat(mMatrix);
//        canvas.restore();
        super.draw(canvas);
    }
}
