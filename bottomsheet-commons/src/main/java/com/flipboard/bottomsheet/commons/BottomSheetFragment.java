package com.flipboard.bottomsheet.commons;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.AccessFragmentInternals;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.OnSheetDismissedListener;
import com.flipboard.bottomsheet.ViewTransformer;

/**
 * A fragment that shows itself in a {@link BottomSheetLayout}. Like a {@link
 * android.support.v4.app.DialogFragment}, you can show this either in a bottom sheet by using
 * {@link #show(FragmentManager, int)} or attach it to a view with the normal fragment transaction
 * methods.
 */
public class BottomSheetFragment extends Fragment implements OnSheetDismissedListener, BottomSheetLayout.OnSheetStateChangeListener {

    private static final String SAVED_SHOWS_BOTTOM_SHEET = "bottomsheet:savedBottomSheet";
    private static final String SAVED_BACK_STACK_ID = "bottomsheet:backStackId";
    private static final String SAVED_BOTTOM_SHEET_LAYOUT_ID = "bottomsheet:bottomSheetLayoutId";

    @IdRes
    private int bottomSheetLayoutId = View.NO_ID;
    private BottomSheetLayout bottomSheetLayout;
    private boolean dismissed;
    private boolean shownByMe;
    private boolean viewDestroyed;
    private boolean showsBottomSheet = true;
    private int backStackId = -1;

    public BottomSheetFragment() {

    }

    /**
     * Display the bottom sheet, adding the fragment to the given FragmentManager. This does
     * <em>not</em> add the transaction to the back stack. When teh fragment is dismissed, a new
     * transaction will be executed  to remove it from the activity.
     *
     * @param manager             The FragmentManager this fragment will be added to.
     * @param bottomSheetLayoutId The bottom sheet layoutId in the parent view to attach the
     *                            fragment to.
     */
    public void show(FragmentManager manager, @IdRes int bottomSheetLayoutId) {
        dismissed = false;
        shownByMe = true;
        this.bottomSheetLayoutId = bottomSheetLayoutId;
        manager.beginTransaction()
                .add(this, String.valueOf(bottomSheetLayoutId))
                .commit();
    }

    /**
     * Display the bottom sheet, adding the fragment using an excising transaction and then
     * committing the transaction.
     *
     * @param transaction         An existing transaction in which to add the fragment.
     * @param bottomSheetLayoutId The bottom sheet layoutId in the parent view to attach the
     *                            fragment to.
     */
    public int show(FragmentTransaction transaction, @IdRes int bottomSheetLayoutId) {
        dismissed = false;
        shownByMe = true;
        this.bottomSheetLayoutId = bottomSheetLayoutId;
        transaction.add(this, String.valueOf(bottomSheetLayoutId));
        viewDestroyed = false;
        backStackId = transaction.commit();
        return backStackId;
    }

    /**
     * Dismiss the fragment and it's bottom sheet. If the fragment was added to the back stack, all
     * back stack state up to and including this entry will be popped. Otherwise, a new transaction
     * will be committed to remove this fragment.
     */
    public void dismiss() {
        dismissInternal(/*allowStateLoss=*/false);
    }

    /**
     * Version of {@link #dismiss()} that uses {@link FragmentTransaction#commitAllowingStateLoss()}.
     * See linked documentation for further details.
     */
    public void dismissAllowingStateLoss() {
        dismissInternal(/*allowStateLoss=*/true);
    }

    private void dismissInternal(boolean allowStateLoss) {
        if (dismissed) {
            return;
        }
        dismissed = true;
        shownByMe = false;
        if (bottomSheetLayout != null) {
            bottomSheetLayout.dismissSheet();
            bottomSheetLayout = null;
        }
        viewDestroyed = true;
        if (backStackId >= 0) {
            getFragmentManager().popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            backStackId = -1;
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.remove(this);
            if (allowStateLoss) {
                ft.commitAllowingStateLoss();
            } else {
                ft.commit();
            }
        }
    }

    public BottomSheetLayout getBottomSheetLayout() {
        return bottomSheetLayout;
    }

    /**
     * Override this to proved a custom {@link ViewTransformer}.
     */
    public ViewTransformer getViewTransformer() {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!shownByMe) {
            dismissed = false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!shownByMe && !dismissed) {
            dismissed = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showsBottomSheet = AccessFragmentInternals.getContainerId(this) == 0;

        if (savedInstanceState != null) {
            showsBottomSheet = savedInstanceState.getBoolean(SAVED_SHOWS_BOTTOM_SHEET, showsBottomSheet);
            backStackId = savedInstanceState.getInt(SAVED_BACK_STACK_ID, -1);
            bottomSheetLayoutId = savedInstanceState.getInt(SAVED_BOTTOM_SHEET_LAYOUT_ID, View.NO_ID);
        }
    }

    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        if (!showsBottomSheet) {
            return super.getLayoutInflater(savedInstanceState);
        }
        bottomSheetLayout = (BottomSheetLayout) findBottomSheetLayout();
        if (bottomSheetLayout != null) {
            return LayoutInflater.from(bottomSheetLayout.getContext());
        }
        return LayoutInflater.from(getContext());
    }

    @Nullable
    private View findBottomSheetLayout() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            View view = parentFragment.getView();
            if (view != null) {
                return view.findViewById(bottomSheetLayoutId);
            } else {
                return null;
            }
        }
        Activity parentActivity = getActivity();
        if (parentActivity != null) {
            return parentActivity.findViewById(bottomSheetLayoutId);
        }
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!showsBottomSheet) {
            return;
        }

        View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException("BottomSheetFragment can not be attached to a container view");
            }
            bottomSheetLayout.setOnSheetStateChangeListener(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bottomSheetLayout != null) {
            viewDestroyed = false;
            bottomSheetLayout.showWithSheetView(getView(), getViewTransformer(), this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!showsBottomSheet) {
            outState.putBoolean(SAVED_SHOWS_BOTTOM_SHEET, false);
        }
        if (backStackId != -1) {
            outState.putInt(SAVED_BACK_STACK_ID, backStackId);
        }
        if (bottomSheetLayoutId != View.NO_ID) {
            outState.putInt(SAVED_BOTTOM_SHEET_LAYOUT_ID, bottomSheetLayoutId);
        }
    }

    /**
     * Remove bottom sheet.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bottomSheetLayout != null) {
            viewDestroyed = true;
            bottomSheetLayout.dismissSheet();
            bottomSheetLayout = null;
        }
    }

    @Override
    @CallSuper
    public void onDismissed(BottomSheetLayout bottomSheetLayout) {
        if (!viewDestroyed) {
            dismissInternal(true);
        }
    }

    @Override
    public void onSheetStateChanged(BottomSheetLayout.State state) {

    }
}
