package be.ombrax.syncscroll;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.ScrollView;

/**
 * Created by maikel.degrande on 02/09/2018.
 */

public class SyncScrollView extends ScrollView implements Runnable {

    //region variables
    private SyncScrollManager syncScrollManager;
    private OverScroller scroller;
    //endregion

    //region constructor
    public SyncScrollView(Context context) {
        super(context);
        init();
    }

    public SyncScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SyncScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SyncScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    //endregion

    //region init
    private void init() {
        scroller = new OverScroller(getContext());
    }
    //endregion

    //region public methods
    public void setScrollManager(SyncScrollManager syncScrollManager) {
        this.syncScrollManager = syncScrollManager;
        syncScrollManager.register(this);
    }
    //endregion

    //region scroll
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (syncScrollManager != null) {
            syncScrollManager.syncScroll(this, t);
        }
    }

    @Override
    public void fling(int velocityY) {
        //We need to handle the fling ourselves to guarantee a perfect sync
        scroller.forceFinished(true);
        scroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0, getChildAt(0).getHeight());
        //Start the 'animation'
        post(this);
    }

    @Override
    public void run() {
        //If the scroller is still computing, perform this iterations scroll and continue the 'animation'
        if (scroller.computeScrollOffset()) {
            smoothScrollTo(0, scroller.getCurrY());
            post(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //If the user touches the screen, finish the scroller and end the 'animation'
        if (ev.getAction() == MotionEvent.ACTION_DOWN && !scroller.isFinished()) {
            scroller.forceFinished(true);
            post(this);
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
    //endregion
}
