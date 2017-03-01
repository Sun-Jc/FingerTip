package sunjc.materialdesigntest.drawerMain.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.util.ViewMVC;

/**
 * Created by SunJc on Feb/17/16.
 */
public class ItemView implements ViewMVC {

    private static LayoutInflater sInflater = null;


    public static void SetLayoutInflater(Activity a) {
        sInflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    private View mRootView;
    private ImageView mIcon;
    private TextView mTextView;

    public ItemView(ViewGroup container){
        mRootView = sInflater.inflate(R.layout.left_menu_item,container,false);
        bindGUI();
    }

    public ItemView(View rootView){
        mRootView = rootView;
        bindGUI();
    }

    private void bindGUI(){
        mIcon = (ImageView) mRootView.findViewById(R.id.itemIconView);
        mTextView = (TextView) mRootView.findViewById(R.id.itemTextView);
    }

    public void display(int position, String title){
        mIcon.setImageBitmap(MainView.kPreloadIcon[position]);
        mTextView.setText(title);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        return null;
    }
}
