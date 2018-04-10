package com.howietian.chenyan.entities;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 83624 on 2018/3/19 0019.
 */

public class Splash extends BmobObject {
    private BmobFile image;

    public Splash() {
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }
}
