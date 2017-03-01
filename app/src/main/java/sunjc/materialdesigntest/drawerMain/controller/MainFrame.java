package sunjc.materialdesigntest.drawerMain.controller;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.about.AboutFragment;
import sunjc.materialdesigntest.MainActivity;
import sunjc.materialdesigntest.drawerMain.view.LeftView;
import sunjc.materialdesigntest.drawerMain.view.MainView;
import sunjc.materialdesigntest.heartRate.HeartRateFragment;
import sunjc.materialdesigntest.heartRate.WelcomeFragment;
import sunjc.materialdesigntest.util.BackableFragment;
import zx.Network.View.HistoryDataFragmentCardView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFrame extends Fragment{

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Region: DEFINE THE CONTENT FRAGMENTS
    public static final int MENU_ITEM_NUM = 3;//有几项
    public static final String[] IC_TITLES = {"ic_touch_app_black_24dp.png","ic_equalizer_black_24dp.png","ic_info_white_24dp.png"};//图标文件
    public static final String[] TITLES = {"Start","History","About"};//名称
    static int currentMenuItemNum = 0;//默认
    public static void setContentFragments(){
        contentFragments[0] = new HeartRateFragment();
        //contentFragments[1] = new HistoryDataFragment();//
        contentFragments[1] = new HistoryDataFragmentCardView();
        contentFragments[2] = new AboutFragment();
    }
    // END Region
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    static LeftFragment leftFragment;

    static Fragment[] contentFragments;

    static Fragment currentTopFragment;

    MainView mView;

    public void switchContentFragment(Fragment f){
        FragmentTransaction ft = getFragmentManager().beginTransaction();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            f.setEnterTransition(new Slide(Gravity.BOTTOM));
        }

        if(null == currentTopFragment) {
            ft.detach(contentFragments[currentMenuItemNum]);
            ft.add(R.id.drawer_content_container,f);
        }else {
                ft.replace(R.id.drawer_content_container, currentTopFragment);
        }
        currentTopFragment = f;
        ft.commit();
    }

    public void switchHomeFromContent(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(currentTopFragment);
        ft.attach(contentFragments[currentMenuItemNum]);
        ft.commit();
        currentTopFragment = null;
    }

    public MainFrame() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(null == mView) {
            mView = new MainView(inflater, container, (AppCompatActivity) getActivity());

            mView.InitPreloadIcons(getActivity().getAssets(), IC_TITLES);

            createFragments();
        }
        return mView.getRootView();
    }

    private void createFragments(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        // create LeftFragment and ContentsFragment
        if(null == leftFragment){
            leftFragment = new LeftFragment();
            contentFragments = new Fragment[MENU_ITEM_NUM];
            setContentFragments();
        }

        // add Fragments, FIRSTLY ADD ALL TO MAKE SURE EVERY FRAG IS GOOD
        if(!leftFragment.isAdded())
            ft.add(R.id.menu_left_container, leftFragment);
        for (int i = 0; i < MENU_ITEM_NUM; i++)
            if(!contentFragments[i].isAdded())
                ft.add(R.id.drawer_content_container, contentFragments[i]);

        for (int i = 0; i < MENU_ITEM_NUM; i++)
            ft.detach(contentFragments[i]);
        ft.commit();
        switchContent(currentMenuItemNum);
    }

    // Region: Event Bus setting
    EventBus mEventBus = EventBus.getDefault();
    @Override
    public void onResume(){
        super.onResume();
        mEventBus.register(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        // Unregister this fragment from the EventBus
        mEventBus.unregister(this);
    }
    // End of Region: Event Bus Setting

    public void onEvent(LeftView.ListItemClickEvent event) {
        mView.closeDrawer();
        switchContent(event.mPosition);
    }

    public void onEvent(LeftView.CloseAppEvent e){
        getActivity().finish();
    }

    private void switchContent(int id){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(currentTopFragment != null) {
            ft.remove(currentTopFragment);
            currentTopFragment = null;
        }

        if(!contentFragments[currentMenuItemNum].isDetached())
            ft.detach(contentFragments[currentMenuItemNum]);
        currentMenuItemNum = id;
        ft.attach(contentFragments[currentMenuItemNum]);
        ft.commit();
        mView.setTitle(TITLES[id]);


        if(id==0){
            WelcomeFragment noticeFragment = new WelcomeFragment();
            noticeFragment.setBackableFragment(new BackableFragment() {
                @Override
                public void goBack() {
                    rootActivity.switchHomeFromFull();
                }
            });
            ((MainActivity)getActivity()).switchFullFragment(noticeFragment);
        }
    }

    // Region: get root activity
    MainActivity rootActivity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.rootActivity = (MainActivity)activity;
    }
    // End of Region: get root activity
}
