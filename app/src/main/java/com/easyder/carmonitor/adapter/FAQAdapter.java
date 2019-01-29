package com.easyder.carmonitor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.easyder.carmonitor.R;

/**
 * Created by ljn on 2017-04-12.
 */
public class FAQAdapter extends BaseExpandableListAdapter{

    private String[] faqPsroblem;

    private String[] faqResult;

    private Context context;

    public FAQAdapter(String[] faqPsroblem, String[] faqResult, Context context) {
        this.faqPsroblem = faqPsroblem;
        this.faqResult = faqResult;
        this.context = context;
    }

    public void setData(String[] faqPsroblem, String[] faqResult){
        this.faqPsroblem = faqPsroblem;
        this.faqResult = faqResult;
        notifyDataSetChanged();
    }
    @Override
    public int getGroupCount() {
        if(faqPsroblem != null) {
            return faqPsroblem.length;
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(faqResult != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if(faqPsroblem.length> groupPosition){
            return faqPsroblem[groupPosition];
        }
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(faqResult.length>groupPosition){
            return faqResult[groupPosition];
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolser mViewHolser;
        if(convertView == null){
            mViewHolser = new ViewHolser();
            convertView = View.inflate(context, R.layout.faq_psroblem_item, null);
            mViewHolser.faq_psroblem_tv = (TextView) convertView.findViewById(R.id.faq_psroblem_tv);
            convertView.setTag(mViewHolser);
        }else{
            mViewHolser = (ViewHolser) convertView.getTag();
        }

        mViewHolser.faq_psroblem_tv.setText(faqPsroblem[groupPosition]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ViewHolser mViewHolser;
        if(convertView == null){
            mViewHolser = new ViewHolser();
            convertView = View.inflate(context, R.layout.faq_result_item, null);
            mViewHolser.faq_psroblem_tv = (TextView) convertView.findViewById(R.id.faq_result_tv);
            mViewHolser.faq_result_item_line = convertView.findViewById(R.id.faq_result_item_line);
            convertView.setTag(mViewHolser);
        }else{
            mViewHolser = (ViewHolser) convertView.getTag();
        }

        mViewHolser.faq_psroblem_tv.setText(faqResult[groupPosition]);

        if(groupPosition == (faqResult.length-1)){
            mViewHolser.faq_result_item_line.setVisibility(View.GONE);
        }else{
            mViewHolser.faq_result_item_line.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class ViewHolser{
        TextView faq_psroblem_tv;
        View faq_result_item_line;
    }
}
