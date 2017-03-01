package sunjc.materialdesigntest.heartRate;


import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.drawerMain.view.LeftView;
import zx.Utility.NetworkUtility.Upload;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {

    private View rootView;

    private int avgHeartRate = 0;
    private TextView mHeartRateDisp;

    public void setAvgHeartRate(int val){
        avgHeartRate = val;
    }

    FloatingActionButton UploadDataButton;//written by ZX

    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_upload, container, false);
        mHeartRateDisp = (TextView)rootView.findViewById(R.id.HRdisp);


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //written by ZX
        UploadDataButton = (FloatingActionButton) rootView.findViewById(R.id.UploadDataButton);
        UploadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Upload());
                thread.start();
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (avgHeartRate > 0 ){
            mHeartRateDisp.setText(Integer.toString(avgHeartRate)+"BPM");
        }else{
            UploadDataButton.setVisibility(View.GONE);
            mHeartRateDisp.setText("INVALID MEASUREMENT");
        }
    }
}
