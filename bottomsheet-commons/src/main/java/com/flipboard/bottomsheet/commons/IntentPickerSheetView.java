package com.flipboard.bottomsheet.commons;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import flipboard.bottomsheet.commons.R;

@SuppressLint("ViewConstructor")
public class IntentPickerSheetView extends FrameLayout {

    public interface Filter {
        boolean include(ActivityInfo info);
    }

    public interface OnIntentPickedListener {
        void onIntentPicked(ActivityInfo activityInfo);
    }

    private class SortAlphabetically implements Comparator<ActivityInfo> {
        @Override
        public int compare(ActivityInfo lhs, ActivityInfo rhs) {
            return lhs.label.compareTo(rhs.label);
        }
    }

    private class FilterNone implements Filter {
        @Override
        public boolean include(ActivityInfo info) {
            return true;
        }
    }

    /**
     * Represents an item in the picker grid
     */
    public static class ActivityInfo {
        public final Drawable icon;
        public final String label;
        public final ComponentName componentName;
        public Object tag;

        public ActivityInfo(Drawable icon, String label, Class<?> clazz) {
            this.icon = icon;
            this.label = label;
            this.componentName = new ComponentName(clazz.getPackage().getName(), clazz.getName());
        }

        ActivityInfo(Drawable icon, CharSequence label, ComponentName componentName) {
            this.icon = icon;
            this.label = label.toString();
            this.componentName = componentName;
        }

        public Intent getConcreteIntent(Intent intent) {
            Intent concreteIntent = new Intent(intent);
            concreteIntent.setComponent(componentName);
            return concreteIntent;
        }
    }

    private final Intent intent;
    private final GridView appGrid;
    private final List<ActivityInfo> mixins = new ArrayList<>();

    private Adapter adapter;
    private Filter filter = new FilterNone();
    private Comparator<ActivityInfo> sortMethod = new SortAlphabetically();

    public IntentPickerSheetView(Context context, Intent intent, @StringRes int titleRes, OnIntentPickedListener listener) {
        this(context, intent, context.getString(titleRes), listener);
    }

    public IntentPickerSheetView(Context context, final Intent intent, final String title, final OnIntentPickedListener listener) {
        super(context);
        this.intent = intent;

        inflate(context, R.layout.grid_sheet_view, this);
        appGrid = (GridView) findViewById(R.id.grid);
        TextView titleView = (TextView) findViewById(R.id.title);

        titleView.setText(title);
        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onIntentPicked(adapter.getItem(position));
            }
        });

        ViewCompat.setElevation(this, Util.dp2px(getContext(), 16f));
    }

    public void setSortMethod(Comparator<ActivityInfo> sortMethod) {
        this.sortMethod = sortMethod;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * Adds custom mixins to the resulting picker sheet
     *
     * @param infos Custom ActivityInfo classes to mix in
     */
    public void setMixins(@NonNull List<ActivityInfo> infos) {
        mixins.clear();
        mixins.addAll(infos);
    }

    public List<ActivityInfo> getMixins() {
        return this.mixins;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.adapter = new Adapter(getContext(), intent, mixins);
        appGrid.setAdapter(this.adapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        final float density = getResources().getDisplayMetrics().density;
        appGrid.setNumColumns((int) (width / (100 * density)));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Necessary for showing elevation on 5.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new Util.ShadowOutline(w, h));
        }
    }

    private class Adapter extends BaseAdapter {

        final List<ActivityInfo> activityInfos;
        final LayoutInflater inflater;

        public Adapter(Context context, Intent intent, List<ActivityInfo> mixins) {
            inflater = LayoutInflater.from(context);
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
            activityInfos = new ArrayList<>(infos.size() + mixins.size());
            activityInfos.addAll(mixins);
            for (ResolveInfo info : infos) {
                ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                ActivityInfo activityInfo = new ActivityInfo(info.loadIcon(pm), info.loadLabel(pm), componentName);
                if (filter.include(activityInfo)) {
                    activityInfos.add(activityInfo);
                }
            }
            Collections.sort(activityInfos, sortMethod);
        }

        @Override
        public int getCount() {
            return activityInfos.size();
        }

        @Override
        public ActivityInfo getItem(int position) {
            return activityInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return activityInfos.get(position).componentName.hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.sheet_grid_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ActivityInfo info = activityInfos.get(position);
            holder.icon.setImageDrawable(info.icon);
            holder.label.setText(info.label);

            return convertView;
        }

        class ViewHolder {
            final ImageView icon;
            final TextView label;

            ViewHolder(View root) {
                icon = (ImageView) root.findViewById(R.id.icon);
                label = (TextView) root.findViewById(R.id.label);
            }
        }

    }

}