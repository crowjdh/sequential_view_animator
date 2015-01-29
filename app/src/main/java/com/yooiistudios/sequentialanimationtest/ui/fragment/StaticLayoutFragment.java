package com.yooiistudios.sequentialanimationtest.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yooiistudios.sequentialanimationtest.R;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.AnimateViewProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.SequentialAnimationProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.SequentialViewAnimator;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * StaticLayoutFragment
 */
public class StaticLayoutFragment extends BaseFragment {
    private static final String TAG = StaticLayoutFragment.class.getSimpleName();

    private View mTarget0;
    private View mTarget1;
    private View mTarget2;
    private View mTarget3;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_static, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();
        if (rootView != null) {
            mTarget0 = rootView.findViewById(R.id.target0);
            mTarget1 = rootView.findViewById(R.id.target1);
            mTarget2 = rootView.findViewById(R.id.target2);
            mTarget3 = rootView.findViewById(R.id.target3);
        }
    }

    @Override
    protected void animate() {
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
}
