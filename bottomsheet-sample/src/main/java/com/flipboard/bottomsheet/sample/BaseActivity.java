package com.flipboard.bottomsheet.sample;

import android.support.v7.app.AppCompatActivity;

import com.flipboard.bottomsheet.BottomSheetLayout;

/**
 * Base activity with some components for the sample activities
 */
public class BaseActivity extends AppCompatActivity {

    protected BottomSheetLayout bottomSheetLayout;

    @Override
    public void onBackPressed() {
        if (bottomSheetLayout.isSheetShowing()) {
            if (bottomSheetLayout.getState() == BottomSheetLayout.State.EXPANDED) {
                bottomSheetLayout.peekSheet();
            } else {
                bottomSheetLayout.dismissSheet();
            }
        } else {
            super.onBackPressed();
        }
    }
}
