package com.howietian.chenyan.personpage;


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
public class PersonDynamicFragment extends BaseFragment {


    public PersonDynamicFragment() {
        // Required empty public constructor
    }

    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_dynamic, container, false);
    }

}
