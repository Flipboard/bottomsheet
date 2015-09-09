package com.flipboard.bottomsheet.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.R;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.flipboard.bottomsheet.commons.WrapLinearLayoutManager;

/**
 * Activity demonstrating the use of {@link MenuSheetView}
 */
public class MenuActivity extends AppCompatActivity {

    protected BottomSheetLayout bottomSheetLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomsheet);
        bottomSheetLayout.setPeekOnDismiss(true);
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
        findViewById(R.id.recycler_button_grid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuSheet(MenuSheetView.MenuType.RECYCLER_GRID, new GridLayoutManager(MenuActivity.this, 3));
            }
        });
        findViewById(R.id.recycler_button_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuSheet(MenuSheetView.MenuType.RECYCLER_LIST, new WrapLinearLayoutManager(MenuActivity.this));
            }
        });
    }

    private void showMenuSheet( final MenuSheetView.MenuType menuType, RecyclerView.LayoutManager layoutManager) {
        MenuSheetView menuSheetView = new MenuSheetView(MenuActivity.this, layoutManager, menuType, "Create...", new MenuSheetView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MenuActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                if (bottomSheetLayout.isSheetShowing()) {
                    bottomSheetLayout.dismissSheet();
                }
                if (item.getItemId() == R.id.reopen) {
                    showMenuSheet(menuType == MenuSheetView.MenuType.RECYCLER_LIST ? MenuSheetView.MenuType.RECYCLER_GRID : MenuSheetView.MenuType.RECYCLER_LIST,  getOtherLayoutManager(menuType));
                }
                return true;
            }
        });

        menuSheetView.inflateMenu(R.menu.create);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void showMenuSheet(final MenuSheetView.MenuType menuType) {
        MenuSheetView menuSheetView =
                new MenuSheetView(MenuActivity.this, menuType, "Create...", new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(MenuActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        if (item.getItemId() == R.id.reopen) {
                            showMenuSheet(menuType == MenuSheetView.MenuType.LIST ? MenuSheetView.MenuType.GRID : MenuSheetView.MenuType.LIST);
                        }
                        return true;
                    }
                });

        menuSheetView.inflateMenu(R.menu.create);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private RecyclerView.LayoutManager getOtherLayoutManager(MenuSheetView.MenuType menuType){
        return menuType == MenuSheetView.MenuType.RECYCLER_LIST ? new GridLayoutManager(MenuActivity.this, 3) : new WrapLinearLayoutManager(MenuActivity.this);
    }
}
