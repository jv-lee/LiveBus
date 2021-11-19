package com.bus.core.live;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

/**
 * @author jv.lee
 * @date 2021/11/18
 * @description
 */
public class ObserverWrapper<T> implements Observer<T> {

    private final Observer<T> observer;

    public ObserverWrapper(Observer<T> observer) {
        this.observer = observer;
    }

    @Override
    public void onChanged(@Nullable T t) {
        if (observer != null) {
            if (isCallOnObserve()) {
                return;
            }
            observer.onChanged(t);
        }
    }

    private boolean isCallOnObserve() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 0) {
            for (StackTraceElement element : stackTrace) {
                if ("android.arch.lifecycle.LiveData".equals(element.getClassName()) &&
                        "observeForever".equals(element.getMethodName())) {
                    return true;
                }
            }
        }
        return false;
    }
}