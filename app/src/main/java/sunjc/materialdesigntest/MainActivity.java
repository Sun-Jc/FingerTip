
package sunjc.materialdesigntest;

/**
 * Created by Lesley on 2016/3/8.
 */

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.drawerMain.controller.MainFrame;

public class MainActivity extends AppCompatActivity {

    MainFrame mMainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_main);
        // Show the default fragment if the application is not restored

        if (savedInstanceState == null) {
            showMainView();
        }
    }

    private void showMainView() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mMainFrame = new MainFrame();
        ft.add(R.id.mainContainer, mMainFrame);
        ft.commit();
    }

    public void switchFullFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            f.setEnterTransition(new Fade());
        }

        ft.replace(R.id.mainContainer, f);
        ft.commit();
    }

    public void switchHomeFromFull() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.mainContainer, mMainFrame);
        ft.commit();
    }

    public MainFrame getMainFrame() {
        return mMainFrame;
    }
}
