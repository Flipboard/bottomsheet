package com.flipboard.bottomsheet.commons;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An ImageView that tries to keep a 1:1 aspect ratio
 */
final class ImagePickerSheetImageView extends ImageView {
    public ImagePickerSheetImageView(Context context) {
        super(context);
    }

    public ImagePickerSheetImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImagePickerSheetImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImagePickerSheetImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int bothDimensionsSpec = widthMeasureSpec;
        super.onMeasure(bothDimensionsSpec, bothDimensionsSpec);
    }
}
