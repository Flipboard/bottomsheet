package com.flipboard.bottomsheet;

import android.view.View;

public interface DimAlphaTransformer {
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
