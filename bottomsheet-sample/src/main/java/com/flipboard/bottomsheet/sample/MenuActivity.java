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
                showMenuSheet(new GridLayoutManager(MenuActivity.this, 3), MenuSheetView.MenuType.RECYCLER_GRID);
            }
        });
        findViewById(R.id.recycler_button_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuSheet(new WrapLinearLayoutManager(MenuActivity.this), MenuSheetView.MenuType.RECYCLER_LIST);
            }
        });
    }

<<<<<<< HEAD
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

    private void showMenuSheet(RecyclerView.LayoutManager layoutManager, MenuSheetView.MenuType menuType){
        MenuSheetView menuSheetView = new MenuSheetView(MenuActivity.this, layoutManager, menuType, "Create...", new MenuSheetView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MenuActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                if (bottomSheetLayout.isSheetShowing()) {
                    bottomSheetLayout.dismissSheet();
                }
                return true;
            }
        });
=======
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
>>>>>>> trunk/master
        menuSheetView.inflateMenu(R.menu.create);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }
}
