package com.flipboard.bottomsheet.commons;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.ViewTransformer;

/**
 * A fragment that shows itself in a {@link BottomSheetLayout}. Like a {@link
 * android.support.v4.app.DialogFragment}, you can show this either in a bottom sheet by using
 * {@link #show(FragmentManager, int)} or attach it to a view with the normal fragment transaction
 * methods.
 * <p>
 * If you don't want to extend from this for your fragment instance, you can use {@link BottomSheetFragmentDelegate}
 * in your fragment implementation instead. You must, however, still implement {@link BottomSheetFragmentInterface}.
 */
public class BottomSheetFragment extends Fragment implements BottomSheetFragmentInterface {

    private BottomSheetFragmentDelegate delegate;

    public BottomSheetFragment() { }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show(FragmentManager manager, @IdRes int bottomSheetLayoutId) {
        getDelegate().show(manager, bottomSheetLayoutId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int show(FragmentTransaction transaction, @IdRes int bottomSheetLayoutId) {
        return getDelegate().show(transaction, bottomSheetLayoutId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dismiss() {
        getDelegate().dismiss();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dismissAllowingStateLoss() {
        getDelegate().dismissAllowingStateLoss();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTransformer getViewTransformer() {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getDelegate().onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getDelegate().onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().onCreate(savedInstanceState);
    }

    @Override
    public LayoutInflater getLayoutInflater(Bundle savedInstanceState) {
        return getDelegate().getLayoutInflater(savedInstanceState, super.getLayoutInflater(savedInstanceState));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDelegate().onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDelegate().onStart();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getDelegate().onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        getDelegate().onDestroyView();
        super.onDestroyView();
    }

    private BottomSheetFragmentDelegate getDelegate() {
        if (delegate == null) {
            delegate = BottomSheetFragmentDelegate.create(this);
        }
        return delegate;
    }
}
