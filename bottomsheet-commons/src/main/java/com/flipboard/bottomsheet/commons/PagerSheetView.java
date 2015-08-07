package com.flipboard.bottomsheet.commons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import flipboard.bottomsheet.commons.R;

/**
 * A sheetview that can display a viewpager with tabs
 * <p>
 * A PagerSheetView can be styled by adding
 * <code><style name="BottomSheetTabLayout" parent="Widget.Design.TabLayout"></style><code/> to the
 * application's styles.xml
 * </p>
 */
@SuppressLint("ViewConstructor")
public class PagerSheetView extends FrameLayout {

    /** Supported tab styles */
    public enum TabStyle {
        Text,
        Icons,
        IconsAndText,
        View
    }

    private ViewPager viewPager;
    private BottomSheetTabLayout tabLayout;

    /**
     * @param context Context to construct the view with
     * @param tabStyle Style of tabs to use
     * @param tabMode Tab mode either {@link android.support.design.widget.TabLayout#MODE_FIXED} or
     *                {@link android.support.design.widget.TabLayout#MODE_SCROLLABLE}
     */
    public PagerSheetView(Context context, TabStyle tabStyle, int tabMode) {
        super(context);
        inflate(context, R.layout.pager_sheet_view, this);
        tabLayout = (BottomSheetTabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout.setUseText(tabStyle == TabStyle.Text || tabStyle == TabStyle.IconsAndText);
        tabLayout.setUseIcons(tabStyle == TabStyle.Icons || tabStyle == TabStyle.IconsAndText);
        tabLayout.setUseCustomView(tabStyle == TabStyle.View);
        tabLayout.setTabMode(tabMode);
    }

    public void setAdapter(BottomSheetPagerAdapter adapter) {
        viewPager.setAdapter(adapter);

        // Relevant bug report on why we set it up this way: https://code.google.com/p/android/issues/detail?id=180462#c17
        if (ViewCompat.isLaidOut(tabLayout)) {
            tabLayout.setupWithViewPager(viewPager);
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                tabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        tabLayout.setupWithViewPager(viewPager);
                        tabLayout.removeOnLayoutChangeListener(this);
                    }
                });
            } else {
                tabLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.setupWithViewPager(viewPager);
                    }
                });
            }
        }
    }

}
