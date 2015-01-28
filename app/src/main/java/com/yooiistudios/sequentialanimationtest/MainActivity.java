package com.yooiistudios.sequentialanimationtest;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimationtest.sequentialanimation.AnimateViewProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.SequentialAnimationProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.SequentialViewAnimator;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity
        implements AnimateViewProperty.AnimationListener, SequentialAnimationProperty.AnimationSupplier {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long ANIM_DURATION_MILLISEC = 500;

    private View mTarget0;
    private View mTarget1;
    private View mTarget2;
    private View mTarget3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTarget0 = findViewById(R.id.target0);
        mTarget1 = findViewById(R.id.target1);
        mTarget2 = findViewById(R.id.target2);
        mTarget3 = findViewById(R.id.target3);
    }

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

    private void animate() {
        AnimateViewProperty property0 =
                new AnimateViewProperty.Builder()
                        .setView(mTarget0)
                        .setViewIndex(0)
                        .setAnimationListener(this)
                        .build();
        AnimateViewProperty property1 =
                new AnimateViewProperty.Builder()
                        .setView(mTarget1)
                        .setViewIndex(1)
                        .setAnimationListener(this)
                        .build();
        AnimateViewProperty property2 =
                new AnimateViewProperty.Builder()
                        .setView(mTarget2)
                        .setViewIndex(2)
                        .setAnimationListener(this)
                        .build();
        AnimateViewProperty property3 =
                new AnimateViewProperty.Builder()
                        .setView(mTarget3)
                        .setViewIndex(3)
                        .setAnimationListener(this)
                        .build();

        SequentialViewAnimator animatorInstance = SequentialViewAnimator.getInstance();
        animatorInstance.putAnimateViewPropertyAt(property0, 0);
        animatorInstance.putAnimateViewPropertyAt(property1, 1);
        animatorInstance.putAnimateViewPropertyAt(property2, 2);
        animatorInstance.putAnimateViewPropertyAt(property3, 3);

        SequentialAnimationProperty sequentialAnimationProperty =
                new SequentialAnimationProperty(this, 0, 1000);

        animatorInstance.setSequentialAnimationProperty(sequentialAnimationProperty);

        animatorInstance.animate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                animate();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnimationEnd(AnimateViewProperty property) {
        Log.i(TAG, "Animation end. Index : " + property.getViewIndex());
    }

    @NonNull
    @Override
    public List<Animation> onSupplyAnimationList() {
        Context context = getApplicationContext();
        Animation fadeOutAnim = makeBottomFadeOutAnimation(context);
        Animation fadeInAnim = makeBottomFadeInAnimation(context);

        ArrayList<Animation> animList = new ArrayList<>();
        animList.add(fadeOutAnim);
        animList.add(fadeInAnim);

        return animList;
    }
}
