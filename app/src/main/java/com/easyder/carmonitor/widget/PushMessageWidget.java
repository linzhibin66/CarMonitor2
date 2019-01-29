package com.easyder.carmonitor.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.adapter.PushMessageAdapter;
import com.easyder.carmonitor.interfaces.LayoutBackListener;
import com.easyder.carmonitor.widget.swipemenulistview.SwipeMenu;
import com.easyder.carmonitor.widget.swipemenulistview.SwipeMenuCreator;
import com.easyder.carmonitor.widget.swipemenulistview.SwipeMenuItem;
import com.easyder.carmonitor.widget.swipemenulistview.SwipeMenuListView;
import com.shinetech.mvp.DB.DBManager;
import com.shinetech.mvp.DB.bean.PushMessage;
import com.shinetech.mvp.utils.ToastUtil;
import com.shinetech.mvp.utils.UIUtils;

import java.util.List;

/**
 * Created by ljn on 2017-05-18.
 */
public class PushMessageWidget {

    private Context context;

    private LayoutBackListener listener;

    private View rootview;

    private SwipeMenuListView mListview;

    private PushMessageAdapter pushMessageAdapter;

    private RelativeLayout pushmessage_layout_outmost;

    private RelativeLayout no_content_layout;

    public PushMessageWidget(Context context,LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        rootview = View.inflate(context, R.layout.activity_message,null);
        initTitle();
        initView();
    }

    public PushMessageWidget(Context context, View rootview, LayoutBackListener listener) {
        this.context = context;
        this.listener = listener;
        this.rootview = rootview;
        initTitle();
        initView();
    }

    public View getView(){
        return rootview;
    }

    private void initTitle(){

        ImageButton title_back = (ImageButton) rootview.findViewById(R.id.title_back);
        TextView title_text = (TextView) rootview.findViewById(R.id.title_text);
        ImageButton title_search = (ImageButton) rootview.findViewById(R.id.title_search);

        title_text.setText(context.getString(R.string.push_message_title));

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

    private void initView(){

        pushmessage_layout_outmost = (RelativeLayout) rootview.findViewById(R.id.pushmessage_layout_outmost);

        no_content_layout = (RelativeLayout) rootview.findViewById(R.id.no_content_layout);

        mListview = (SwipeMenuListView) rootview.findViewById(R.id.message_list);

        List<PushMessage> pushMessages = DBManager.queryPushMessage();

        if(pushMessages == null || pushMessages.size() == 0){
            no_content_layout.setVisibility(View.VISIBLE);
        }else{
            no_content_layout.setVisibility(View.GONE);
        }

        pushMessageAdapter = new PushMessageAdapter(context);

        pushMessageAdapter.setData(pushMessages);

        mListview.setAdapter(pushMessageAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(context);
                // set item background ECEBF1
                openItem.setBackground(new ColorDrawable(Color.rgb(0xEC, 0xEB,
                        0xF1)));
                // set item width
                openItem.setWidth(UIUtils.dip2px(70));

                View view = View.inflate(context, R.layout.push_message_delete_menu, null);

                openItem.setView(view);
              /*  // set item title
                openItem.setTitle("删除");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);*/
                // add to menu
                menu.addMenuItem(openItem);
            }
        };

        mListview.setMenuCreator(creator);

        // step 2. listener item click event
        mListview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                PushMessage item = (PushMessage) pushMessageAdapter.getItem(position);
                switch (index) {
                    case 0:
//                        ToastUtil.showShort(item.getMessageType() + "  delete");

                        DBManager.deletePushMessage(item);

                        List<PushMessage> pushMessages = DBManager.queryPushMessage();
                        pushMessageAdapter.setData(pushMessages);
                        pushMessageAdapter.notifyDataSetChanged();
                        if(pushMessages == null || pushMessages.size() == 0){
                            no_content_layout.setVisibility(View.VISIBLE);
                        }else{
                            no_content_layout.setVisibility(View.GONE);
                        }
                        break;
                }
                return false;
            }
        });
    }

    public void setOutmostTouchListener(View.OnTouchListener touchListener){
        if(pushmessage_layout_outmost != null){
            pushmessage_layout_outmost.setOnTouchListener(touchListener);
        }
    }
}
