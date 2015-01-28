package com.yooiistudios.sequentialanimationtest.sequentialanimation;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimationtest.AnimationListenerImpl;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Dongheyon Jeong in News-Android-L from Yooii Studios Co., LTD. on 15. 1. 27.
 *
 * SequentialViewAnimator
 *  순차적으로 뷰들을 animating 하는 클래스
 */
public class SequentialViewAnimator {
    private static final String TAG = SequentialViewAnimator.class.getSimpleName();
    private volatile static SequentialViewAnimator instance;

    private final SparseArray<AnimateViewProperty> mAnimateViewProperties;
    private SequentialAnimationProperty mSequentialAnimationProperty;
    private AnimationHandler mAnimationHandler;
    private volatile boolean mIsAnimating;
    private long mStartTimeInMilli;

    private SequentialViewAnimator() {
        mAnimateViewProperties = new SparseArray<>();
        mAnimationHandler = new AnimationHandler(this);
    }

    public static SequentialViewAnimator getInstance() {
        if (instance == null) {
            synchronized (SequentialViewAnimator.class) {
                if (instance == null) {
                    instance = new SequentialViewAnimator();
                }
            }
        }
        return instance;
    }

    public void putAnimateViewPropertyAt(AnimateViewProperty property, int idx) {
        mAnimateViewProperties.put(idx, property);
    }

    public void setSequentialAnimationProperty(SequentialAnimationProperty sequentialAnimationProperty) {
        mSequentialAnimationProperty = sequentialAnimationProperty;
    }

    public void animate() {
        if (isReadyForAnimation()) {
            prepareForNewAnimationSequence();
            runSequentialAnimation();
        }
    }

    private void runSequentialAnimation() {
        int viewCount = mAnimateViewProperties.size();
        for (int i = 0; i < viewCount; i++) {
            // TODO index ordering
            int propertyIndex = mAnimateViewProperties.keyAt(i);
            // TODO transient!! 화면 회전시 뷰 재생성 막자!
            //
            AnimateViewProperty property = mAnimateViewProperties.get(propertyIndex);
            requestAnimation(property);
        }
    }

    private void requestAnimation(AnimateViewProperty property) {
        long delay = mSequentialAnimationProperty.getDelay(property);
        Message message = Message.obtain();
        message.obj = property;

        mAnimationHandler.sendMessageDelayed(message, delay);
    }

    private void requestNextAnimation(AnimateViewProperty property) {
        if (!isLastAnimation(property)) {
            property.increaseAnimationIndex();

            requestAnimation(property);
        }
    }

    private void animate(AnimateViewProperty property) {
        Animation animation = getAnimationFromProperty(property);

        if (isLastAnimation(property)) {
            startLastAnimation(property, animation);
        } else {
            startAnimation(property, animation);
        }
        // TODO msg.obj에 property 변수가 계속 연결되어 있는지 생각해보자.
    }

    private static void startAnimation(AnimateViewProperty property, Animation animation) {
        View viewToAnimate = property.getView();
        viewToAnimate.startAnimation(animation);
    }

    private static void startLastAnimation(AnimateViewProperty property, Animation animation) {
        AnimateViewPropertyAnimationListener listener =
                new AnimateViewPropertyAnimationListener(property);
        animation.setAnimationListener(listener);
        startAnimation(property, animation);
    }

    private boolean isLastAnimation(AnimateViewProperty property) {
        List<Animation> animations = getSequentialAnimationProperty().getAnimations();
        return property.getAnimationIndex() == animations.size() - 1;
    }

    private Animation getAnimationFromProperty(AnimateViewProperty property) {
        List<Animation> animations = getSequentialAnimationProperty().getAnimations();
        return animations.get(property.getAnimationIndex());
    }

    private boolean isReadyForAnimation() {
        return !mIsAnimating && mAnimateViewProperties.size() > 0 && mSequentialAnimationProperty != null;
    }

    private void prepareForNewAnimationSequence() {
        mIsAnimating = true;
        mStartTimeInMilli = System.currentTimeMillis();
    }

    private SequentialAnimationProperty getSequentialAnimationProperty() {
        return mSequentialAnimationProperty;
    }

    private static class AnimationHandler extends Handler {
        private WeakReference<SequentialViewAnimator> mAnimatorWeakReference;

        public AnimationHandler(SequentialViewAnimator animator) {
            mAnimatorWeakReference = new WeakReference<>(animator);
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            validateMessage(message);

            final AnimateViewProperty property = getProperty(message);
            boolean animate = property.getView() != null;

            if (animate) {
                SequentialViewAnimator animator = mAnimatorWeakReference.get();

                animator.animate(property);
                animator.requestNextAnimation(property);
            }
        }

        private AnimateViewProperty getProperty(Message message) {
            return (AnimateViewProperty)message.obj;
        }

        private void validateMessage(Message message) {
            if (!(message.obj instanceof AnimateViewProperty)) {
                throw new IllegalStateException("msg.obj MUST BE an instance of "
                        + AnimateViewProperty.class.getSimpleName());
            }
        }
    }

    private static class AnimateViewPropertyAnimationListener extends AnimationListenerImpl {
        private AnimateViewProperty mAnimateViewProperty;

        public AnimateViewPropertyAnimationListener(AnimateViewProperty animateViewProperty) {
            mAnimateViewProperty = animateViewProperty;
        }

        public AnimateViewProperty getAnimateViewProperty() {
            return mAnimateViewProperty;
        }

        @Override
        public final void onAnimationEnd(Animation animation) {
            super.onAnimationEnd(animation);

            AnimateViewProperty.AnimationListener callback =
                    getAnimateViewProperty().getAnimationListener();

            if (callback != null) {
                callback.onAnimationEnd(getAnimateViewProperty());
            }
        }
    }
}
