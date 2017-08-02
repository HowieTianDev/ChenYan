package com.howietian.chenyan.circle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class TypeActivity extends BaseActivity {
    @Bind(R.id.list_type)
    ListView listType;

    public static final String TYPE = "type";
    private String[] types = new String[]{"综合", "体育", "情感", "国学", "竞赛", "文艺", "动漫", "闲情"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_type);
    }

    @Override
    public void init() {
        super.init();
        listType.setAdapter(new ArrayAdapter<>(this, R.layout.item_list, R.id.tv_item, types));
        listType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra(TYPE, types[i]);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
