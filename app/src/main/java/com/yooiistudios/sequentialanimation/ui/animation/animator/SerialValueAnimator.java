package com.yooiistudios.sequentialanimation.ui.animation.animator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yooiistudios.sequentialanimation.ui.animation.ViewTransientUtils;
import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yooiistudios.sequentialanimation.ui.animation.animator.SerialValueAnimator.ValueAnimatorProperty;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * SerialValueAnimator
 *  android.view.animation.Animation 객체를 사용하는 애니메이터
 */
public class SerialValueAnimator extends SerialAnimator<ValueAnimatorProperty,
        SerialValueAnimator.ValueTransitionListener> {
    private static final String TAG = SerialValueAnimator.class.getSimpleName();
    private Map<ViewProperty, ValueAnimator> mValueAnimators;
    // TODO 상위 클래스로 올려야 하는데...
    private boolean mIsAnimating;

    public SerialValueAnimator() {
        mValueAnimators = new HashMap<>();
    }

    @Override
    public void putViewPropertyIfRoom(ViewProperty requestedViewProperty, int idx) {
        super.putViewPropertyIfRoom(requestedViewProperty, idx);

        ViewProperty viewProperty = getViewProperties().get(idx);

        ValueAnimatorProperty transitionProperty = getTransitionProperty();
        long timePast = System.currentTimeMillis() - getStartTimeInMilli();
        if (transitionProperty.inTimeToTransit(viewProperty, timePast)) {
            viewProperty.getTransitionInfo().index =
                    transitionProperty.getTransitionIndexForProperty(viewProperty, timePast);
            viewProperty.getTransitionInfo().currentPlayTime =
                    transitionProperty.getCurrentPlayTime(viewProperty, timePast);

            transitAndRequestNext(viewProperty);
        } else if (transitionProperty.shouldTransitInFuture(viewProperty, timePast)){
            long consume = transitionProperty.getTotalDurationBefore(viewProperty);
            Log.i("shouldTransitInFuture", "view index : " + viewProperty.getViewIndex());
            Log.i("shouldTransitInFuture", "consume : " + consume);
            requestTransitionWithDelayConsume(viewProperty, consume);
        }
//        if (animating) {
//            // TODO 리펙터 해야 하는데 어떻게 해야 할까?
//        }
    }

    @Override
    protected void onTransit(ViewProperty property, ValueTransitionListener transitionListener) {
        List<ValueAnimator> valueAnimators = getTransitionProperty().getTransitions(property.getView());
        if (isLastTransition(property)) {
            for (ValueAnimator animator : valueAnimators) {
                animator.addListener(transitionListener);
            }
        }
        ValueAnimator valueAnimator = valueAnimators.get(property.getTransitionInfo().index);
        valueAnimator.start();
        valueAnimator.setCurrentPlayTime(property.getTransitionInfo().currentPlayTime);

        mValueAnimators.put(property, valueAnimator);
    }

    @Override
    protected void onAnimate() {
        mIsAnimating = true;
    }

    @Override
    protected boolean isReadyForTransition() {
        return super.isReadyForTransition();// && !isAnimating();
    }

    @Override
    public void cancelAllTransitions() {
        super.cancelAllTransitions();
        for (Map.Entry<ViewProperty, ValueAnimator> entry : mValueAnimators.entrySet()) {
            ViewProperty viewProperty = entry.getKey();
            ValueAnimator animator = entry.getValue();
            animator.cancel();

            List<ValueAnimator> valueAnimators = getTransitionProperty().getTransitions(viewProperty.getView());
            ValueAnimator valueAnimator = valueAnimators.get(0);
            valueAnimator.setCurrentPlayTime(0);
        }
    }

    @Override
    protected ValueTransitionListener makeTransitionListener(ViewProperty property) {
        ValueTransitionListener listener = new ValueTransitionListener(property);
        // FIXME 아래 라인 copy & paste 임. super 로 빼야 할듯
        listener.setIsLastTransition(isLastTransition(property));

        return listener;
    }

    protected static class ValueTransitionListener
            implements SerialAnimator.TransitionListener, ValueAnimator.AnimatorListener {
        private ViewProperty mViewProperty;
        private boolean mIsLastTransition;

        public ValueTransitionListener(ViewProperty viewProperty) {
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

        private void notifyOnAnimationEnd() {
            ViewProperty.AnimationListener callback =
                    getViewProperty().getAnimationListener();

            ViewTransientUtils.clearState(getViewProperty());

            if (callback != null) {
                callback.onAnimationEnd(getViewProperty());
            }
        }


        @Override
        public void onAnimationEnd(Animator animation) {
            if (isLastTransition()) {
                notifyOnAnimationEnd();

//                mAnimatorWeakReference.get().setAnimating(false);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            ViewTransientUtils.clearState(getViewProperty());
        }

        @Override public void onAnimationStart(Animator animation) { }
        @Override public void onAnimationRepeat(Animator animation) { }
    }

    public static class ValueAnimatorProperty extends SerialAnimator.TransitionProperty<ValueAnimator> {
        public ValueAnimatorProperty(@NonNull TransitionSupplier<ValueAnimator> transitionSupplier,
                                     long initialDelayInMillisec, long intervalInMillisec) {
            super(transitionSupplier, initialDelayInMillisec, intervalInMillisec);
        }

        @Override
        protected long getDuration(ValueAnimator transition) {
            return transition.getDuration();
        }

        protected long getCurrentPlayTime(ViewProperty property, long timePast) {
            return timePast - getDelayForInitialTransition(property) - getTotalDurationBefore(property);
//            return timePast - getDelayForInitialTransition(property) - getPreviousTransitionDuration(property);
//            return System.currentTimeMillis() - initialStartTime - getDelaySinceBase(property);
        }
    }
}
