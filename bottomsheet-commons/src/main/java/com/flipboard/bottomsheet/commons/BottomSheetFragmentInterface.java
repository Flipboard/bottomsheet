package com.flipboard.bottomsheet.commons;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.flipboard.bottomsheet.ViewTransformer;

/**
 * This interface can be applied to a {@link android.support.v4.app.Fragment} to make it compatible with
 * {@link BottomSheetFragmentDelegate}. You should only have to implement
 */
public interface BottomSheetFragmentInterface {

    /**
     * Display the bottom sheet, adding the fragment to the given FragmentManager. This does
     * <em>not</em> add the transaction to the back stack. When teh fragment is dismissed, a new
     * transaction will be executed  to remove it from the activity.
     *
     * @param manager The FragmentManager this fragment will be added to.
     * @param bottomSheetLayoutId The bottom sheet layoutId in the parent view to attach the
     * fragment to.
     */
    void show(FragmentManager manager, @IdRes int bottomSheetLayoutId);

    /**
     * Display the bottom sheet, adding the fragment using an excising transaction and then
     * committing the transaction.
     *
     * @param transaction An existing transaction in which to add the fragment.
     * @param bottomSheetLayoutId The bottom sheet layoutId in the parent view to attach the
     * fragment to.
     */
    int show(FragmentTransaction transaction, @IdRes int bottomSheetLayoutId);

    /**
     * Dismiss the fragment and it's bottom sheet. If the fragment was added to the back stack, all
     * back stack state up to and including this entry will be popped. Otherwise, a new transaction
     * will be committed to remove this fragment.
     */
    void dismiss();

    /**
     * Version of {@link #dismiss()} that uses {@link FragmentTransaction#commitAllowingStateLoss()}.
     * See linked documentation for further details.
     */
    void dismissAllowingStateLoss();

    /**
     * Override this to proved a custom {@link ViewTransformer}.
     */
    ViewTransformer getViewTransformer();

}
