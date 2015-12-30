package com.flipboard.bottomsheet.commons;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

    private int columnWidthDp = 100;

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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (ActivityInfo activityInfo : adapter.activityInfos) {
            if (activityInfo.iconLoadTask != null) {
                activityInfo.iconLoadTask.cancel(true);
                activityInfo.iconLoadTask = null;
            }
        }
    }

    /**
     * Represents an item in the picker grid
     */
    public static class ActivityInfo {
        public Drawable icon;
        public final String label;
        public final ComponentName componentName;
        public final ResolveInfo resolveInfo;
        private AsyncTask<Void, Void, Drawable> iconLoadTask;
        public Object tag;

        public ActivityInfo(Drawable icon, String label, Context context, Class<?> clazz) {
            this.icon = icon;
            resolveInfo = null;
            this.label = label;
            this.componentName = new ComponentName(context, clazz.getName());
        }

        ActivityInfo(ResolveInfo resolveInfo, CharSequence label, ComponentName componentName) {
            this.resolveInfo = resolveInfo;
            this.label = label.toString();
            this.componentName = componentName;
        }

        public Intent getConcreteIntent(Intent intent) {
            Intent concreteIntent = new Intent(intent);
            concreteIntent.setComponent(componentName);
            return concreteIntent;
        }
    }

    protected final Intent intent;
    protected final GridView appGrid;
    protected final TextView titleView;
    protected final List<ActivityInfo> mixins = new ArrayList<>();

    protected Adapter adapter;
    protected Filter filter = new FilterNone();
    protected Comparator<ActivityInfo> sortMethod = new SortAlphabetically();

    public IntentPickerSheetView(Context context, Intent intent, @StringRes int titleRes, OnIntentPickedListener listener) {
        this(context, intent, context.getString(titleRes), listener);
    }

    public IntentPickerSheetView(Context context, final Intent intent, final String title, final OnIntentPickedListener listener) {
        super(context);
        this.intent = intent;

        inflate(context, R.layout.grid_sheet_view, this);
        appGrid = (GridView) findViewById(R.id.grid);
        titleView = (TextView) findViewById(R.id.title);

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
    
    public void setColumnWidthDp(int columnWidthDp) {
        this.columnWidthDp = columnWidthDp;
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
        getResources().getDimensionPixelSize(R.dimen.bottomsheet_default_sheet_width);
        appGrid.setNumColumns((int) (width / (columnWidthDp * density)));
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
        private PackageManager packageManager;

        public Adapter(Context context, Intent intent, List<ActivityInfo> mixins) {
            inflater = LayoutInflater.from(context);
            packageManager = context.getPackageManager();
            List<ResolveInfo> infos = packageManager.queryIntentActivities(intent, 0);
            activityInfos = new ArrayList<>(infos.size() + mixins.size());
            activityInfos.addAll(mixins);
            for (ResolveInfo info : infos) {
                ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                ActivityInfo activityInfo = new ActivityInfo(info, info.loadLabel(packageManager), componentName);
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
            final ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.sheet_grid_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ActivityInfo info = activityInfos.get(position);
            if (info.iconLoadTask != null) {
                info.iconLoadTask.cancel(true);
                info .iconLoadTask = null;
            }
            if (info.icon != null) {
                holder.icon.setImageDrawable(info.icon);
            } else {
                holder.icon.setImageDrawable(getResources().getDrawable(R.color.divider_gray));
                info.iconLoadTask = new AsyncTask<Void, Void, Drawable>() {
                    @Override
                    protected Drawable doInBackground(@NonNull Void... params) {
                        return info.resolveInfo.loadIcon(packageManager);
                    }

                    @Override
                    protected void onPostExecute(@NonNull Drawable drawable) {
                        info.icon = drawable;
                        info.iconLoadTask = null;
                        holder.icon.setImageDrawable(drawable);
                    }
                };
                info.iconLoadTask.execute();
            }
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
