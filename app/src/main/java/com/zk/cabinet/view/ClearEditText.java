package com.zk.cabinet.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.zk.cabinet.R;
import com.zk.cabinet.utils.DensityUtil;

/**
 * 一键清除的 AppCompatClearEditText
 * 1.带删除图标，一键清除
 * 2.失去焦点的时候 可隐藏软键盘（这个代码需要放开）
 * Created by 赵鑫 on 2020/8/4.
 */
public class ClearEditText extends AppCompatEditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private Drawable mClearTextIcon;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnTouchListener mOnTouchListener;
    private Context mContext;

    public ClearEditText(final Context context) {
        super(context);
        mContext = context;
        init(mContext);
    }

    public ClearEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(mContext);
    }

    public ClearEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(mContext);
    }

    private int rightOffset;
    private int rightPadding;
    private int initPaddingRight;

    private void init(final Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_delete);

        Bitmap clearButShow = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_delete)).getBitmap();

        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable); //Wrap the drawable so that it can be tinted pre Lollipop
//        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
        mClearTextIcon = wrappedDrawable;
        mClearTextIcon.setBounds(0, 0, mClearTextIcon.getIntrinsicHeight(), mClearTextIcon.getIntrinsicHeight());
        setClearIconVisible(false);

        rightOffset = DensityUtil.dip2px(context, 10);
        initPaddingRight = getPaddingRight();
        if (TextUtils.isEmpty(getText().toString())) {
            rightPadding = initPaddingRight + rightOffset;
        } else {
            rightPadding = initPaddingRight + clearButShow.getWidth() + rightOffset + rightOffset;
        }
        setPadding(getPaddingLeft(), getPaddingTop(), rightPadding, getPaddingBottom());
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mOnFocusChangeListener = l;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
            //  失去焦点，隐藏软键盘
            //1.得到InputMethodManager对象
//            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            //2.调用hideSoftInputFromWindow方法隐藏软键盘
//            imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (mClearTextIcon.isVisible() && x > getWidth() - getPaddingRight() - mClearTextIcon.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                setError(null);
                setText("");
            }
            return true;
        }
        return mOnTouchListener != null && mOnTouchListener.onTouch(view, motionEvent);
    }

    @Override
    public final void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (isFocused()) {
            setClearIconVisible(text.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void setClearIconVisible(final boolean visible) {
        mClearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mClearTextIcon : null,
                compoundDrawables[3]);
    }
}
