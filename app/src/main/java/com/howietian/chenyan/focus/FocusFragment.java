package com.howietian.chenyan.focus;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FocusFragment extends BaseFragment {


    public FocusFragment() {
        // Required empty public constructor
    }

    private static final String FOCUS_FRAGMENT = "focus_fragment";



    public static FocusFragment newInstance(String args){
        FocusFragment fragment = new FocusFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FOCUS_FRAGMENT,args);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_focus, container, false);
    }

}
