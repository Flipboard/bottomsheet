package com.flipboard.bottomsheet.commons;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public abstract class BottomSheetPagerAdapter extends FragmentStatePagerAdapter {

    public BottomSheetPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Override this method for tabs with text
     *
     * @param position position of the page
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    /**
     * Override this method for tabs with icons
     *
     * @param position position of the page
     * @return
     */
    public Drawable getPageIcon(int position) {
        return null;
    }

    /**
     * Override this method for tabs with a custom view
     *
     * @param position
     * @return
     */
    public View getPageCustomView(ViewGroup root, int position) {
        return null;
    }

}
