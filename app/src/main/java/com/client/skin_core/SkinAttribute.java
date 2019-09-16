package com.client.skin_core;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.client.skin_core.util.L;
import com.client.skin_core.util.SkinResources;
import com.client.skin_core.util.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

class SkinAttribute {

    private static final List<String> mAttributes = new ArrayList<>();

    static {
        mAttributes.add("background");
        mAttributes.add("src");

        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");

        mAttributes.add("skinTypeface");

    }

    private Typeface typeface;

    //记录换肤需要操作的View与属性信息
    private List<SkinView> mSkinViews = new ArrayList<>();

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public void applySkin() {

        for (SkinView mSkinView : mSkinViews) {
            L.i("=======更新列表======="+ mSkinView.view.toString());
            mSkinView.applySkin(typeface);
        }
    }

    public SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
    }

    /*
    * 换肤控件筛选，根据其的属性值确定
    * */
    public void load(View view, AttributeSet attrs) {
        List<SkinPair> mSkinPairs = new ArrayList<>();
        for(int i = 0; i < attrs.getAttributeCount(); i++){
            String attributeName = attrs.getAttributeName(i);
            if(mAttributes.contains(attributeName)){
                String attributeValue = attrs.getAttributeValue(i);
                if(attributeValue.startsWith("#")){
                    continue;
                }
                int resId;
                if(attributeValue.startsWith("?")){
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                }else{
                    resId = Integer.parseInt(attributeValue.substring(1));
                }

                if(resId !=0){
                    SkinPair skinPair = new SkinPair(attributeName, resId);
                    mSkinPairs.add(skinPair);
                }
            }
        }

        //最原始的textview是不包括mAttributes的元素的，但是需要换字体
        if(!mSkinPairs.isEmpty() || view instanceof TextView || view instanceof SkinViewSupport){
            SkinView skinView = new SkinView(view, mSkinPairs);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        }

    }

    static class SkinView {
        View view;
        List<SkinPair> skinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        public void applySkin(Typeface typeface) {
            L.i("=======开始替换布局=======");
            applyTypeFace(typeface);
            applySkinSupport();
            for (SkinPair skinPair: skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                Object background;
                switch(skinPair.attributeName){
                    case "background":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                    background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList
                                (skinPair.resId));
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "skinTypeface":
                        applyTypeFace(SkinResources.getInstance().getTypeface
                                (skinPair.resId));
                        break;
                    default:
                        break;
                }

                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right,
                            bottom);
                }
            }
        }

        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                L.i("========="+view.toString());
                ((SkinViewSupport) view).applySkin();
            }
        }

        private void applyTypeFace(Typeface typeface) {
            L.i("===检查字体==="+view.toString());
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    static class SkinPair {

        String attributeName;
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
