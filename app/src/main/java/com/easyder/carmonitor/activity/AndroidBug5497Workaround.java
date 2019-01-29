package com.easyder.carmonitor.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.easyder.carmonitor.presenter.MainActivityPresenter;

/**
 * Created by ljn on 2017-12-08.
 */

public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity (Activity activity, MainActivityPresenter presenter) {
        new AndroidBug5497Workaround(activity, presenter);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private MainActivityPresenter presenter;

    private AndroidBug5497Workaround(Activity activity, MainActivityPresenter presenter) {
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        this.presenter = presenter;
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            presenter.updateDialogViewOfInputMethod(usableHeightNow, mChildOfContent, frameLayoutParams);
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        //return (r.bottom - r.top);// 全屏模式下： return r.bottom
        return r.bottom;
    }
}
