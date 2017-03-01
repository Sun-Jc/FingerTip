package sunjc.materialdesigntest.drawerMain.controller;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import sunjc.materialdesigntest.drawerMain.view.ItemView;

/**
 * Created by SunJc on Feb/17/16.
 */
public class ItemAdapter extends BaseAdapter {

    public static void SetLayoutInflater(Activity a) {
        ItemView.SetLayoutInflater(a);
    }

    String[] mItems;

    public ItemAdapter(String[] titles){
        mItems = titles;
    }

    @Override
    public int getCount() {
        return MainFrame.MENU_ITEM_NUM;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View useView = convertView;
        ItemView itemView;
        if(null == convertView){
            itemView = new ItemView(parent);
        }else{
            itemView = new ItemView(convertView);
        }
        itemView.display(position,mItems[position]);
        return itemView.getRootView();
    }
}
