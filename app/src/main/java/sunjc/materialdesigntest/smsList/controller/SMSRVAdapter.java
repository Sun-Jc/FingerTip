package sunjc.materialdesigntest.smsList.controller;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;

import sunjc.materialdesigntest.smsList.model.SMSinfoModel;
import sunjc.materialdesigntest.smsList.view.SMSItemView;

/**
 * Created by SunJc on Feb/19/16.
 */
public class SMSRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    SMSinfoModel[] mSmsInfo;

    public static void SetLayoutInflater(Activity a) {
        SMSItemView.SetLayoutInflater(a);
    }

    public SMSRVAdapter(LinkedList<SMSinfoModel> smsInfo){
        this.mSmsInfo = smsInfo.toArray(new SMSinfoModel[smsInfo.size()]);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SMSItemView siv = new SMSItemView(parent);
        return siv.getViewHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SMSItemView.itemViewHolder)holder).setFromTime(mSmsInfo[position].mFrom, mSmsInfo[position].mTime);
    }

    @Override
    public int getItemCount() {
        return mSmsInfo.length;
    }
}
