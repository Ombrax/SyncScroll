package be.ombrax.syncscroll;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maikel.degrande on 02/09/2018.
 */

public class SyncScrollManager {

    //region variables
    private List<View> views;
    private volatile boolean isSyncing = false;
    //endregion

    //region constructor
    public SyncScrollManager() {
        views = new ArrayList<>();
    }
    //endregion

    //region public methods
    public void register(View view) {
        if (!views.contains(view)) {
            views.add(view);
        }
    }

    public void syncScroll(View invoker, int y) {
        //Prevent infinite recursion
        if (isSyncing)
            return;

        isSyncing = true;

        for (View view : views) {
            if (view == invoker)
                continue;
            view.scrollTo(0, y);
        }

        isSyncing = false;
    }
    //endregion
}
