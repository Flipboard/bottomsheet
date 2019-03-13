package com.flipboard.bottomsheet.sample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.MenuSheetView;

/**
 * Activity demonstrating the use of {@link MenuSheetView}
 */
public class MenuActivity extends AppCompatActivity {

    protected BottomSheetLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomSheetLayout = findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);
        findViewById(R.id.list_button).setOnClickListener(v -> showMenuSheet(MenuSheetView.MenuType.LIST));
        findViewById(R.id.grid_button).setOnClickListener(v -> showMenuSheet(MenuSheetView.MenuType.GRID));
    }

    private void showMenuSheet(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(MenuActivity.this, menuType, "Create...", item -> {
                    Toast.makeText(MenuActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    if (bottomSheetLayout.isSheetShowing()) {
                        bottomSheetLayout.dismissSheet();
                    }
                    if (item.getItemId() == R.id.reopen) {
                        showMenuSheet(menuType == MenuSheetView.MenuType.LIST ? MenuSheetView.MenuType.GRID : MenuSheetView.MenuType.LIST);
                    }
                    return true;
                });
        menuSheetView.inflateMenu(R.menu.create);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }
}
