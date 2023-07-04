package com.marsad.stylishdialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class RotatingAnimation extends Animation {
    public static final int ROLL_BY_X = 0;
    public static final int ROLL_BY_Y = 1;
    public static final int ROLL_BY_Z = 2;
    private final float mFromDegrees;
    private final float mToDegrees;
    private final int mRollType;
    private int mPivotXType = ABSOLUTE;
    private int mPivotYType = ABSOLUTE;
    private float mPivotXValue = 0.0f;
    private float mPivotYValue = 0.0f;
    private float mPivotX;
    private float mPivotY;
    private Camera mCamera;

    public RotatingAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotatingAnimation);

        mFromDegrees = a.getFloat(R.styleable.RotatingAnimation_fromDeg, 0.0f);
        mToDegrees = a.getFloat(R.styleable.RotatingAnimation_toDeg, 0.0f);
        mRollType = a.getInt(R.styleable.RotatingAnimation_rollType, ROLL_BY_X);
        mPivotXType = parsePivotType(a.peekValue(R.styleable.RotatingAnimation_customPivotX));
        mPivotXValue = parsePivotValue(a.peekValue(R.styleable.RotatingAnimation_customPivotX));

        mPivotYType = parsePivotType(a.peekValue(R.styleable.RotatingAnimation_customPivotY));
        mPivotYValue = parsePivotValue(a.peekValue(R.styleable.RotatingAnimation_customPivotY));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            a.close();
        } else {
            a.recycle();
        }


        initializePivotPoint();
    }

    public RotatingAnimation(int rollType, float fromDegrees, float toDegrees) {
        mRollType = rollType;
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mPivotX = 0.0f;
        mPivotY = 0.0f;
    }

    public RotatingAnimation(int rollType, float fromDegrees, float toDegrees, float pivotX, float pivotY) {
        mRollType = rollType;
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;

        mPivotXType = ABSOLUTE;
        mPivotYType = ABSOLUTE;
        mPivotXValue = pivotX;
        mPivotYValue = pivotY;
        initializePivotPoint();
    }

    public RotatingAnimation(int rollType, float fromDegrees, float toDegrees, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        mRollType = rollType;
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;

        mPivotXValue = pivotXValue;
        mPivotXType = pivotXType;
        mPivotYValue = pivotYValue;
        mPivotYType = pivotYType;
        initializePivotPoint();
    }


    private int parsePivotType(TypedValue value) {
        if (value == null) return ABSOLUTE;

        if (value.type == TypedValue.TYPE_FRACTION) {
            return (value.data & TypedValue.COMPLEX_UNIT_MASK) == TypedValue.COMPLEX_UNIT_FRACTION_PARENT ? RELATIVE_TO_PARENT : RELATIVE_TO_SELF;
        } else {
            return ABSOLUTE;
        }
    }

    private float parsePivotValue(TypedValue value) {
        if (value == null) return 0.0f;

        if (value.type == TypedValue.TYPE_FRACTION) {
            return TypedValue.complexToFloat(value.data);
        } else if (value.type == TypedValue.TYPE_FLOAT) {
            return value.getFloat();
        } else if (value.type >= TypedValue.TYPE_FIRST_INT && value.type <= TypedValue.TYPE_LAST_INT) {
            return value.data;
        }

        return 0.0f;
    }

    private void initializePivotPoint() {
        if (mPivotXType == ABSOLUTE) {
            mPivotX = mPivotXValue;
        }
        if (mPivotYType == ABSOLUTE) {
            mPivotY = mPivotYValue;
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
        mPivotX = resolveSize(mPivotXType, mPivotXValue, width, parentWidth);
        mPivotY = resolveSize(mPivotYType, mPivotYValue, height, parentHeight);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final Matrix matrix = t.getMatrix();

        mCamera.save();
        switch (mRollType) {
            case ROLL_BY_X:
                mCamera.rotateX(degrees);
                break;
            case ROLL_BY_Y:
                mCamera.rotateY(degrees);
                break;
            case ROLL_BY_Z:
                mCamera.rotateZ(degrees);
                break;
        }
        mCamera.getMatrix(matrix);
        mCamera.restore();

        matrix.preTranslate(-mPivotX, -mPivotY);
        matrix.postTranslate(mPivotX, mPivotY);
    }

}
