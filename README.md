#BottomSheet

[![Join the chat at https://gitter.im/Flipboard/bottomsheet](https://badges.gitter.im/Flipboard/bottomsheet.svg)](https://gitter.im/Flipboard/bottomsheet?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Build Status](https://travis-ci.org/Flipboard/bottomsheet.svg)](https://travis-ci.org/Flipboard/bottomsheet) [![Join the chat at https://gitter.im/Flipboard/bottomsheet](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Flipboard/bottomsheet?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

BottomSheet is an Android component which presents a dismissible view from the bottom of the screen. BottomSheet can be a useful replacement for dialogs and menus but can hold any view so the use cases are endless. This repository includes the BottomSheet component itself but also includes a set of common view components presented in a bottom sheet. These are located in the commons module.

BottomSheet has been used in production at Flipboard for a while now so it is thoroughly tested. Here is a GIF of it in action inside of Flipboard!

![FlipUI gif](http://i.imgur.com/2e3ZhoU.gif)

##Installation
If all you want is the BottomSheet component and don't need things from commons you can skip that dependency.
```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'com.flipboard:bottomsheet-core:1.5.0'
    compile 'com.flipboard:bottomsheet-commons:1.5.0' // optional
}
```

##Getting Started
Get started by wrapping your layout in a `BottomSheetLayout`. So if you currently have this:
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

</LinearLayout>
```

You would have to update it to look like this:
```xml
<com.flipboard.bottomsheet.BottomSheetLayout
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

	</LinearLayout>

</com.flipboard.bottomsheet.BottomSheetLayout>
```

Back in your activity or fragment you would get a reference to the BottomSheetLayout like any other view.
```java
BottomSheetLayout bottomSheet = (BottomSheetLayout) findViewById(R.id.bottomsheet);
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

That's it for the simplest of use cases. Check out the [API documentation](https://github.com/Flipboard/bottomsheet/wiki/API-Documentation) to find out how to customize BottomSheet to fit your use cases.

For more examples, also see the [Recipes](https://github.com/Flipboard/bottomsheet/wiki/Recipes) wiki.

##Common Components
These are located in the optional `bottomsheet-commons` dependency and implement common use cases for bottom sheet.

Intent Picker | Menu Sheet | ImagePicker Sheet
--- | --- | ---
![IntentPickerSheetView gif](http://i.imgur.com/wr9HJD1.gif) | ![MenuSheetView gif](http://i.imgur.com/f2j9Y5e.gif) | ![ImagePickerSheetView gif](https://camo.githubusercontent.com/23a9cf2bf9353a98d1b585e79d06639c7f5297c7/687474703a2f2f692e696d6775722e636f6d2f6f67764b4735692e676966)

####IntentPickerSheetView
This component presents an intent chooser in the form of a BottomSheet view. Give it an intent such as a share intent and let the user choose what activity they want to share the intent with in a BottomSheet.

Example from the sample app.
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
	public boolean include(IntentPickerSheetView.ActivityInfo info) {
		return !info.componentName.getPackageName().startsWith("com.android");
	}
});
// Sort activities in reverse order for no good reason
intentPickerSheet.setSortMethod(new Comparator<IntentPickerSheetView.ActivityInfo>() {
	@Override
	public int compare(IntentPickerSheetView.ActivityInfo lhs, IntentPickerSheetView.ActivityInfo rhs) {
		return rhs.label.compareTo(lhs.label);
	}
});
bottomSheet.showWithSheetView(intentPickerSheet);
```

####MenuSheetView
This component presents a BottomSheet view that's backed by a menu. It behaves similarly to the new `NavigationView` in the Design support library, and is intended to mimic the examples in the Material Design spec. It supports list and grid states, with the former adding further support for separators and subheaders.

Example from the sample app.
```java
MenuSheetView menuSheetView =
        new MenuSheetView(MenuActivity.this, MenuSheetView.MenuType.LIST, "Create...", new MenuSheetView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MenuActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                if (bottomSheetLayout.isSheetShowing()) {
                    bottomSheetLayout.dismissSheet();
                }
                return true;
            }
        });
menuSheetView.inflateMenu(R.menu.create);
bottomSheetLayout.showWithSheetView(menuSheetView);
```

##Contributing
We welcome pull requests for bug fixes, new features, and improvements to BottomSheet. Contributors to the main BottomSheet repository must accept Flipboard's Apache-style [Individual Contributor License Agreement (CLA)](https://docs.google.com/forms/d/1gh9y6_i8xFn6pA15PqFeye19VqasuI9-bGp_e0owy74/viewform) before any changes can be merged.
