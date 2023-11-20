package com.example.efficientjanitor.animation;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;

public interface PushDown {
    PushDown setScale(float scale);
    PushDown setScale(@PushDownAnim.Mode int mode, float scale);
    PushDown setDurationPush(long duration);
    PushDown setDurationRelease(long duration);
    PushDown setInterpolatorPush(AccelerateDecelerateInterpolator interpolatorPush);
    PushDown setInterpolatorRelease(AccelerateDecelerateInterpolator interpolatorRelease);
    PushDown setOnClickListener(View.OnClickListener clickListener);
    PushDown setOnLongClickListener(OnLongClickListener clickListener);
    PushDown setOnTouchEvent(OnTouchListener eventListener);
}

