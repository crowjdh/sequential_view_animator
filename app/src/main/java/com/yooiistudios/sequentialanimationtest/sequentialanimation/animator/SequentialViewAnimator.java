package com.yooiistudios.sequentialanimationtest.sequentialanimation.animator;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import com.yooiistudios.sequentialanimationtest.sequentialanimation.ViewProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animationproperty.TransitionProperty;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Dongheyon Jeong in News-Android-L from Yooii Studios Co., LTD. on 15. 1. 27.
 *
 * SequentialViewAnimator
 *  순차적으로 뷰들을 animating 하는 클래스
 */
public abstract class SequentialViewAnimator<T extends TransitionProperty> {
    private final SparseArray<ViewProperty> mViewProperties;
    private T mTransitionProperty;
    private TransitionHandler mTransitionHandler;
    private long mStartTimeInMilli;

    protected SequentialViewAnimator() {
        mViewProperties = new SparseArray<>();
        mTransitionHandler = new TransitionHandler(this);
    }

    public void putAnimateViewPropertyAt(ViewProperty property, int idx) {
        mViewProperties.put(idx, property);
    }

    public void setTransitionProperty(T transitionProperty) {
        mTransitionProperty = transitionProperty;
    }

    public void animate() {
        if (isReadyForTransition()) {
            cancelAllTransitions();
            prepareForNewTransitionSequence();
            runSequentialTransition();
        }
    }

    public void cancelAllTransitions() {
        mTransitionHandler.removeCallbacksAndMessages(null);
    }

    private void runSequentialTransition() {
        int viewCount = mViewProperties.size();
        for (int i = 0; i < viewCount; i++) {
            // SparseArray 의 keyAt 메서드 특성상 아래와 같이 쿼리하면 key 의 ascending order 로 결과값이 나온다.
            int propertyIndex = mViewProperties.keyAt(i);
            ViewProperty property = mViewProperties.get(propertyIndex);
            beforeRequestTransition(property);
            requestTransition(property);
        }
    }

    private void requestTransition(ViewProperty property) {
        long delay = getTransitionProperty().getDelay(property);
        Message message = Message.obtain();
        message.obj = property;

        mTransitionHandler.sendMessageDelayed(message, delay);
    }

    private void requestNextTransition(ViewProperty property) {
        if (!isLastTransition(property)) {
            property.increaseTransitionIndex();

            requestTransition(property);
        }
    }

    protected abstract void transit(ViewProperty property);

    // TODO 리스너로 뺄 수 있으면 빼자
    protected abstract void beforeRequestTransition(ViewProperty property);

    protected boolean isLastTransition(ViewProperty property) {
        List transitions = getTransitionProperty().getTransitions();
        return property.getTransitionIndex() == transitions.size() - 1;
    }

    private boolean isReadyForTransition() {
        return mViewProperties.size() > 0 && mTransitionProperty != null;
    }

    private void prepareForNewTransitionSequence() {
        mStartTimeInMilli = System.currentTimeMillis();
    }

    protected T getTransitionProperty() {
        return mTransitionProperty;
    }

    protected SparseArray<ViewProperty> getViewProperties() {
        return mViewProperties;
    }

    private static class TransitionHandler extends Handler {
        private WeakReference<SequentialViewAnimator> mAnimatorWeakReference;

        public TransitionHandler(SequentialViewAnimator animator) {
            mAnimatorWeakReference = new WeakReference<>(animator);
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            validateMessage(message);

            final ViewProperty property = getProperty(message);
            boolean animate = property.getView() != null;

            if (animate) {
                SequentialViewAnimator animator = mAnimatorWeakReference.get();

                animator.transit(property);
                animator.requestNextTransition(property);
            }
        }

        private ViewProperty getProperty(Message message) {
            return (ViewProperty)message.obj;
        }

        private void validateMessage(Message message) {
            if (!(message.obj instanceof ViewProperty)) {
                throw new IllegalStateException("msg.obj MUST BE an instance of subclass of "
                        + ViewProperty.class.getSimpleName());
            }
        }
    }
}
