package com.flipboard.bottomsheet.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.IntentPickerSheetView;

import java.util.Comparator;


public class MainActivity extends Activity {

    BottomSheetLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        final TextView shareText = (TextView) findViewById(R.id.share_text);
        final Button shareButton = (Button) findViewById(R.id.share_button);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.getText().toString());
                shareIntent.setType("text/plain");
                IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(MainActivity.this, shareIntent, "Share with...", new IntentPickerSheetView.OnIntentPickedListener() {
                    @Override
                    public void onIntentPicked(Intent intent) {
                        bottomSheetLayout.dismissSheet();
                        startActivity(intent);
                    }
                });
                // Filter out built in sharing options such as bluetooth and beam.
                intentPickerSheet.setFilter(new IntentPickerSheetView.Filter() {
                    @Override
                    public boolean include(IntentPickerSheetView.ActvityInfo info) {
                        return !info.componentName.getPackageName().startsWith("com.android");
                    }
                });
                // Sort activities in reverse order for no good reason
                intentPickerSheet.setSortMethod(new Comparator<IntentPickerSheetView.ActvityInfo>() {
                    @Override
                    public int compare(IntentPickerSheetView.ActvityInfo lhs, IntentPickerSheetView.ActvityInfo rhs) {
                        return rhs.label.compareTo(lhs.label);
                    }
                });
                bottomSheetLayout.showWithSheetView(intentPickerSheet);
            }
        });
    }

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
