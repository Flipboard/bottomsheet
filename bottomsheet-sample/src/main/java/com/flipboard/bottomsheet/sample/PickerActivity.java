package com.flipboard.bottomsheet.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;

import java.util.Collections;

/**
 * Activity demonstrating the use of {@link IntentPickerSheetView}
 */
public class PickerActivity extends AppCompatActivity {

    protected BottomSheetLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        bottomSheetLayout = findViewById(R.id.bottomsheet);
        final TextView shareText = findViewById(R.id.share_text);
        final Button shareButton = findViewById(R.id.share_button);

        shareButton.setOnClickListener(v -> {
            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(shareText.getWindowToken(), 0);

            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.getText().toString());
            shareIntent.setType("text/plain");
            IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(PickerActivity.this, shareIntent, "Share with...", activityInfo -> {
                bottomSheetLayout.dismissSheet();
                startActivity(activityInfo.getConcreteIntent(shareIntent));
            });
            // Filter out built in sharing options such as bluetooth and beam.
            intentPickerSheet.setFilter(info -> !info.componentName.getPackageName().startsWith("com.android"));
            // Sort activities in reverse order for no good reason
            intentPickerSheet.setSortMethod((lhs, rhs) -> rhs.label.compareTo(lhs.label));

            // Add custom mixin example
            Drawable customDrawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_launcher, null);
            IntentPickerSheetView.ActivityInfo customInfo = new IntentPickerSheetView.ActivityInfo(customDrawable, "Custom mix-in", PickerActivity.this, MainActivity.class);
            intentPickerSheet.setMixins(Collections.singletonList(customInfo));

            bottomSheetLayout.showWithSheetView(intentPickerSheet);
        });
    }
}
