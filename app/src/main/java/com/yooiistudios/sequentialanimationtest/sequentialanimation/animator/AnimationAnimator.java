package com.yooiistudios.sequentialanimationtest.sequentialanimation.animator;

import android.view.View;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimationtest.AnimationListenerImpl;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.ViewProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.ViewUtils;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animationproperty.AnimationProperty;

import java.util.List;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * AnimationAnimator
 *  android.view.animation.Animation 객체를 사용하는 애니메이터
 */
public class AnimationAnimator extends SequentialViewAnimator<AnimationProperty> {

    @Override
    protected void transit(ViewProperty property) {
        List<Animation> animations = getTransitionProperty().getTransitions();
        Animation animation = animations.get(property.getTransitionIndex());

        startAnimation(property, animation);
    }

    @Override
    protected void beforeRequestTransition(ViewProperty property) {
        ViewUtils.setViewHasTransientState(property);
    }

    @Override
    public void cancelAllTransitions() {
        super.cancelAllTransitions();
        for (int i = 0; i < getViewProperties().size(); i++) {
            int propertyIndex = getViewProperties().keyAt(i);
            ViewProperty property = getViewProperties().get(propertyIndex);

            property.getView().clearAnimation();
        }
    }

    private void startAnimation(ViewProperty property, Animation animation) {
        animation.setAnimationListener(makeTransitionListener(property));
        View viewToAnimate = property.getView();
        viewToAnimate.startAnimation(animation);
    }


    private SequentialTransitionListener makeTransitionListener(ViewProperty property) {
        SequentialTransitionListener listener = new SequentialTransitionListener(property);
        listener.setIsLastTransition(isLastTransition(property));

        return listener;
    }

    // TODO ValueAnimator 도 구현해야 하니 인터페이스로 묶어야 할듯..
    private static class SequentialTransitionListener extends AnimationListenerImpl {
        private ViewProperty mViewProperty;
        private boolean mIsLastTransition;

        public SequentialTransitionListener(ViewProperty viewProperty) {
            mViewProperty = viewProperty;
        }

        public ViewProperty getViewProperty() {
            return mViewProperty;
        }

        public boolean isLastTransition() {
            return mIsLastTransition;
        }

        public void setIsLastTransition(boolean isLastTransition) {
            mIsLastTransition = isLastTransition;
        }

        @Override
        public final void onAnimationEnd(Animation animation) {
            super.onAnimationEnd(animation);

            if (isLastTransition()) {
                notifyOnAnimationEnd();
            }
        }

        private void notifyOnAnimationEnd() {
            ViewProperty.AnimationListener callback =
                    getViewProperty().getAnimationListener();

            ViewUtils.setViewNotHaveTransientState(getViewProperty());

            if (callback != null) {
                callback.onAnimationEnd(getViewProperty());
            }
        }
    }

}
