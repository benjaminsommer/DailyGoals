package com.benjaminsommer.dailygoals.ui.wishlist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benjaminsommer.dailygoals.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WishlistSearchFragment extends Fragment {


    public WishlistSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wishlist_search, container, false);

        SearchView searchView = (SearchView) view.findViewById(R.id.wishList_search_searchView);


        return view;

    }

}
