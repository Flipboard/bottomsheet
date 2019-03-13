package com.flipboard.bottomsheet.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.ImagePickerSheetView;

/**
 * Activity demonstrating the use of {@link ImagePickerSheetView}
 */
public final class BottomSheetFragmentActivity extends AppCompatActivity {

    protected BottomSheetLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet_fragment);
        bottomSheetLayout = findViewById(R.id.bottomsheet);
        findViewById(R.id.bottomsheet_fragment_button).setOnClickListener(v -> new MyFragment().show(getSupportFragmentManager(), R.id.bottomsheet));
    }
}
