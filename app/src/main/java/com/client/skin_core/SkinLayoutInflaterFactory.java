package com.client.skin_core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import com.client.skin_core.util.L;
import com.client.skin_core.util.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

class SkinLayoutInflaterFactory implements LayoutInflater.Factory2, Observer {
    private static final String[] mClassPrefixList = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };
    //记录对应View的构造函数
    private static final Map<String, Constructor<? extends View>> mConstructorMap
            = new HashMap<>();
    private static final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};

    // 当选择新皮肤后需要替换View与之对应的属性
    // 页面属性管理器
    private SkinAttribute skinAttribute;

    private Activity activity;

    public SkinLayoutInflaterFactory(Activity activity, Typeface typeface) {
        this.activity = activity;
        skinAttribute = new SkinAttribute(typeface);
    }

    /*
    * 当系统加载布局的时候调用，收集需要换肤的控件
    * 主要功能是得到当前控件并将其加入到换肤列表中（mSkinViews）
    * */
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        //换肤就是在需要时候替换 View的属性(src、background等)
        //所以这里创建 View,从而修改View属性
        View view = createViewFromTag(name, context, attrs);
        if (null == view) {
            view = createView(name, context, attrs);
        }

        if (null != view) {
            L.e(String.format("检查[%s]:" + name, context.getClass().getName()));
            //加载属性
            skinAttribute.load(view, attrs);
        }
        return view;
    }

    private View createViewFromTag(String name, Context context, AttributeSet attrs) {
        if(name.contains(".")){
            return null;
        }
        for(int i = 0; i < mClassPrefixList.length; i++){
            View view = createView(mClassPrefixList[i] + name, context, attrs);
            if(null != view){
                return view;
            }
        }
        return  null;
    }

    private View createView(String name, Context context, AttributeSet attrs) {
        Constructor<? extends View> constructor = findConstructor(context, name);
        try {
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
        }
        return null;
    }

    private Constructor<? extends View> findConstructor(Context context, String name) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (null == constructor) {
            try {
                //asSubclass 强转， 将Class<?>强转为Class<? extends View> 类型
                Class<? extends View> clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = clazz.getConstructor(mConstructorSignature);
                mConstructorMap.put(name, constructor);
            } catch (Exception e) {
            }
        }
        return constructor;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    /*
    * 通过监听进入，进行控件换肤
    * */
    @Override
    public void update(Observable o, Object arg) {
        L.i("=========更新=========");
        //换状态栏
        SkinThemeUtils.updateStatusBarColor(activity);
        //换字体
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
        skinAttribute.setTypeface(typeface);
        //换其他背景
        skinAttribute.applySkin();
    }
}
