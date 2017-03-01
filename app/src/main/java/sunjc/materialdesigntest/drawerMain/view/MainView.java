package sunjc.materialdesigntest.drawerMain.view;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.drawerMain.controller.MainFrame;
import sunjc.materialdesigntest.util.ViewMVC;
import sunjc.materialdesigntest.util.Utils;

public class MainView implements ViewMVC {

    View mRootView;

    Toolbar mToolbar;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    DrawerLayout mDrawerLayout;

    public static Bitmap[] kPreloadIcon;

    public static void InitPreloadIcons(AssetManager loader, String[] icTitles){
        kPreloadIcon = new Bitmap[MainFrame.MENU_ITEM_NUM];
        for (int i = 0; i < MainFrame.MENU_ITEM_NUM; i++) {
            kPreloadIcon[i] = Utils.loadBitmap(loader, icTitles[i]);
        }
    }

    public MainView(LayoutInflater inflater, ViewGroup container, AppCompatActivity activity){
        mRootView = inflater.inflate(R.layout.fragment_main, container,false);

        setToolBar(activity);
        setDrawer(activity);
    }

    public void setTitle(String title){
        mToolbar.setTitle(title);
    }

    private void setToolBar(AppCompatActivity activity){
        mToolbar = (Toolbar) mRootView.findViewById(R.id.app_bar);
        mToolbar.setLogo(R.drawable.gym);
        activity.setSupportActionBar(mToolbar);
    }

    private void setDrawer(AppCompatActivity activity){
        mDrawerLayout = (DrawerLayout) mRootView.findViewById(R.id.drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(activity,
                mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public View getRootView(){
        return mRootView;
    }

    @Override
    public Bundle getViewState(){
        return null;
    }
}
