package com.flipboard.bottomsheet.sample;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.MenuSheetView;

/**
 * Activity demonstrating the use of {@link MenuSheetView}
 */
public class MenuActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        findViewById(R.id.list_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuSheet(MenuSheetView.MenuType.LIST);
            }
        });
        findViewById(R.id.grid_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuSheet(MenuSheetView.MenuType.GRID);
            }
        });
    }

    private void showMenuSheet(MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(MenuActivity.this, menuType, "Create...", new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MenuActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.create);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }
}
