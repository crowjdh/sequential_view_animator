package com.yooiistudios.sequentialanimation.ui.animation;

import android.support.v4.view.ViewCompat;

import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * ViewUtil
 *  뷰 유틸....
 */
public class ViewTransientUtils {
    // 어뎁터뷰가 스크롤될 경우 아이템뷰들을 재사용하므로 예상치 못한 뷰에서 애니메이션이 실행되는 문제가 있다.
    // Android Developers 의 DevBytes: ListView Animations 를 참조,
    // 해당 뷰를 재사용하지 않게 setHasTransientState 프로퍼티를 true 로 설정,
    // 애니메이션이 끝난 후 다시 false 로 바꿔주는 방식을 사용했다.
    // 링크 : http://youtu.be/8MIfSxgsHIs
    public static void setState(ViewProperty property) {
        ViewCompat.setHasTransientState(property.getView(), true);
    }

    public static void clearState(ViewProperty property) {
        ViewCompat.setHasTransientState(property.getView(), false);
    }
}
