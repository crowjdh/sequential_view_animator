package com.yooiistudios.sequentialanimationtest.ui;

import android.content.Context;
import android.os.Build;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimationtest.CubicBezierInterpolator;
import com.yooiistudios.sequentialanimationtest.R;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * AnimationFactory
 */
public class AnimationFactory {
    private static final long ANIM_DURATION_MILLISEC = 500;

    public static Animation makeBottomFadeOutAnimation(Context context) {
        Animation fadeOutAnim = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnim.setDuration(ANIM_DURATION_MILLISEC);
        fadeOutAnim.setFillEnabled(true);
        fadeOutAnim.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fadeOutAnim.setInterpolator(context, R.animator.interpolator_bottom_fade);
        } else {
            fadeOutAnim.setInterpolator(new CubicBezierInterpolator(.57f, .15f, .65f, .67f));
        }
        return fadeOutAnim;
    }

    public static Animation makeBottomFadeInAnimation(Context context) {
        Animation fadeOutAnim = new AlphaAnimation(0.0f, 1.0f);
        fadeOutAnim.setDuration(ANIM_DURATION_MILLISEC);
        fadeOutAnim.setFillEnabled(true);
        fadeOutAnim.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fadeOutAnim.setInterpolator(context, R.animator.interpolator_bottom_fade);
        } else {
            fadeOutAnim.setInterpolator(new CubicBezierInterpolator(.57f, .15f, .65f, .67f));
        }

        return fadeOutAnim;
    }
}
