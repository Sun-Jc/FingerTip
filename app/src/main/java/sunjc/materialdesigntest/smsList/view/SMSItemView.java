package sunjc.materialdesigntest.smsList.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.util.ViewMVC;

/**
 * Created by SunJc on Feb/19/16.
 */
public class SMSItemView{

    private static LayoutInflater sInflater = null;
    public static void SetLayoutInflater(Activity a) {
        sInflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    itemViewHolder mViewHolder;

    public RecyclerView.ViewHolder getViewHolder() {
        return mViewHolder;
    }

    public class itemViewHolder extends RecyclerView.ViewHolder{
        TextView mSMSFrom;
        TextView mSMSTime;
        public itemViewHolder(View root) {
            super(root);
            mSMSFrom = (TextView)root.findViewById(R.id.smsFromTextView);
            mSMSTime = (TextView)root.findViewById(R.id.smsTimeTextView);
        }

        public void setFromTime(String from, String time){
            mSMSFrom.setText(from);
            mSMSTime.setText(time);
        }
    }

    public SMSItemView(ViewGroup container) {
        View root = sInflater.inflate(R.layout.sms_item,container,false);
        mViewHolder = new itemViewHolder(root);
    }
}
