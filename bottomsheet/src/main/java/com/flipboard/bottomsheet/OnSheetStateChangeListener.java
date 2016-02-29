package com.flipboard.bottomsheet;

public interface OnSheetStateChangeListener {

    /**
     * Callback for the sheet's state changes.
     *
     * @param state the new state.
     */
    void onSheetStateChanged(BottomSheetLayout.State state);
}
