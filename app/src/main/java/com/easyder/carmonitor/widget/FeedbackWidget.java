package com.easyder.carmonitor.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.interfaces.FeedbackListener;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-21.
 */
public class FeedbackWidget implements View.OnClickListener {

    private Context context;

    private FeedbackListener listener;

    private View rootView;

    private EditText feedback_content;

    private EditText user_name;

    private EditText user_phone_number;

    private Button commit_feedback;

    private RelativeLayout feedback_layout_outmost;

//    private View currentBoundInputView;

    public FeedbackWidget(Context context,FeedbackListener listener) {
        this.context = context;
        this.listener = listener;
        rootView = View.inflate(context, R.layout.feedback_layout, null);
        initView();
        initTitle(rootView);
    }

    public FeedbackWidget(Context context,View rootView, FeedbackListener listener) {
        this.context = context;
        this.listener = listener;
        this.rootView = rootView;
        initView();
        initTitle(rootView);
    }

    public View getView(){
        return rootView;
    }

    private void initView(){
        feedback_content = (EditText) rootView.findViewById(R.id.feedback_content);
        user_name = (EditText) rootView.findViewById(R.id.user_name);
        user_phone_number = (EditText) rootView.findViewById(R.id.user_phone_number);

        commit_feedback = (Button) rootView.findViewById(R.id.commit_feedback);
        commit_feedback.setOnClickListener(this);
        feedback_layout_outmost = (RelativeLayout) rootView.findViewById(R.id.feedback_layout_outmost);
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(feedback_layout_outmost != null){
            feedback_layout_outmost.setOnTouchListener(touchListener);
        }
    }

    private void initTitle(View rootview){

        ImageButton title_back = (ImageButton) rootview.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootview.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootview.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.feedback_title));

        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        title_search.setVisibility(View.GONE);

        RelativeLayout title_layout = (RelativeLayout) rootview.findViewById(R.id.title_layout);

        UIUtils.setImmersiveStatusBar(title_layout);

    }

    @Override
    public void onClick(View v) {

        String message = "";

        if(feedback_content == null || user_name == null || user_phone_number == null){
            return ;
        }

        String content = feedback_content.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            ToastUtil.showShort(context.getString(R.string.feedback_no_content));
            return;
        }

        String username = user_name.getText().toString().trim();
        if(!TextUtils.isEmpty(username)){
            message  = message + context.getString(R.string.feedback_user)+username;
        }

        String user_phone = user_phone_number.getText().toString().trim();
        if(!TextUtils.isEmpty(user_phone)){
            message  = message + context.getString(R.string.feedback_phone)+user_phone;
        }
            message  = message + context.getString(R.string.feedback_content)+content;

        ToastUtil.showShort(message);

        if(listener != null ){
            listener.feedbackCommit(message);
        }
    }

    /**
     * 弹出输入法
     *
     */
    public void showInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 接受软键盘输入的编辑文本或其它视图

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    /**
     * 关闭输入法
     *
     */
    public void hideInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(rootView.getApplicationWindowToken(), 0);
    }

}
