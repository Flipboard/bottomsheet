package com.flipboard.bottomsheet.sample;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.flipboard.bottomsheet.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.picker_button).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PickerActivity.class)));

        findViewById(R.id.menu_button).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MenuActivity.class)));

        findViewById(R.id.image_picker_button).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ImagePickerActivity.class)));

        findViewById(R.id.bottomsheet_fragment_button).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BottomSheetFragmentActivity.class)));
    }
}
