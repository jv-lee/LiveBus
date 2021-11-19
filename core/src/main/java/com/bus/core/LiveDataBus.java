package com.bus.core;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

import com.bus.annotation.ObserverBus;
import com.bus.core.live.BusMutableLiveData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jv.lee
 * @date 2019/3/30
 * 事件总线
 */
public class LiveDataBus {

    /**
     * 消息通道
     */
    private final Map<String, BusMutableLiveData<Object>> bus;

    /**
     * 存储非激活事件的临时容器
     */
    private final Map<String, Observer<Object>> tempMap = new HashMap<>();

    private volatile static LiveDataBus instance;

    private LiveDataBus() {
        bus = new HashMap<>();
    }

    public static LiveDataBus getInstance() {
        if (instance == null) {
            synchronized (LiveDataBus.class) {
                if (instance == null) {
                    instance = new LiveDataBus();
                }
            }
        }
        return instance;
    }

    public <T> MutableLiveData<T> getChannel(String target, Class<T> type) {
        if (!bus.containsKey(target)) {
            bus.put(target, new BusMutableLiveData<>());
        }
        return (MutableLiveData<T>) bus.get(target);
    }

    public MutableLiveData<Object> getChannel(String target) {
        return getChannel(target, Object.class);
    }


    /**
     * 订阅通知
     *
     * @param lifecycleOwner 生命周期
     */
    public void injectBus(LifecycleOwner lifecycleOwner) {
        Class<? extends LifecycleOwner> aClass = lifecycleOwner.getClass();
        //获取当前类所有方法
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            //激活通知
            ObserverBus injectBus = method.getAnnotation(ObserverBus.class);
            if (injectBus != null) {
                String value = injectBus.value();
                boolean isActive = injectBus.isActive();
                boolean viscosity = injectBus.isViscosity();

                //创建通知后要执行的操作 ， 调用数据方法
                Observer<Object> observer = o -> {
                    try {
                        method.invoke(lifecycleOwner, o);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                };

                //是否是激活状态通知
                if (isActive) {
                    LiveDataBus.getInstance().getChannel(value).observe(lifecycleOwner, observer);
                } else {
                    tempMap.put(value, observer);
                    LiveDataBus.getInstance().getChannel(value).observeForever(observer);
                }

                //处理粘性事件
                if (viscosity) {
                    lifecycleOwner.getLifecycle().addObserver(new LifecycleObserver() {
                        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                        public void onCreate() {
                            MutableLiveData<Object> channel = LiveDataBus.getInstance().getChannel(value);
                            //获取最新一条消息补发
                            Object vicValue = channel.getValue();
                            if (vicValue != null) {
                                channel.postValue(vicValue);
                            }
                        }

                        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                        public void onDestroy() {
                            lifecycleOwner.getLifecycle().removeObserver(this);
                        }
                    });

                }
            }

        }
    }

    /**
     * 取消订阅通知 (仅在使用非激活状态可通知模式 需要取消订阅)
     *
     * @param lifecycleOwner 生命周期
     */
    public void unInjectBus(LifecycleOwner lifecycleOwner) {
        Class<? extends LifecycleOwner> aClass = lifecycleOwner.getClass();
        //获取当前类所有方法
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            ObserverBus injectBus = method.getAnnotation(ObserverBus.class);
            if (injectBus != null) {
                String value = injectBus.value();
                boolean isActive = injectBus.isActive();

                //非活跃状态通知取消订阅
                if (!isActive) {
                    Observer remove = tempMap.remove(value);
                    if (remove != null) {
                        LiveDataBus.getInstance().getChannel(value).removeObserver(remove);
                    }
                }
            }
        }
    }

}