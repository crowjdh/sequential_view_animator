package com.yooiistudios.sequentialanimation.ui;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

import com.yooiistudios.sequentialanimation.R;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * AnimationFactory
 */
public class AnimationFactory {
    private static final long ANIM_DURATION_MILLISEC = 500;
    private static final Interpolator INTERPOLATOR =
            new CubicBezierInterpolator(.57f, .15f, .65f, .67f);
    private static final int INTERPOLATOR_RES_ID = R.animator.interpolator_bottom_fade;

    public static Animation makeBottomFadeOutAnimation(Context context) {
        Animation fadeOutAnim = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnim.setDuration(ANIM_DURATION_MILLISEC);
        fadeOutAnim.setFillEnabled(true);
        fadeOutAnim.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fadeOutAnim.setInterpolator(context, INTERPOLATOR_RES_ID);
        } else {
            fadeOutAnim.setInterpolator(INTERPOLATOR);
        }
        return fadeOutAnim;
    }

    public static Animation makeBottomFadeInAnimation(Context context) {
        Animation fadeOutAnim = new AlphaAnimation(0.0f, 1.0f);
        fadeOutAnim.setDuration(ANIM_DURATION_MILLISEC);
        fadeOutAnim.setFillEnabled(true);
        fadeOutAnim.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fadeOutAnim.setInterpolator(context, INTERPOLATOR_RES_ID);
        } else {
            fadeOutAnim.setInterpolator(INTERPOLATOR);
        }

        return fadeOutAnim;
    }

    public static ValueAnimator makeFadeInAnimator() {
        PropertyValuesHolder holder = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
        Interpolator interpolator = INTERPOLATOR;
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holder);
        animator.setDuration(300);
        animator.setInterpolator(interpolator);

        return animator;
    }

    public static ValueAnimator makeFadeOutAnimator() {
        PropertyValuesHolder holder = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f);
        Interpolator interpolator = INTERPOLATOR;
        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(holder);
        animator.setDuration(300);
        animator.setInterpolator(interpolator);

        return animator;
    }

    public static ValueAnimator getTestAnimator() {
        PropertyValuesHolder holder = PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f);
        Interpolator interpolator = new CubicBezierInterpolator(.57f, .15f, .65f, .67f);
        ValueAnimator animator = ObjectAnimator.ofPropertyValuesHolder(holder);
        animator.setDuration(1000);
        animator.setInterpolator(interpolator);

        return animator;
    }
}
