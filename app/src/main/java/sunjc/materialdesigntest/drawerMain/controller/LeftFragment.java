package sunjc.materialdesigntest.drawerMain.controller;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sunjc.materialdesigntest.drawerMain.view.LeftView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeftFragment extends Fragment {

    LeftView mView;
    ItemAdapter mItemAdapter;


    public LeftFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(null == mView) {
            mView = new LeftView(inflater, container);

            initList();
        }

        return mView.getRootView();
    }

    private void initList(){
        ItemAdapter.SetLayoutInflater(getActivity());

        mItemAdapter = new ItemAdapter(MainFrame.TITLES);

        mView.setListAdapter(mItemAdapter);
    }
}
