package com.yygo.lib.wiget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.yygo.lib.R;
import com.yygo.lib.helper.Helper;


/**
 * 
 * function: 清空的输入框(用于单行)
 * 说明：修改删除图片 android:drawableRight
 * 
 * 注意：
 * setOnFocusChangeListener换成setOnFocusChangeImpl
 * addTextChangedListener换成addTextWatcherImpl
 * 
 * @ author:linjunying
 */
public class ClearEditText extends EditText
         				   implements OnFocusChangeListener, TextWatcher {
	// 删除按钮的引用
    private Drawable mClearDrawable; 

    // 控件是否有焦点
    private boolean hasFoucs;
    
    private OnFocusChangeImpl onFocusChangeImpl;
    
    private TextWatcherImpl textWatcherImpl;
 
    public ClearEditText(Context context) { 
    	this(context, null); 
    } 
 
    public ClearEditText(Context context, AttributeSet attrs) { 
    	//这里构造方法也很重要，不加这个很多属性不能再XML里面定义
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public void setOnFocusChangeImpl(OnFocusChangeImpl onFocusChangeImpl) {
		this.onFocusChangeImpl = onFocusChangeImpl;
	}
    
    public void addTextWatcherImpl(TextWatcherImpl textWatcherImpl) {
		this.textWatcherImpl = textWatcherImpl;
	}
    
    private void init() { 
    	//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
    	mClearDrawable = getCompoundDrawables()[2]; 
        if (Helper.isNull(mClearDrawable)) {
        	mClearDrawable = getResources().getDrawable(R.drawable.btn_editext_clear_fw);
        } 
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight()); 
        
        //默认设置隐藏图标
        setClearIconVisible(false); 
        //设置焦点改变的监听
        setOnFocusChangeListener(this); 
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this); 
    } 
 
 
    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override 
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));
				
				if (touchable) {
					this.setText("");
				}
			}
		}

		return super.onTouchEvent(event);
	}
 
    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
    	this.hasFoucs = hasFocus;
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0); 
        } else { 
            setClearIconVisible(false); 
        } 
        
        if(Helper.isNotNull(onFocusChangeImpl)) {
        	onFocusChangeImpl.onFocusChange(v, hasFocus);
        }
    } 
 
 
    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
    
    
    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override 
    public void onTextChanged(CharSequence s, int start, int count, int after) { 
    	if(hasFoucs){
    		setClearIconVisible(s.length() > 0);
    	}
    	
    	if(Helper.isNotNull(textWatcherImpl)) {
    		textWatcherImpl.onTextChanged(s, start, count, after);
        }
    } 
 
    @Override 
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { 
    	if(Helper.isNotNull(textWatcherImpl)) {
    		textWatcherImpl.beforeTextChanged(s, start, count, after);
        }
    } 
 
    @Override 
    public void afterTextChanged(Editable s) { 
    	if(Helper.isNotNull(textWatcherImpl)) {
    		textWatcherImpl.afterTextChanged(s);
        }
    } 
    
    /**
     * 
     * function: OnFocusChange实现
     * 
     * @ author:linjunying
     */
    public interface OnFocusChangeImpl {
    	void onFocusChange(View v, boolean focus);
    }
    
    /**
     * 
     * function: TextWatcher实现
     * 
     * @ author:linjunying
     */
    public interface TextWatcherImpl {
    	
    	void afterTextChanged(Editable s);
    	
    	void beforeTextChanged(CharSequence s, int start, int count, int after);
    	
    	void onTextChanged(CharSequence s, int start, int count, int after);
    }

}
