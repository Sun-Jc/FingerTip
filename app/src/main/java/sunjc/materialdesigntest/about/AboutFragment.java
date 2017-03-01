package sunjc.materialdesigntest.about;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sunjc.materialdesigntest.MainActivity;
import sunjc.materialdesigntest.drawerMain.controller.MainFrame;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    AboutView mView;

    MainFrame rootMainFrame;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        rootMainFrame = ((MainActivity) activity).getMainFrame();
    }

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = new AboutView(inflater, container);
        return mView.getRootView();
    }


}
