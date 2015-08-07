package com.flipboard.bottomsheet.commons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

class BottomSheetTabLayout extends TabLayout {

    private boolean useCustomView = false;
    private boolean useIcons = false;
    private boolean useText = true;

    public BottomSheetTabLayout(Context context) {
        this(context, null, 0);
    }

    public BottomSheetTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomSheetTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTabsFromPagerAdapter(@NonNull PagerAdapter adapter) {
        if (adapter instanceof BottomSheetPagerAdapter) {
            BottomSheetPagerAdapter bottomSheetPagerAdapter = (BottomSheetPagerAdapter) adapter;
            removeAllTabs();
            int i = 0;

            for (int count = adapter.getCount(); i < count; ++i) {
                Tab tab = newTab();
                if (useCustomView) {
                    tab.setCustomView(bottomSheetPagerAdapter.getPageCustomView(this, i));
                } else {
                    if (useIcons) {
                        tab.setIcon(bottomSheetPagerAdapter.getPageIcon(i));
                    }

                    if (useText) {
                        tab.setText(bottomSheetPagerAdapter.getPageTitle(i));
                    }
                }

                addTab(tab);
            }
        } else {
            super.setTabsFromPagerAdapter(adapter);
        }
    }

    public void setUseIcons(boolean useIcons) {
        this.useIcons = useIcons;
    }

    public void setUseText(boolean useText) {
        this.useText = useText;
    }

    public void setUseCustomView(boolean useCustomView) {
        this.useCustomView = useCustomView;
    }
}
