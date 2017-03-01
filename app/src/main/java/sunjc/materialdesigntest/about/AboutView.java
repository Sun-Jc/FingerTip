package sunjc.materialdesigntest.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.util.ViewMVC;

/**
 * Created by SunJc on Feb/18/16.
 */
public class AboutView implements ViewMVC {

    View mRootView;

    public AboutView(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.about_us, container, false);
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
