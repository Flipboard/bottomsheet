package com.flipboard.bottomsheet;

public interface OnSheetDismissedListener {

    /**
     * Called when the presented sheet has been dismissed.
     *
     * @param bottomSheetLayout The bottom sheet which contained the presented sheet.
     */
    void onDismissed(BottomSheetLayout bottomSheetLayout);

}
