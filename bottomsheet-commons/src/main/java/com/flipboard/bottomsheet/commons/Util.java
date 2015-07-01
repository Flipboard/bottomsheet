package com.flipboard.bottomsheet.commons;

import android.content.Context;
import android.util.TypedValue;

public class Util {

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

}
