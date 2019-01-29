package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easyder.carmonitor.R;
import com.easyder.carmonitor.widget.stickdot.DensityUtils;
import com.easyder.carmonitor.widget.stickdot.StickyDotHepler;
import com.shinetech.mvp.DB.DBManager;

/**
 * Created by ljn on 2017/2/24.
 */
public class UserMenuItemAdapter extends BaseAdapter{

    private String[] items;
    private Context context;
    private int[] icon;

    public UserMenuItemAdapter(Context context, String[] items, int[] icon) {
        this.items = items;
        this.context = context;
        this.icon = icon;
    }

    @Override
    public int getCount() {
        if(items!=null) {
            return items.length;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(items!=null && items.length>position) {
            return items[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if(convertView == null){
            convertView = View.inflate(context, R.layout.main_user_menu_item,null);
            mViewHolder = new ViewHolder();
            mViewHolder.text = (TextView) convertView.findViewById(R.id.item_text);
            mViewHolder.tv_activity_windows = (TextView) convertView.findViewById(R.id.tv_activity_windows);
            mViewHolder.img = (ImageView) convertView.findViewById(R.id.item_icon);
            convertView.setTag(mViewHolder);
        }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.text.setText(items[position]);
//        TODO set img
        mViewHolder.img.setImageResource(icon[position]);

        if(position == 3){
            int noReadCount = DBManager.queryPushMessageNoReadCount();
            if(noReadCount>0) {
                mViewHolder.tv_activity_windows.setText(noReadCount+"");
                TextView mDragView = (TextView) LayoutInflater.from(context).inflate(R.layout.include_view, null, false);
                mDragView.setText(noReadCount+"");
                mViewHolder.tv_activity_windows.setVisibility(View.VISIBLE);
                StickyDotHepler hepler = new StickyDotHepler(context, mViewHolder.tv_activity_windows, mDragView)
                        .setMaxDragDistance(DensityUtils.dip2px(context, 100))
                        .setColor(Color.parseColor("#94D5EE"))
                        .setOutListener(new StickyDotHepler.StickyListener() {
                            @Override
                            public void outRangeUp(PointF dragCanterPoint) {
//                                Toast.makeText(context, "第  " + position + " 个", Toast.LENGTH_LONG).show();
                                DBManager.clearNoReadMessage();
                            }
                        });
            }else{
                mViewHolder.tv_activity_windows.setVisibility(View.GONE);
            }

        }else{
            mViewHolder.tv_activity_windows.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder{
        TextView text;
        TextView tv_activity_windows;
        ImageView img;
    }
}
