package com.flipboard.bottomsheet.sample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.BottomSheetPagerAdapter;
import com.flipboard.bottomsheet.commons.PagerSheetView;

/**
 * Activity demonstrating {@link PagerSheetView}
 */
public class PagerActivity extends AppCompatActivity {

    private static final String ARG_POSITION = "ARG_POS";

    private BottomSheetLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);
        findViewById(R.id.icons_pager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerSheetView pagerSheetView = new PagerSheetView(PagerActivity.this, PagerSheetView.TabStyle.Icons, TabLayout.MODE_FIXED);
                pagerSheetView.setAdapter(new TestBottomSheetPagerAdapter(getSupportFragmentManager()));
                bottomSheetLayout.showWithSheetView(pagerSheetView);
            }
        });
        findViewById(R.id.text_pager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerSheetView pagerSheetView = new PagerSheetView(PagerActivity.this, PagerSheetView.TabStyle.Text, TabLayout.MODE_FIXED);
                pagerSheetView.setAdapter(new TestBottomSheetPagerAdapter(getSupportFragmentManager()));
                bottomSheetLayout.showWithSheetView(pagerSheetView);
            }
        });
        findViewById(R.id.icons_text_pager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerSheetView pagerSheetView = new PagerSheetView(PagerActivity.this, PagerSheetView.TabStyle.IconsAndText, TabLayout.MODE_FIXED);
                pagerSheetView.setAdapter(new TestBottomSheetPagerAdapter(getSupportFragmentManager()));
                bottomSheetLayout.showWithSheetView(pagerSheetView);
            }
        });
        findViewById(R.id.custom_pager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerSheetView pagerSheetView = new PagerSheetView(PagerActivity.this, PagerSheetView.TabStyle.View, TabLayout.MODE_FIXED);
                pagerSheetView.setAdapter(new TestBottomSheetPagerAdapter(getSupportFragmentManager()));
                bottomSheetLayout.showWithSheetView(pagerSheetView);
            }
        });
        findViewById(R.id.scrollable_pager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerSheetView pagerSheetView = new PagerSheetView(PagerActivity.this, PagerSheetView.TabStyle.Text, TabLayout.MODE_SCROLLABLE);
                pagerSheetView.setAdapter(new TestScrollBottomSheetPagerAdapter(getSupportFragmentManager()));
                bottomSheetLayout.showWithSheetView(pagerSheetView);
            }
        });
    }

    public class TestBottomSheetPagerAdapter extends BottomSheetPagerAdapter {

        public TestBottomSheetPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TestFragment fragment = new TestFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + position;
        }

        @Override
        public Drawable getPageIcon(int position) {
            return getResources().getDrawable(R.mipmap.ic_launcher);
        }

        @Override
        public View getPageCustomView(ViewGroup root, int position) {
            View v = getLayoutInflater().inflate(R.layout.custom_tab, root, false);
            ((TextView) v.findViewById(R.id.tab_text)).setText("" + position);
            return v;
        }
    }

    public class TestScrollBottomSheetPagerAdapter extends BottomSheetPagerAdapter {

        public TestScrollBottomSheetPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TestFragment fragment = new TestFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + position;
        }

    }

    public static class TestFragment extends Fragment {

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            int position = args.getInt(ARG_POSITION, 0);
            View v = inflater.inflate(R.layout.pager_item, container, false);
            ((TextView) v.findViewById(R.id.text)).setText("Hello from page " + position);
            return v;
        }

    }

}
