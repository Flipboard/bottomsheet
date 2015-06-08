#BottomSheet
BottomSheet is an Android component which presents a dismissible view from the bottom of the screen. BottomSheet can be a useful replacement for dialogs and menus but can hold any view so the use cases are endless. This repository includes the BottomSheet component itself but also includes a set of common view components presented in a bottom sheet. These are located in the commons module.

BottomSheet has been used in production at Flipboard for a while now so it is thoroughly tested. Here is a GIF of it in action inside of Flipboard!

![FlipUI gif](http://i.imgur.com/2e3ZhoU.gif)

##Installation
If all you want is the BottomSheet component and don't need things from commons you can skip that dependency.
```groovy
dependencies {
    compile 'com.flipboard:bottomsheet-core:1.1.0'
    compile 'com.flipboard:bottomsheet-commons:1.1.0' // optional
}
```

##Getting Started
Get started by wrapping your layout in a BottomSheet. So if you currently have this:
```xml
<LinearLayout
	android:id="@+id/root"
	android:orientation="vertical"
	android:layout_width="match_parent"
	android:layout_height="match_parent">

	<View
		android:id="@+id/view1"
		android:layout_width="match_parent"
		android:layout_height="match_parent"/>

	<View
		android:id="@+id/view2"
		android:layout_width="match_parent"
		android:layout_height="match_parent"/>

</LinearLayout>
```

You would have to update it to look like this:
```xml
<com.flipboard.bottomsheet.BottomSheet
	android:id="@+id/bottomsheet"
	android:layout_width="match_parent"
	android:layout_height="match_parent">

	<LinearLayout
		android:id="@+id/root"
		android:orientation="vertical"
		android:layout_width="match_parent"
		android:layout_height="match_parent">

		<View
			android:id="@+id/view1"
			android:layout_width="match_parent"
			android:layout_height="match_parent"/>

		<View
			android:id="@+id/view2"
			android:layout_width="match_parent"
			android:layout_height="match_parent"/>

	</LinearLayout>

</com.flipboard.bottomsheet.BottomSheet>
```

Back in your activity or fragment you would get a reference to the BottomSheet like any other view.
```java
BottomSheet bottomSheet = (BottomSheet) findViewById(R.id.bottomsheet);
```

Now all you need to do is show a view in the bottomSheet:
```java
bottomSheet.showWithSheetView(LayoutInflater.from(context).inflate(R.layout.my_sheet_layout, bottomSheet, false));
```

You could also use one of the sheet views from the commons module.
```java
bottomSheet.showWithSheetView(new IntentPickerSheetView(this, shareIntent, "Share with...", new IntentPickerSheetView.OnIntentPickedListener() {
	@Override
	public void onIntentPicked(Intent intent) {
		bottomSheet.dismissSheet();
		startActivity(intent);
	}
});
```

That's it for the simplest of use cases. Check out the API documentation below to find out how to customize BottomSheet to fit your use cases.

##API
###BottomSheet
 ```java
/**
 * Set the presented sheet to be in an expanded state.
 */
public void expandSheet();

/**
 * Set the presented sheet to be in a peeked state.
 */
public void peekSheet();

/**
 * @return The peeked state translation for the presented sheet view. Translation is counted from the bottom of the view.
 */
public float getPeekSheetTranslation();

/**
 * @return The maximum translation for the presented sheet view. Translation is counted from the bottom of the view.
 */
public float getMaxSheetTranslation();

/**
 * @return The currently presented sheet view. If no sheet is currently presented null will returned.
 */
public View getContentView();

/**
 * @return The currently presented sheet view. If no sheet is currently presented null will returned.
 */
public View getSheetView();

/**
 * Set the content view of the bottom sheet. This is the view which is shown under the sheet
 * being presented. This is usually the root view of your application.
 *
 * @param contentView The content view of your application.
 */
public void setContentView(View contentView);

/**
 * Convenience for showWithSheetView(sheetView, null, null)
 */
public void showWithSheetView(View sheetView);

/**
 * Convenience for showWithSheetView(sheetView, viewTransformer, null)
 */
public void showWithSheetView(View sheetView, ViewTransformer viewTransformer);

/**
 * Present a sheet view to the user.
 *
 * @param sheetView The sheet to be presented.
 * @param viewTransformer The view transformer to use when presenting the sheet.
 * @param onSheetDismissedListener The listener to notify when the sheet is dismissed.
 */
public void showWithSheetView(View sheetView, ViewTransformer viewTransformer, OnSheetDismissedListener onSheetDismissedListener);

/**
 * Dismiss the sheet currently being presented.
 */
public void dismissSheet();

/**
 * @return The current state of the sheet.
 */
public State getState();

/**
 * @return Whether or not a sheet is currently presented.
 */
public boolean isSheetShowing();

/**
 * Set the default view transformer to use for showing a sheet. Usually applications will use
 * a similar transformer for most use cases of bottom sheet so this is a convenience instead of
 * passing a new transformer each time a sheet is shown. This choice is overridden by any
 * view transformer passed to showWithSheetView().
 *
 * @param defaultViewTransformer The view transformer user by default.
 */
public void setDefaultViewTransformer(ViewTransformer defaultViewTransformer);

/**
 * Enable or disable dimming of the content view while a sheet is presented. If enabled a
 * transparent black dim is overlaid on top of the content view indicating that the sheet is the
 * foreground view. This dim is animated into place is coordination with the sheet view.
 * Defaults to true.
 *
 * @param shouldDimContentView whether or not to dim the content view.
 */
public void setShouldDimContentView(boolean shouldDimContentView);

/**
 * @return whether the content view is being dimmed while presenting a sheet or not.
 */
public boolean shouldDimContentView();

/**
 * Enable or disable the use of a hardware layer for the presented sheet while animating.
 * This settings defaults to true and should only be changed if you know that putting the
 * sheet in a layer will negatively effect performance. One such example is if the sheet contains
 * a view which needs to frequently be re-drawn.
 *
 * @param useHardwareLayerWhileAnimating whether or not to use a hardware layer.
 */
public void setUseHardwareLayerWhileAnimating(boolean useHardwareLayerWhileAnimating);

/**
 * Set a OnSheetStateChangeListener which will be notified when the state of the presented sheet changes.
 *
 * @param onSheetStateChangeListener the listener to be notified.
 */
public void setOnSheetStateChangeListener(OnSheetStateChangeListener onSheetStateChangeListener);
```

###OnSheetDismissedListener
```java
/**
 * Called when the presented sheet has been dismissed.
 *
 * @param bottomSheet The bottom sheet which contained the presented sheet.
 */
void onDismissed(BottomSheet bottomSheet);
```

###ViewTransformer
```java
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
void transformView(float translation, float maxTranslation, float peekedTranslation, BottomSheet parent, View view);
```

##Common Components
These are located in the optional `bottomsheet-commons` dependency and implement common use cases for bottom sheet.

###IntentPickerSheetView
This component presents an intent chooser in the form of a BottomSheet view. Give it an intent such as a share intent and let the user choose what activity they want to share the intent with in a BottomSheet. Here is a GIF of it in action!

![IntentPickerSheetView gif](http://i.imgur.com/ld56kbi.gif)

Here is a sample use case of this component taken from the sample application.
```java
IntentPickerSheetView intentPickerSheet = new IntentPickerSheetView(MainActivity.this, shareIntent, "Share with...", new IntentPickerSheetView.OnIntentPickedListener() {
	@Override
	public void onIntentPicked(Intent intent) {
		bottomSheet.dismissSheet();
		startActivity(intent);
	}
});
// Filter out built in sharing options such as bluetooth and beam.
intentPickerSheet.setFilter(new IntentPickerSheetView.Filter() {
	@Override
	public boolean include(IntentPickerSheetView.ActvityInfo info) {
		return !info.componentName.getPackageName().startsWith("com.android");
	}
});
// Sort activities in reverse order for no good reason
intentPickerSheet.setSortMethod(new Comparator<IntentPickerSheetView.ActvityInfo>() {
	@Override
	public int compare(IntentPickerSheetView.ActvityInfo lhs, IntentPickerSheetView.ActvityInfo rhs) {
		return rhs.label.compareTo(lhs.label);
	}
});
bottomSheet.showWithSheetView(intentPickerSheet);
```

##Contributing
We welcome pull requests for bug fixes, new features, and improvements to BottomSheet. Contributors to the main BottomSheet repository must accept Flipboard's Apache-style [Individual Contributor License Agreement (CLA)](https://docs.google.com/forms/d/1gh9y6_i8xFn6pA15PqFeye19VqasuI9-bGp_e0owy74/viewform) before any changes can be merged.
