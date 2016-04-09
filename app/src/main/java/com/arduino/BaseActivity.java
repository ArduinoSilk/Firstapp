package com.arduino;

import android.app.Activity;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by ES29 on 3/6/2016.
 */
public abstract class BaseActivity extends Activity {

    protected ImageLoader imageLoader =ImageLoader.getInstance();
}
