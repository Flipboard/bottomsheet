package com.flipboard.bottomsheet.commons;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
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

public class IntentPickerSheetView extends FrameLayout {

    public interface Filter {
        boolean include(ActvityInfo info);
    }

    public interface OnIntentPickedListener {
        void onIntentPicked(Intent intent);
    }

    private class SortAlphabetically implements Comparator<ActvityInfo> {
        @Override
        public int compare(ActvityInfo lhs, ActvityInfo rhs) {
            return lhs.label.compareTo(rhs.label);
        }
    }

    private class FilterNone implements Filter {
        @Override
        public boolean include(ActvityInfo info) {
            return true;
        }
    }

    public class ActvityInfo {
        public final Drawable icon;
        public final String label;
        public final ComponentName componentName;

        ActvityInfo(Drawable icon, CharSequence label, ComponentName componentName) {
            this.icon = icon;
            this.label = label.toString();
            this.componentName = componentName;
        }
    }

    private final Intent intent;
    private final GridView appGrid;

    private Adapter adapter;
    private Filter filter = new FilterNone();
    private Comparator<ActvityInfo> sortMethod = new SortAlphabetically();

    public IntentPickerSheetView(Context context, Intent intent, @StringRes int titleRes, OnIntentPickedListener listener) {
        this(context, intent, context.getString(titleRes), listener);
    }

    public IntentPickerSheetView(Context context, final Intent intent, final String title, final OnIntentPickedListener listener) {
        super(context);
        this.intent = intent;

        inflate(context, R.layout.intent_picker_sheet_view, this);
        appGrid = (GridView) findViewById(R.id.grid);
        TextView titleView = (TextView) findViewById(R.id.title);

        titleView.setText(title);
        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent concreteIntent = new Intent(intent);
                concreteIntent.setComponent(adapter.getItem(position).componentName);
                listener.onIntentPicked(concreteIntent);
            }
        });
    }

    public void setSortMethod(Comparator<ActvityInfo> sortMethod) {
        this.sortMethod = sortMethod;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.adapter = new Adapter(getContext(), intent);
        appGrid.setAdapter(this.adapter);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        float density = getResources().getDisplayMetrics().density;
        appGrid.setNumColumns((int) (getWidth() / (100 * density)));
    }

    private class Adapter extends BaseAdapter {

        final List<ActvityInfo> actvityInfos;
        final LayoutInflater inflater;

        public Adapter(Context context, Intent intent) {
            inflater = LayoutInflater.from(context);
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
            actvityInfos = new ArrayList<>(infos.size());
            for (ResolveInfo info : infos) {
                ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                ActvityInfo actvityInfo = new ActvityInfo(info.loadIcon(pm), info.loadLabel(pm), componentName);
                if (filter.include(actvityInfo)) {
                    actvityInfos.add(actvityInfo);
                }
            }
            Collections.sort(actvityInfos, sortMethod);
        }

        @Override
        public int getCount() {
            return actvityInfos.size();
        }

        @Override
        public ActvityInfo getItem(int position) {
            return actvityInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return actvityInfos.get(position).label.hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.intent_picker_grid_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ActvityInfo info = actvityInfos.get(position);
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