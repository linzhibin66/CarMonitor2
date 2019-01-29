package com.orhanobut.dialogplus;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ljn on 2017/2/20.
 */
public class EmptyHolder implements Holder{

    private int viewResourceId;
    private View layout;
    private View.OnKeyListener keyListener;

    public EmptyHolder(int viewResourceId) {
        this.viewResourceId = viewResourceId;
    }

    @Override
    public void addHeader(View view) {

    }

    @Override
    public void addFooter(View view) {

    }

    @Override
    public void setBackgroundResource(int colorResource) {

    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        layout = inflater.inflate(viewResourceId, parent, false);
        layout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyListener == null) {
                    throw new NullPointerException("keyListener should not be null");
                }
                return keyListener.onKey(v, keyCode, event);
            }
        });
        return layout;
    }

    @Override
    public void setOnKeyListener(View.OnKeyListener keyListener) {
        this.keyListener = keyListener;

    }

    @Override
    public View getInflatedView() {
        return layout;
    }

    @Override
    public View getHeader() {
        return null;
    }

    @Override
    public View getFooter() {
        return null;
    }

    @Override
    public void setContentView(View mContentView) {

    }

}
