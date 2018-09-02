package com.benjaminsommer.dailygoals.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.entities.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sommer on 11.06.2017.
 */

public class CategoryDialog extends DialogFragment {

    public static final String TAG = CategoryDialog.class.getSimpleName();

    private AdapterView.OnItemClickListener onItemClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_category_selection, container, false);

        // populate list
        ArrayList<Category> categoryList = new ArrayList<>();
        String[] categoryIDs = getResources().getStringArray(R.array.category_identifier);
        String[] categoryNames = getResources().getStringArray(R.array.category_name);
        TypedArray imgs = getResources().obtainTypedArray(R.array.category_icon);
        for (int i = 0; i < categoryNames.length; i++) {
            categoryList.add(new Category(Integer.valueOf(categoryIDs[i]), categoryNames[i], imgs.getDrawable(i)));
        }

        ListView listView = (ListView) rootView.findViewById(R.id.dialogCategorySeleciton_listView);
        CategoryArrayAdapter categoryArrayAdapter = new CategoryArrayAdapter(getActivity(), categoryList);
        listView.setAdapter(categoryArrayAdapter);
        listView.setOnItemClickListener(onItemClickListener);

        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onItemClickListener = (AdapterView.OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onItemClickListener");
        }
    }


    public class CategoryArrayAdapter extends ArrayAdapter<Category> {

        private final List<Category> list;
        private final Activity context;

        class ViewHolder {
            protected ImageView ivIcon;
            protected TextView tvName;
        }

        public CategoryArrayAdapter(Activity context, List<Category> list) {
            super(context, R.layout.dialog_goal_category, list);
            this.context = context;
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                view = inflater.inflate(R.layout.dialog_goal_category, null);
                final ViewHolder viewHolder = new CategoryArrayAdapter.ViewHolder();
                viewHolder.ivIcon = (ImageView) view.findViewById(R.id.dialogCategory_imageView_icon);
                viewHolder.tvName = (TextView) view.findViewById(R.id.dialogCategory_textView_name);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }

            CategoryArrayAdapter.ViewHolder holder = (CategoryArrayAdapter.ViewHolder) view.getTag();
            holder.ivIcon.setImageDrawable(list.get(position).getIcon());
            holder.tvName.setText(list.get(position).getName());

            return view;
        }
    }


}
