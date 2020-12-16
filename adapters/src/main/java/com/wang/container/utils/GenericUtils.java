package com.wang.container.utils;

import android.content.Context;

import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * 获取泛型相关操作
 */
public class GenericUtils {
    private static final HashMap<String, Integer> mIds = new HashMap<>(64);

    /**
     * 根据dataBinding的名称获取对应资源id
     * <p>
     * 使用条件：您必须不混淆{@link ViewDataBinding}的子类
     *
     * @param baseClass  基类class（BaseXxx.class）
     * @param childClass 子类class（getClass()）
     * @throws IndexOutOfBoundsException 必须在Proguard里忽略ViewDataBinding的子类不然会崩溃
     */
    @MainThread
    @LayoutRes
    public static int getGenericRes(Context context, Class baseClass, Class childClass) {
        //取缓存
        String clsName = baseClass.getName() + childClass.getName();
        Integer id = mIds.get(clsName);
        if (id != null) {
            return id;
        }

        //获取子类的dataBinding名
        Class<? extends ViewDataBinding> dbClass = GenericUtils.getGenericClass(ViewDataBinding.class, baseClass, childClass);
        if (dbClass == null || dbClass == ViewDataBinding.class) {
            throw new RuntimeException("泛型不合规：" + dbClass + "，class：" + childClass + "（如果想自定义，你必须覆盖相关方法）");
        }

        //根据泛型查找id
        char[] chars = dbClass.getSimpleName().toCharArray();
        StringBuilder builder = new StringBuilder();
        for (char c : chars) {
            if (c < 91 && c > 64) {
                c = (char) (c + 32);
                if (builder.length() > 0) {
                    builder.append("_");
                    builder.append(c);
                } else {
                    builder.append(c);
                }
            } else {
                builder.append(c);
            }
        }
        builder.setLength(builder.length() - 8);//去掉结尾的_binding
        id = context.getResources().getIdentifier(builder.toString(), "layout", context.getPackageName());
        mIds.put(clsName, id);//缓存
        return id;
    }

    /**
     * 获取泛型对应的class
     *
     * @param genericSuperClass 泛型父类的class，以便找到泛型
     * @param endClass          遍历结束的class（一般是最后一个泛型类的class，如BaseActivity.class）
     * @param myClass           当前类的class（getClass()）
     * @return 泛型的class
     */
    public static <T> Class<T> getGenericClass(@NonNull Class genericSuperClass, Class endClass, Class myClass) {
        if (myClass == endClass || myClass == Object.class || myClass == null) {
            return null;
        }
        Type superType = myClass.getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) superType).getActualTypeArguments();
            for (Type type : types) {
                if (type instanceof Class) {
                    Class vhClass = (Class) type;
                    //noinspection unchecked
                    if (genericSuperClass.isAssignableFrom(vhClass)) {
                        //noinspection unchecked
                        return vhClass;
                    }
                }
            }
        }
        return getGenericClass(genericSuperClass, endClass, myClass.getSuperclass());
    }
}
