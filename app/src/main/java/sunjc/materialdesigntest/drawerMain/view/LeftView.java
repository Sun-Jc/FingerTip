package sunjc.materialdesigntest.drawerMain.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import de.greenrobot.event.EventBus;
import sunjc.materialdesigntest.R;
import sunjc.materialdesigntest.util.ViewMVC;


/**
 * Created by SunJc on Feb/17/16.
 */
public class LeftView implements ViewMVC {
    View mRootView;

    ListView mMenuList;
    Button mExitButton;

    public LeftView(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.left_menu_list, container, false);

        initList();

        mExitButton = (Button)mRootView.findViewById(R.id.exitButton);
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus eventBus = EventBus.getDefault();
                eventBus.post(new CloseAppEvent());
            }
        });
    }

    private void initList() {
        mMenuList = (ListView) mRootView.findViewById(R.id.leftMenuListView);
        mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Get the event bus
                EventBus eventBus = EventBus.getDefault();
                // Post a new event to the bus
                eventBus.post(new ListItemClickEvent(position, id));
            }
        });
        mMenuList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    public void setListAdapter(ListAdapter adapter){
        mMenuList.setAdapter(adapter);
        mMenuList.setItemChecked(0, true);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public Bundle getViewState() {
        return null;
    }


    /**
     * This nested static class represents an item click event that will be posted on EventBus.
     */
    public static class ListItemClickEvent {
        public int mPosition;
        public long mId;

        public ListItemClickEvent(int position, long id) {
            mPosition = position;
            mId = id;
        }
    }

    public static class CloseAppEvent{}

}
