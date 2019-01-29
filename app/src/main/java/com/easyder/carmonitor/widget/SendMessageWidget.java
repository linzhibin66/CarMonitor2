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
import com.easyder.carmonitor.interfaces.SendMessagebackListener;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

/**
 * Created by ljn on 2017-04-21.
 */
public class SendMessageWidget implements View.OnClickListener {

    private Context context;

    private SendMessagebackListener listener;

    private View rootView;

    private EditText message_content;

    private Button commit_send_message;

    private RelativeLayout feedback_layout_outmost;

//    private View currentBoundInputView;

    public SendMessageWidget(Context context, SendMessagebackListener listener) {
        this.context = context;
        this.listener = listener;
        rootView = View.inflate(context, R.layout.sendmessage_layout, null);
        initView();
        initTitle(rootView);
    }

    public SendMessageWidget(Context context, View rootView, SendMessagebackListener listener) {
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
        message_content = (EditText) rootView.findViewById(R.id.message_content);

        commit_send_message = (Button) rootView.findViewById(R.id.commit_send_message);
        commit_send_message.setOnClickListener(this);
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

        title_text.setText(context.getString(R.string.compact_operation_send_message));

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

        if(message_content == null){
            return ;
        }

        String content = message_content.getText().toString().trim();
        if(TextUtils.isEmpty(content)){
            ToastUtil.showShort(context.getString(R.string.message_no_content));
            return;
        }

        if(listener != null ){
            listener.commit(content);
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
