package com.howietian.chenyan.me.club;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ApplySuccessActivity extends BaseActivity {


    @Bind(R.id.tb_apply_success)
    Toolbar tbApplySuccess;
    @Bind(R.id.tv_club_id)
    TextView tvClubId;

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_apply_success);
    }

    @Override
    public void init() {
        super.init();
        if (getIntent() != null) {
            String clubId = getIntent().getStringExtra(ApplyClubActivity.FROM_APPLY_CLUB);
            tvClubId.setText(clubId);
        }

        setSupportActionBar(tbApplySuccess);
        tbApplySuccess.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
