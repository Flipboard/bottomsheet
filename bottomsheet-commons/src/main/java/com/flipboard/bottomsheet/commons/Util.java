package com.flipboard.bottomsheet.commons;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Outline;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;

class Util {

    /**
     * Convert a dp float value to pixels
     *
     * @param dp      float value in dps to convert
     * @return DP value converted to pixels
     */
    static int dp2px(Context context, float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return Math.round(px);
    }

    /**
     * A helper class for providing a shadow on sheets
     */
    @TargetApi(21)
    static class ShadowOutline extends ViewOutlineProvider {

        int width;
        int height;

        ShadowOutline(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRect(0, 0, width, height);
        }
    }

    private Util() {
        throw new AssertionError("No Instances");
    }
}
