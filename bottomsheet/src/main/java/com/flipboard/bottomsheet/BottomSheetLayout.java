package com.flipboard.bottomsheet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class BottomSheetLayout extends FrameLayout {

    private static final Property<BottomSheetLayout, Float> SHEET_TRANSLATION = new Property<BottomSheetLayout, Float>(Float.class, "sheetTranslation") {
        @Override
        public Float get(BottomSheetLayout object) {
            return object.getSheetTranslation();
        }

        @Override
        public void set(BottomSheetLayout object, Float value) {
            object.setSheetTranslation(value);
        }
    };

    /**
     * Utility class which registers if the animation has been canceled so that subclasses may respond differently in onAnimationEnd
     */
    private class CancelDetectionAnimationListener extends AnimatorListenerAdapter {

        protected boolean canceled;

        @Override
        public void onAnimationCancel(Animator animation) {
            canceled = true;
        }

    }

    private class IdentityViewTransformer extends BaseViewTransformer {
        @Override
        public void transformView(float translation, float maxTranslation, float peekedTranslation, BottomSheetLayout parent, View view) {
            // no-op
        }
    }

    public enum State {
        HIDDEN,
        PEEKED,
        EXPANDED
    }

    public interface OnSheetStateChangeListener {
        void onSheetStateChanged(State state);
    }

    private static final long ANIMATION_DURATION = 300;

    private Rect contentClipRect = new Rect();
    private State state = State.HIDDEN;
    private TimeInterpolator animationInterpolator = new DecelerateInterpolator(1.6f);
    public boolean bottomSheetOwnsTouch;
    private boolean sheetViewOwnsTouch;
    private float sheetTranslation;
    private VelocityTracker velocityTracker;
    private float minFlingVelocity;
    private float touchSlop;
    private ViewTransformer defaultViewTransformer = new IdentityViewTransformer();
    private ViewTransformer viewTransformer;
    private OnSheetDismissedListener onSheetDismissedListener;
    private boolean shouldDimContentView = true;
    private boolean useHardwareLayerWhileAnimating = true;
    private Animator currentAnimator;
    private OnSheetStateChangeListener onSheetStateChangeListener;
    private View dimView;

    /** Snapshot of the touch's y position on a down event */
    private float downY;

    /** Snapshot of the touch's x position on a down event */
    private float downX;

    /** Snapshot of the sheet's translation at the time of the last down event */
    private float downSheetTranslation;

    /** Snapshot of the sheet's state at the time of the last down event */
    private State downState;

    public BottomSheetLayout(Context context) {
        super(context);
        init();
    }

    public BottomSheetLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomSheetLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomSheetLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        minFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        touchSlop = viewConfiguration.getScaledTouchSlop();

        dimView = new View(getContext());
        dimView.setBackgroundColor(Color.BLACK);
        dimView.setAlpha(0);
    }

    /**
     * Don't call addView directly, use setContentView() and showWithSheetView()
     */
    @Override
    public void addView(@NonNull View child) {
        if (getChildCount() > 0) {
            throw new IllegalArgumentException("You may not declare more then one child of bottom sheet. The sheet view must be added dynamically with showWithSheetView()");
        }
        setContentView(child);
    }

    @Override
    public void addView(@NonNull View child, int index) {
        addView(child);
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        addView(child);
    }

    @Override
    public void addView(@NonNull View child, ViewGroup.LayoutParams params) {
        addView(child);
    }

    @Override
    public void addView(@NonNull View child, int width, int height) {
        addView(child);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        velocityTracker.clear();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int bottomClip = (int) (getHeight() - Math.ceil(sheetTranslation));
        this.contentClipRect.set(0, 0, getWidth(), bottomClip);
    }

    private void setSheetTranslation(float sheetTranslation) {
        this.sheetTranslation = sheetTranslation;
        int bottomClip = (int) (getHeight() - Math.ceil(sheetTranslation));
        this.contentClipRect.set(0, 0, getWidth(), bottomClip);
        getSheetView().setTranslationY(getHeight() - sheetTranslation);
        transformView(sheetTranslation);
        dimView.setAlpha(shouldDimContentView ? getDimAlpha(sheetTranslation) : 0);
    }

    private void transformView(float sheetTranslation) {
        if (viewTransformer != null) {
            viewTransformer.transformView(sheetTranslation, getMaxSheetTranslation(), getPeekSheetTranslation(), this, getContentView());
        } else if (defaultViewTransformer != null) {
            defaultViewTransformer.transformView(sheetTranslation, getMaxSheetTranslation(), getPeekSheetTranslation(), this, getContentView());
        }
    }

    private float getDimAlpha(float sheetTranslation) {
        if (viewTransformer != null) {
            return viewTransformer.getDimAlpha(sheetTranslation, getMaxSheetTranslation(), getPeekSheetTranslation(), this, getContentView());
        } else if (defaultViewTransformer != null) {
            return defaultViewTransformer.getDimAlpha(sheetTranslation, getMaxSheetTranslation(), getPeekSheetTranslation(), this, getContentView());
        }
        return 0;
    }

    private float getSheetTranslation() {
        return sheetTranslation;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSheetShowing();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isSheetShowing()) {
            return false;
        }
        if (isAnimating()) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Snapshot the state of things when finger touches the screen.
            // This allows us to calculate deltas without losing precision which we would have if we calculated deltas based on the previous touch.
            bottomSheetOwnsTouch = false;
            sheetViewOwnsTouch = false;
            downY = event.getY();
            downX = event.getX();
            downSheetTranslation = getSheetTranslation();
            downState = state;
            velocityTracker.clear();
        }
        velocityTracker.addMovement(event);

        // The max translation is a hard limit while the min translation is where we start dragging more slowly and allow the sheet to be dismissed.
        float maxSheetTranslation = getMaxSheetTranslation();
        float peekSheetTranslation = getPeekSheetTranslation();

        float deltaY = downY - event.getY();
        float deltaX = downX - event.getX();

        if (!bottomSheetOwnsTouch && !sheetViewOwnsTouch) {
            bottomSheetOwnsTouch = Math.abs(deltaY) > touchSlop;
            sheetViewOwnsTouch = Math.abs(deltaX) > touchSlop;

            if (bottomSheetOwnsTouch) {
                if (state == State.PEEKED) {
                    MotionEvent cancelEvent = MotionEvent.obtain(event);
                    cancelEvent.offsetLocation(0, sheetTranslation - getHeight());
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                    getSheetView().dispatchTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                sheetViewOwnsTouch = false;
                downY = event.getY();
                downX = event.getX();
                deltaY = 0;
                deltaX = 0;
            }
        }

        // This is not the actual new sheet translation but a first approximation it will be adjusted to account for max and min translations etc.
        float newSheetTranslation = downSheetTranslation + deltaY;

        if (bottomSheetOwnsTouch) {
            // If we are scrolling down and the sheet cannot scroll further, go out of expanded mode.
            boolean scrollingDown = deltaY < 0;
            boolean canScrollUp = canScrollUp(getSheetView(), event.getX(), event.getY() + (sheetTranslation - getHeight()));
            if (state == State.EXPANDED && scrollingDown && !canScrollUp) {
                // Reset variables so deltas are correctly calculated from the point at which the sheet was 'detached' from the top.
                downY = event.getY();
                downSheetTranslation = getSheetTranslation();
                velocityTracker.clear();
                setState(State.PEEKED);
                setSheetLayerTypeIfEnabled(LAYER_TYPE_HARDWARE);
                newSheetTranslation = getSheetTranslation();

                // Dispatch a cancel event to the sheet to make sure its touch handling is cleaned up nicely.
                MotionEvent cancelEvent = MotionEvent.obtain(event);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                getSheetView().dispatchTouchEvent(cancelEvent);
                cancelEvent.recycle();
            }

            // If we are at the top of the view we should go into expanded mode.
            if (state == State.PEEKED && newSheetTranslation > maxSheetTranslation) {
                setSheetTranslation(maxSheetTranslation);

                // Dispatch a down event to the sheet to make sure its touch handling is initiated correctly.
                newSheetTranslation = Math.min(maxSheetTranslation, newSheetTranslation);
                MotionEvent downEvent = MotionEvent.obtain(event);
                downEvent.setAction(MotionEvent.ACTION_DOWN);
                getSheetView().dispatchTouchEvent(downEvent);
                downEvent.recycle();
                setState(State.EXPANDED);
                setSheetLayerTypeIfEnabled(LAYER_TYPE_NONE);
            }

            if (state == State.EXPANDED) {
                // Dispatch the touch to the sheet if we are expanded so it can handle its own internal scrolling.
                event.offsetLocation(0, sheetTranslation - getHeight());
                getSheetView().dispatchTouchEvent(event);
            } else {
                // Make delta less effective when sheet is below the minimum translation.
                // This makes it feel like scrolling in jello which gives the user an indication that the sheet will be dismissed if they let go.
                if (newSheetTranslation < peekSheetTranslation) {
                    newSheetTranslation = peekSheetTranslation - (peekSheetTranslation - newSheetTranslation) / 4f;
                }

                setSheetTranslation(newSheetTranslation);

                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // If touch is canceled, go back to previous state, a canceled touch should never commit an action.
                    if (downState == State.EXPANDED) {
                        expandSheet();
                    } else {
                        peekSheet();
                    }
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (newSheetTranslation < peekSheetTranslation) {
                        dismissSheet();
                    } else {
                        // If touch is released, go to a new state depending on velocity.
                        // If the velocity is not high enough we use the position of the sheet to determine the new state.
                        velocityTracker.computeCurrentVelocity(1000);
                        float velocityY = velocityTracker.getYVelocity();
                        if (Math.abs(velocityY) < minFlingVelocity) {
                            if (getSheetTranslation() > getHeight() / 2) {
                                expandSheet();
                            } else {
                                peekSheet();
                            }
                        } else {
                            if (velocityY < 0) {
                                expandSheet();
                            } else {
                                peekSheet();
                            }
                        }
                    }
                }
            }
        } else {
            // If the user clicks outside of the bottom sheet area we should dismiss the bottom sheet.
            boolean touchAboveBottomSheet = event.getY() < (getHeight() - getSheetTranslation());
            if (event.getAction() == MotionEvent.ACTION_UP && touchAboveBottomSheet) {
                dismissSheet();
                return true;
            }

            event.offsetLocation(0, sheetTranslation - getHeight());
            getSheetView().dispatchTouchEvent(event);
        }
        return true;
    }

    private boolean isAnimating() {
        return currentAnimator != null;
    }

    private void cancelCurrentAnimation() {
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
    }

    private boolean canScrollUp(View view, float x, float y) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                int childLeft = child.getLeft();
                int childTop = child.getTop();
                int childRight = child.getRight();
                int childBottom = child.getBottom();
                boolean intersects = x > childLeft && x < childRight && y > childTop && y < childBottom;
                if (intersects && canScrollUp(child, x - childLeft, y - childTop)) {
                    return true;
                }
            }
        }
        return view.canScrollVertically(-1);
    }

    private void setSheetLayerTypeIfEnabled(int layerType) {
        if (useHardwareLayerWhileAnimating) {
            getSheetView().setLayerType(layerType, null);
        }
    }

    private void setState(State state) {
        this.state = state;
        if (onSheetStateChangeListener != null) {
            onSheetStateChangeListener.onSheetStateChanged(state);
        }
    }

    private boolean hasFullHeightSheet() {
        return getSheetView() == null || getSheetView().getHeight() == getHeight();
    }

    /**
     * Set dim and translation to the initial state
     * */
    private void initializeSheetValues() {
        this.sheetTranslation = 0;
        this.contentClipRect.set(0, 0, getWidth(), getHeight());
        getSheetView().setTranslationY(getHeight());
        dimView.setAlpha(0);
    }

    /**
     * Set the presented sheet to be in an expanded state.
     */
    public void expandSheet() {
        cancelCurrentAnimation();
        setSheetLayerTypeIfEnabled(LAYER_TYPE_NONE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, SHEET_TRANSLATION, getHeight());
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(animationInterpolator);
        anim.addListener(new CancelDetectionAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled) {
                    currentAnimator = null;
                }
            }
        });
        anim.start();
        currentAnimator = anim;
        setState(State.EXPANDED);
    }

    /**
     * Set the presented sheet to be in a peeked state.
     */
    public void peekSheet() {
        cancelCurrentAnimation();
        setSheetLayerTypeIfEnabled(LAYER_TYPE_HARDWARE);
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, SHEET_TRANSLATION, getPeekSheetTranslation());
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(animationInterpolator);
        anim.addListener(new CancelDetectionAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled) {
                    currentAnimator = null;
                }
            }
        });
        anim.start();
        currentAnimator = anim;
        setState(State.PEEKED);
    }

    /**
     * @return The peeked state translation for the presented sheet view. Translation is counted from the bottom of the view.
     */
    public float getPeekSheetTranslation() {
        return hasFullHeightSheet() ?  getHeight() / 3 : getSheetView().getHeight();
    }

    /**
     * @return The maximum translation for the presented sheet view. Translation is counted from the bottom of the view.
     */
    public float getMaxSheetTranslation() {
        return hasFullHeightSheet() ? getHeight() - getPaddingTop() : getSheetView().getHeight();
    }

    /**
     * @return The currently presented sheet view. If no sheet is currently presented null will returned.
     */
    public View getContentView() {
        return getChildCount() > 0 ? getChildAt(0) : null;
    }

    /**
     * @return The currently presented sheet view. If no sheet is currently presented null will returned.
     */
    public View getSheetView() {
        return getChildCount() > 2 ? getChildAt(2) : null;
    }

    /**
     * Set the content view of the bottom sheet. This is the view which is shown under the sheet
     * being presented. This is usually the root view of your application.
     *
     * @param contentView The content view of your application.
     */
    public void setContentView(View contentView) {
        super.addView(contentView, -1, generateDefaultLayoutParams());
        super.addView(dimView, -1, generateDefaultLayoutParams());
    }

    /**
     * Convenience for showWithSheetView(sheetView, null, null)
     */
    public void showWithSheetView(View sheetView) {
        showWithSheetView(sheetView, null);
    }

    /**
     * Convenience for showWithSheetView(sheetView, viewTransformer, null)
     */
    public void showWithSheetView(View sheetView, ViewTransformer viewTransformer) {
        showWithSheetView(sheetView, viewTransformer, null);
    }

    /**
     * Present a sheet view to the user.
     *
     * @param sheetView The sheet to be presented.
     * @param viewTransformer The view transformer to use when presenting the sheet.
     * @param onSheetDismissedListener The listener to notify when the sheet is dismissed.
     */
    public void showWithSheetView(View sheetView, ViewTransformer viewTransformer, OnSheetDismissedListener onSheetDismissedListener) {
        if (state != State.HIDDEN) {
            throw new IllegalStateException("A sheet view is already presented, make sure to dismiss it before showing another.");
        }
        LayoutParams params = (LayoutParams) sheetView.getLayoutParams();
        if (params != null) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            params = generateDefaultLayoutParams();
        }
        super.addView(sheetView, -1, params);
        initializeSheetValues();
        this.viewTransformer = viewTransformer;
        this.onSheetDismissedListener = onSheetDismissedListener;

        // Don't start animating until the sheet has been drawn once. This ensures that we don't do layout while animating and that
        // the drawing cache for the view has been warmed up. tl;dr it reduces lag.
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                post(new Runnable() {
                    @Override
                    public void run() {
                        // Make sure sheet view is still here when first draw happens.
                        // In the case of a large lag it could be that the view is dismissed before it is drawn resulting in sheet view being null here.
                        if (getSheetView() != null) {
                            peekSheet();
                        }
                    }
                });
                return true;
            }
        });
    }

    /**
     * Dismiss the sheet currently being presented.
     */
    public void dismissSheet() {
        if (state == State.HIDDEN) {
            // no-op
            return;
        }
        cancelCurrentAnimation();
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, SHEET_TRANSLATION, 0);
        anim.setDuration(ANIMATION_DURATION);
        anim.setInterpolator(animationInterpolator);
        anim.addListener(new CancelDetectionAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled) {
                    currentAnimator = null;
                    setState(State.HIDDEN);
                    setSheetLayerTypeIfEnabled(LAYER_TYPE_NONE);
                    removeView(getSheetView());

                    if (onSheetDismissedListener != null) {
                        onSheetDismissedListener.onDismissed(BottomSheetLayout.this);
                    }

                    // Remove sheet specific properties
                    viewTransformer = null;
                    onSheetDismissedListener = null;
                }
            }
        });
        anim.start();
        currentAnimator = anim;
    }

    /**
     * @return The current state of the sheet.
     */
    public State getState() {
        return state;
    }

    /**
     * @return Whether or not a sheet is currently presented.
     */
    public boolean isSheetShowing() {
        return state != State.HIDDEN;
    }

    /**
     * Set the default view transformer to use for showing a sheet. Usually applications will use
     * a similar transformer for most use cases of bottom sheet so this is a convenience instead of
     * passing a new transformer each time a sheet is shown. This choice is overridden by any
     * view transformer passed to showWithSheetView().
     *
     * @param defaultViewTransformer The view transformer user by default.
     */
    public void setDefaultViewTransformer(ViewTransformer defaultViewTransformer) {
        this.defaultViewTransformer = defaultViewTransformer;
    }

    /**
     * Enable or disable dimming of the content view while a sheet is presented. If enabled a
     * transparent black dim is overlayed on top of the content view indicating that the sheet is the
     * foreground view. This dim is animated into place is coordination with the sheet view.
     * Defaults to true.
     *
     * @param shouldDimContentView whether or not to dim the content view.
     */
    public void setShouldDimContentView(boolean shouldDimContentView) {
        this.shouldDimContentView = shouldDimContentView;
    }

    /**
     * @return whether the content view is being dimmed while presenting a sheet or not.
     */
    public boolean shouldDimContentView() {
        return shouldDimContentView;
    }

    /**
     * Enable or disable the use of a hardware layer for the presented sheet while animating.
     * This settings defaults to true and should only be changed if you know that putting the
     * sheet in a layer will negatively effect performance. One such example is if the sheet contains
     * a view which needs to frequently be re-drawn.
     *
     * @param useHardwareLayerWhileAnimating whether or not to use a hardware layer.
     */
    public void setUseHardwareLayerWhileAnimating(boolean useHardwareLayerWhileAnimating) {
        this.useHardwareLayerWhileAnimating = useHardwareLayerWhileAnimating;
    }

    /**
     * Set a OnSheetStateChangeListener which will be notified when the state of the presented sheet changes.
     *
     * @param onSheetStateChangeListener the listener to be notified.
     */
    public void setOnSheetStateChangeListener(OnSheetStateChangeListener onSheetStateChangeListener) {
        this.onSheetStateChangeListener = onSheetStateChangeListener;
    }

}
