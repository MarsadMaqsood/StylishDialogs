package com.marsad.stylishdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

public class StylishAlertDialog extends Dialog implements View.OnClickListener {
    public static final int NORMAL = 0;
    public static final int ERROR = 1;
    public static final int SUCCESS = 2;
    public static final int WARNING = 3;
    public static final int CUSTOM_IMAGE = 4;
    public static final int PROGRESS = 5;
    //aliases
    public final static int BUTTON_CONFIRM = DialogInterface.BUTTON_POSITIVE;
    public final static int BUTTON_CANCEL = DialogInterface.BUTTON_NEGATIVE;
    public static boolean DARK_STYLE = false;
    private final float defStrokeWidth;
    private View mDialogView;
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private Animation mOverlayOutAnim;
    private Animation mErrorInAnim;
    private AnimationSet mErrorXInAnim;
    private AnimationSet mSuccessLayoutAnimSet;
    private Animation mSuccessBowAnim;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private FrameLayout mCustomViewContainer;
    private View mCustomView;
    private String mTitleText;
    private String mContentText;
    private boolean mShowCancel;
    private boolean mShowContent;
    private String mCancelText;
    private String mConfirmText;
    private String mNeutralText;
    private int mAlertType;
    private FrameLayout mErrorFrame;
    private FrameLayout mSuccessFrame;
    private FrameLayout mProgressFrame;
    private SuccessView mSuccessTick;
    private ImageView mErrorX;
    private View mSuccessLeftMask;
    private View mSuccessRightMask;
    private Drawable mCustomImgDrawable;
    private ImageView mCustomImage;
    private LinearLayout mButtonsContainer;
    private Button mConfirmButton;
    private boolean mHideConfirmButton = false;
    private Button mCancelButton;
    private Button mNeutralButton;
    private Integer mConfirmButtonBackgroundColor;
    private Integer mConfirmButtonTextColor;
    private Integer mNeutralButtonBackgroundColor;
    private Integer mNeutralButtonTextColor;
    private Integer mCancelButtonBackgroundColor;
    private Integer mCancelButtonTextColor;
    private PGHelper mProgressHelper;
    private FrameLayout mWarningFrame;
    private OnStylishClickListener mCancelClickListener;
    private OnStylishClickListener mConfirmClickListener;
    private OnStylishClickListener mNeutralClickListener;
    private boolean mCloseFromCancel;
    private boolean mHideKeyBoardOnDismiss = true;
    private int contentTextSize = 0;
    private float strokeWidth = 0;


    public StylishAlertDialog(Context context) {
        this(context, NORMAL);
    }

    public StylishAlertDialog(Context context, int alertType) {
        super(context, DARK_STYLE ? R.style.alert_dialog_dark : R.style.alert_dialog_light);
        setCancelable(true);
        setCanceledOnTouchOutside(true); //TODO was false

        defStrokeWidth = getContext().getResources().getDimension(R.dimen.buttons_stroke_width);
        strokeWidth = defStrokeWidth;

        mProgressHelper = new PGHelper(context);
        mAlertType = alertType;
        mErrorInAnim = AnimLoader.loadAnimation(getContext(), R.anim.error_layout_in);
        mErrorXInAnim = (AnimationSet) AnimLoader.loadAnimation(getContext(), R.anim.error_in);
        // 2.3.x system don't support alpha-animation on layer-list drawable
        // remove it from animation set
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            List<Animation> childAnim = mErrorXInAnim.getAnimations();
            int idx = 0;
            for (; idx < childAnim.size(); idx++) {
                if (childAnim.get(idx) instanceof AlphaAnimation) {
                    break;
                }
            }
            if (idx < childAnim.size()) {
                childAnim.remove(idx);
            }
        }
        mSuccessBowAnim = AnimLoader.loadAnimation(getContext(), R.anim.success_rotate);
        mSuccessLayoutAnimSet = (AnimationSet) AnimLoader.loadAnimation(getContext(), R.anim.success_layout_rotate);
        mModalInAnim = (AnimationSet) AnimLoader.loadAnimation(getContext(), R.anim.model_in);
        mModalOutAnim = (AnimationSet) AnimLoader.loadAnimation(getContext(), R.anim.model_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                if (mHideKeyBoardOnDismiss) {
                    hideSoftKeyboard();
                }
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            StylishAlertDialog.super.cancel();
                        } else {
                            StylishAlertDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // dialog overlay fade out
        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                WindowManager.LayoutParams wlp = getWindow().getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                getWindow().setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public StylishAlertDialog hideConfirmButton() {
        this.mHideConfirmButton = true;
        return this;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stylish_dialogs);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = findViewById(R.id.title_text);
        mContentTextView = findViewById(R.id.content_text);
        mCustomViewContainer = findViewById(R.id.custom_view_container);
        mErrorFrame = findViewById(R.id.error_frame);
        mErrorX = mErrorFrame.findViewById(R.id.error_x);
        mSuccessFrame = findViewById(R.id.success_frame);
        mProgressFrame = findViewById(R.id.progress_dialog);
        mSuccessTick = mSuccessFrame.findViewById(R.id.success_tick);
        mSuccessLeftMask = mSuccessFrame.findViewById(R.id.mask_left);
        mSuccessRightMask = mSuccessFrame.findViewById(R.id.mask_right);
        mCustomImage = findViewById(R.id.custom_image);
        mWarningFrame = findViewById(R.id.warning_frame);
        mButtonsContainer = findViewById(R.id.buttons_container);
        mConfirmButton = findViewById(R.id.confirm_button);
        mConfirmButton.setOnClickListener(this);
        mConfirmButton.setOnTouchListener(Constants.FOCUS_TOUCH_LISTENER);
        mCancelButton = findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(this);
        mCancelButton.setOnTouchListener(Constants.FOCUS_TOUCH_LISTENER);
        mNeutralButton = findViewById(R.id.neutral_button);
        mNeutralButton.setOnClickListener(this);
        mNeutralButton.setOnTouchListener(Constants.FOCUS_TOUCH_LISTENER);
        mProgressHelper.setProgressWheel((ProgressWheel) findViewById(R.id.progressWheel));

        setTitleText(mTitleText);
        setContentText(mContentText);
        setCustomView(mCustomView);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        setNeutralText(mNeutralText);
        applyStroke();
        setConfirmButtonBackgroundColor(mConfirmButtonBackgroundColor);
        setConfirmButtonTextColor(mConfirmButtonTextColor);
        setCancelButtonBackgroundColor(mCancelButtonBackgroundColor);
        setCancelButtonTextColor(mCancelButtonTextColor);
        setNeutralButtonBackgroundColor(mNeutralButtonBackgroundColor);
        setNeutralButtonTextColor(mNeutralButtonTextColor);
        changeAlertType(mAlertType, true);

    }

    private void restore() {
        mCustomImage.setVisibility(View.GONE);
        mErrorFrame.setVisibility(View.GONE);
        mSuccessFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mProgressFrame.setVisibility(View.GONE);

        mConfirmButton.setVisibility(mHideConfirmButton ? View.GONE : View.VISIBLE);

        adjustButtonContainerVisibility();

        mConfirmButton.setBackgroundResource(R.drawable.btn_bg_green);
        mErrorFrame.clearAnimation();
        mErrorX.clearAnimation();
        mSuccessTick.clearAnimation();
        mSuccessLeftMask.clearAnimation();
        mSuccessRightMask.clearAnimation();
    }

    /**
     * Hides buttons container if all buttons are invisible or gone.
     * This deletes useless margins
     */
    private void adjustButtonContainerVisibility() {
        boolean showButtonsContainer = false;
        for (int i = 0; i < mButtonsContainer.getChildCount(); i++) {
            View view = mButtonsContainer.getChildAt(i);
            if (view instanceof Button && view.getVisibility() == View.VISIBLE) {
                showButtonsContainer = true;
                break;
            }
        }
        mButtonsContainer.setVisibility(showButtonsContainer ? View.VISIBLE : View.GONE);
    }

    private void playAnimation() {
        if (mAlertType == ERROR) {
            mErrorFrame.startAnimation(mErrorInAnim);
            mErrorX.startAnimation(mErrorXInAnim);
        } else if (mAlertType == SUCCESS) {
            mSuccessTick.startTickAnim();
            mSuccessRightMask.startAnimation(mSuccessBowAnim);
        }
    }

    private void changeAlertType(int alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            mConfirmButton.setVisibility(mHideConfirmButton ? View.GONE : View.VISIBLE);
            switch (mAlertType) {
                case ERROR:
                    mErrorFrame.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    mSuccessFrame.setVisibility(View.VISIBLE);
                    // initial rotate layout of success mask
                    mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
                    mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
                    break;
                case WARNING:
//                    mConfirmButton.setBackgroundResource(R.drawable.red_button_background);
                    mWarningFrame.setVisibility(View.VISIBLE);
                    break;
                case CUSTOM_IMAGE:
                    setCustomImage(mCustomImgDrawable);
                    break;
                case PROGRESS:
                    mProgressFrame.setVisibility(View.VISIBLE);
                    mConfirmButton.setVisibility(View.GONE);
//                    mButtonsContainer.setVisibility(View.GONE);
                    break;
            }
            adjustButtonContainerVisibility();
            if (!fromCreate) {
                playAnimation();
            }
        }
    }

    public int getAlertType() {
        return mAlertType;
    }

    public void changeAlertType(int alertType) {
        changeAlertType(alertType, false);
    }

    public String getTitleText() {
        return mTitleText;
    }

    public StylishAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            if (text.isEmpty()) {
                mTitleTextView.setVisibility(View.GONE);
            } else {
                mTitleTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setText(Html.fromHtml(mTitleText));
            }
        }
        return this;
    }

    public StylishAlertDialog setTitleText(int resId) {
        return setTitleText(getContext().getResources().getString(resId));
    }

    public StylishAlertDialog setCustomImage(Drawable drawable) {
        mCustomImgDrawable = drawable;
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mCustomImage.setVisibility(View.VISIBLE);
            mCustomImage.setImageDrawable(mCustomImgDrawable);
        }
        return this;
    }

    public StylishAlertDialog setCustomImage(int resourceId) {
        return setCustomImage(getContext().getResources().getDrawable(resourceId));
    }

    public String getContentText() {
        return mContentText;
    }

    /**
     * @param text text which can contain html tags.
     */
    public StylishAlertDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            if (contentTextSize != 0) {
                mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, spToPx(contentTextSize, getContext()));
            }
            mContentTextView.setText(Html.fromHtml(mContentText));
            mContentTextView.setVisibility(View.VISIBLE);
            mCustomViewContainer.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * @param width in SP
     */
    public StylishAlertDialog setStrokeWidth(float width) {
        this.strokeWidth = spToPx(width, getContext());
        return this;
    }

    private void applyStroke() {
        if (Float.compare(defStrokeWidth, strokeWidth) != 0) {
            Resources r = getContext().getResources();
            setButtonBackgroundColor(mConfirmButton, r.getColor(R.color.main_green_color));
            setButtonBackgroundColor(mNeutralButton, r.getColor(R.color.main_disabled_color));
            setButtonBackgroundColor(mCancelButton, r.getColor(R.color.red_btn_bg_color));
        }
    }

    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public StylishAlertDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public boolean isShowContentText() {
        return mShowContent;
    }

    public StylishAlertDialog showContentText(boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public StylishAlertDialog setCancelText(String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public String getConfirmText() {
        return mConfirmText;
    }

    public StylishAlertDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public Integer getConfirmButtonBackgroundColor() {
        return mConfirmButtonBackgroundColor;
    }

    public StylishAlertDialog setConfirmButtonBackgroundColor(Integer color) {
        mConfirmButtonBackgroundColor = color;
        setButtonBackgroundColor(mConfirmButton, color);
        return this;
    }

    public Integer getNeutralButtonBackgroundColor() {
        return mNeutralButtonBackgroundColor;
    }

    public StylishAlertDialog setNeutralButtonBackgroundColor(Integer color) {
        mNeutralButtonBackgroundColor = color;
        setButtonBackgroundColor(mNeutralButton, color);
        return this;
    }

    public Integer getCancelButtonBackgroundColor() {
        return mCancelButtonBackgroundColor;
    }

    public StylishAlertDialog setCancelButtonBackgroundColor(Integer color) {
        mCancelButtonBackgroundColor = color;
        setButtonBackgroundColor(mCancelButton, color);
        return this;
    }

    private void setButtonBackgroundColor(Button btn, Integer color) {
        if (btn != null && color != null) {
            Drawable[] drawableItems = ViewUtils.getDrawable(btn);
            if (drawableItems != null) {
                GradientDrawable gradientDrawableUnChecked = (GradientDrawable) drawableItems[1];
                //solid color
                gradientDrawableUnChecked.setColor(color);
                //stroke
                gradientDrawableUnChecked.setStroke((int) strokeWidth, genStrokeColor(color));
            }
        }
    }

    private int genStrokeColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.7f; // decrease value component
        return Color.HSVToColor(hsv);
    }

    public Integer getConfirmButtonTextColor() {
        return mConfirmButtonTextColor;
    }

    public StylishAlertDialog setConfirmButtonTextColor(Integer color) {
        mConfirmButtonTextColor = color;
        if (mConfirmButton != null && color != null) {
            mConfirmButton.setTextColor(mConfirmButtonTextColor);
        }
        return this;
    }

    public Integer getNeutralButtonTextColor() {
        return mNeutralButtonTextColor;
    }

    public StylishAlertDialog setNeutralButtonTextColor(Integer color) {
        mNeutralButtonTextColor = color;
        if (mNeutralButton != null && color != null) {
            mNeutralButton.setTextColor(mNeutralButtonTextColor);
        }
        return this;
    }

    public Integer getCancelButtonTextColor() {
        return mCancelButtonTextColor;
    }

    public StylishAlertDialog setCancelButtonTextColor(Integer color) {
        mCancelButtonTextColor = color;
        if (mCancelButton != null && color != null) {
            mCancelButton.setTextColor(mCancelButtonTextColor);
        }
        return this;
    }

    public StylishAlertDialog setCancelClickListener(OnStylishClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public StylishAlertDialog setConfirmClickListener(OnStylishClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    public StylishAlertDialog setNeutralText(String text) {
        mNeutralText = text;
        if (mNeutralButton != null && mNeutralText != null && !text.isEmpty()) {
            mNeutralButton.setVisibility(View.VISIBLE);
            mNeutralButton.setText(mNeutralText);
        }
        return this;
    }

    public StylishAlertDialog setNeutralClickListener(OnStylishClickListener listener) {
        mNeutralClickListener = listener;
        return this;
    }

    @Override
    public void setTitle(CharSequence title) {
        this.setTitleText(title.toString());
    }

    @Override
    public void setTitle(int titleId) {
        this.setTitleText(getContext().getResources().getString(titleId));
    }

    public Button getButton(int buttonType) {
        switch (buttonType) {
            default:
            case BUTTON_CONFIRM:
                return mConfirmButton;
            case BUTTON_CANCEL:
                return mCancelButton;
            case BUTTON_NEUTRAL:
                return mNeutralButton;
        }
    }

    public StylishAlertDialog setConfirmButton(String text, OnStylishClickListener listener) {
        this.setConfirmText(text);
        this.setConfirmClickListener(listener);
        return this;
    }

    public StylishAlertDialog setConfirmButton(int resId, OnStylishClickListener listener) {
        String text = getContext().getResources().getString(resId);
        setConfirmButton(text, listener);
        return this;
    }

    public StylishAlertDialog setCancelButton(String text, OnStylishClickListener listener) {
        this.setCancelText(text);
        this.setCancelClickListener(listener);
        return this;
    }

    public StylishAlertDialog setCancelButton(int resId, OnStylishClickListener listener) {
        String text = getContext().getResources().getString(resId);
        setCancelButton(text, listener);
        return this;
    }

    public StylishAlertDialog setNeutralButton(String text, OnStylishClickListener listener) {
        this.setNeutralText(text);
        this.setNeutralClickListener(listener);
        return this;
    }

    public StylishAlertDialog setNeutralButton(int resId, OnStylishClickListener listener) {
        String text = getContext().getResources().getString(resId);
        setNeutralButton(text, listener);
        return this;
    }

    public int getContentTextSize() {
        return contentTextSize;
    }

    /**
     * Set content text size
     *
     * @param value text size in sp
     */
    public StylishAlertDialog setContentTextSize(int value) {
        this.contentTextSize = value;
        return this;
    }

    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
        playAnimation();
    }

    /**
     * set custom view instead of message
     *
     * @param view
     */
    public StylishAlertDialog setCustomView(View view) {
        mCustomView = view;
        if (mCustomView != null && mCustomViewContainer != null) {
            mCustomViewContainer.addView(view);
            mCustomViewContainer.setVisibility(View.VISIBLE);
            mContentTextView.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        //several view animations can't be launched at one view, that's why apply alpha animation on child
        ((ViewGroup) mDialogView).getChildAt(0).startAnimation(mOverlayOutAnim); //alpha animation
        mDialogView.startAnimation(mModalOutAnim); //scale animation
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(StylishAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(StylishAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.neutral_button) {
            if (mNeutralClickListener != null) {
                mNeutralClickListener.onClick(StylishAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        }
    }

    public PGHelper getProgressHelper() {
        return mProgressHelper;
    }

    public boolean isHideKeyBoardOnDismiss() {
        return this.mHideKeyBoardOnDismiss;
    }

    public StylishAlertDialog setHideKeyBoardOnDismiss(boolean hide) {
        this.mHideKeyBoardOnDismiss = hide;
        return this;
    }

    private void hideSoftKeyboard() {
        Activity activity = getOwnerActivity();
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public interface OnStylishClickListener {
        void onClick(StylishAlertDialog StylishAlertDialog);
    }
}
