package com.flipboard.bottomsheet;

import android.view.View;

public interface ViewTransformer {

    /**
     * Called on every frame while animating the presented sheet. This method allows you to coordinate
     * other animations (usually on the content view) with the sheet view's translation.
     *
     * @param translation The current translation of the presented sheet view.
     * @param maxTranslation The max translation of the presented sheet view.
     * @param peekedTranslation The peeked state translation of the presented sheet view.
     * @param parent The BottomSheet presenting the sheet view.
     * @param view The content view to transform.
     */
    void transformView(float translation, float maxTranslation, float peekedTranslation, BottomSheetLayout parent, View view);

    /**
     * Called on when the translation of the sheet view changes allowing you to customize the amount of dimming which
     * is applied to the content view.
     *
     * @param translation The current translation of the presented sheet view.
     * @param maxTranslation The max translation of the presented sheet view.
     * @param peekedTranslation The peeked state translation of the presented sheet view.
     * @param parent The BottomSheet presenting the sheet view.
     * @param view The content view to transform.
     *
     * @return The alpha to apply to the dim overlay.
     */
    float getDimAlpha(float translation, float maxTranslation, float peekedTranslation, BottomSheetLayout parent, View view);

}
