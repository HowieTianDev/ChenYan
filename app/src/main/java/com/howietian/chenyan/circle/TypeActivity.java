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
    private String[] types = new String[]{"学校：你的学校发生了什么...", "挑战：打卡活动", "情感：恋爱、情侣、暗恋、喜欢...", "体育：足球、篮球、跑步、健身...", "娱乐：音乐、影视、游戏、动漫...", "文艺：阅读、摄影...", "生活：萌宠、美食...", "比赛：竞赛、辩论、商赛...","学习：语文、数学、外语..."};

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
                intent.putExtra(TYPE, types[i].split("：")[0]);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
