package com.yooiistudios.sequentialanimationtest.sequentialanimation;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
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
            cancelAllAnimations();
            prepareForNewAnimationSequence();
            runSequentialAnimation();
        }
    }

    // TODO refactor
    public void cancelAllAnimations() {
        mAnimationHandler.removeCallbacksAndMessages(null);
        for (int i = 0; i < mAnimateViewProperties.size(); i++) {
            int propertyIndex = mAnimateViewProperties.keyAt(i);
            AnimateViewProperty property = mAnimateViewProperties.get(propertyIndex);

            property.getView().clearAnimation();
        }
    }

    private void runSequentialAnimation() {
        int viewCount = mAnimateViewProperties.size();
        for (int i = 0; i < viewCount; i++) {
            // SparseArray 의 keyAt 메서드 특성상 아래와 같이 쿼리하면 key 의 ascending order 로 결과값이 나온다.
            int propertyIndex = mAnimateViewProperties.keyAt(i);
            AnimateViewProperty property = mAnimateViewProperties.get(propertyIndex);
            setViewHasTransientState(property);
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

        startAnimation(property, animation);
    }

    private SequentialAnimationListener makeAnimationListener(AnimateViewProperty property) {
        SequentialAnimationListener listener = new SequentialAnimationListener(property);
        listener.setAnimatingLastAnimation(isLastAnimation(property));

        return listener;
    }

    private void startAnimation(AnimateViewProperty property, Animation animation) {
        animation.setAnimationListener(makeAnimationListener(property));
        View viewToAnimate = property.getView();
        viewToAnimate.startAnimation(animation);
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
//        mIsAnimating = true;
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

    private static class SequentialAnimationListener extends AnimationListenerImpl {
        private AnimateViewProperty mAnimateViewProperty;
        private boolean mIsAnimatingLastAnimation;

        public SequentialAnimationListener(AnimateViewProperty animateViewProperty) {
            mAnimateViewProperty = animateViewProperty;
        }

        public AnimateViewProperty getAnimateViewProperty() {
            return mAnimateViewProperty;
        }

        public boolean isAnimatingLastAnimation() {
            return mIsAnimatingLastAnimation;
        }

        public void setAnimatingLastAnimation(boolean isAnimatingLastAnimation) {
            mIsAnimatingLastAnimation = isAnimatingLastAnimation;
        }

        @Override
        public final void onAnimationEnd(Animation animation) {
            super.onAnimationEnd(animation);

            if (isAnimatingLastAnimation()) {
                notifyOnAnimationEnd();
            }
        }

        private void notifyOnAnimationEnd() {
            AnimateViewProperty.AnimationListener callback =
                    getAnimateViewProperty().getAnimationListener();

            setViewNotHaveTransientState(getAnimateViewProperty());

            if (callback != null) {
                callback.onAnimationEnd(getAnimateViewProperty());
            }
        }
    }

    // 어뎁터뷰가 스크롤될 경우 아이템뷰들을 재사용하므로 예상치 못한 뷰에서 애니메이션이 실행되는 문제가 있다.
    // Android Developers 의 DevBytes: ListView Animations 를 참조,
    // 해당 뷰를 재사용하지 않게 setHasTransientState 프로퍼티를 true 로 설정,
    // 애니메이션이 끝난 후 다시 false 로 바꿔주는 방식을 사용했다.
    // 링크 : http://youtu.be/8MIfSxgsHIs
    private static void setViewHasTransientState(AnimateViewProperty property) {
        ViewCompat.setHasTransientState(property.getView(), true);
//        property.getView().setHasTransientState(true);
    }

    private static void setViewNotHaveTransientState(AnimateViewProperty property) {
        ViewCompat.setHasTransientState(property.getView(), false);
//        property.getView().setHasTransientState(false);
    }
}
