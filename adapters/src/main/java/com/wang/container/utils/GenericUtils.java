package com.wang.container.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import java.lang.reflect.Method;
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
     * @return 0表示没找到，请查看Proguard里有没有混淆
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
        Class<? extends ViewDataBinding> dbClass = getBindingClass(baseClass, childClass);

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
        if (builder.length() > 8) {
            builder.setLength(builder.length() - 8);//去掉结尾的_binding
            id = context.getResources().getIdentifier(builder.toString(), "layout", context.getPackageName());
            if (id != 0) {
                mIds.put(clsName, id);//缓存
                return id;
            }
        }
        return 0;
    }

    /**
     * 根据泛型获取dataBinding的view，支持混淆
     *
     * @param parent inflate的parent
     */
    @NonNull
    public static View getGenericView(Context context, Class baseClass, Class childClass, @Nullable ViewGroup parent) {
        int id = getGenericRes(context, baseClass, childClass);
        LayoutInflater inflater = LayoutInflater.from(context);
        if (id != 0) {
            return inflater.inflate(id, parent, false);
        }
        //获取子类的dataBinding名
        Class<? extends ViewDataBinding> dbClass = getBindingClass(baseClass, childClass);
        try {
            Method im = dbClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            ViewDataBinding vdb = (ViewDataBinding) im.invoke(null, inflater, parent, false);
            return vdb.getRoot();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("未知错误：" + dbClass + "，class：" + childClass);
        }
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

    @NonNull
    private static Class<? extends ViewDataBinding> getBindingClass(Class baseClass, Class childClass) {
        Class<? extends ViewDataBinding> dbClass = GenericUtils.getGenericClass(ViewDataBinding.class, baseClass, childClass);
        if (dbClass == null || dbClass == ViewDataBinding.class) {
            throw new RuntimeException("泛型不合规：" + dbClass + "，class：" + childClass + "（如果想自定义，你必须覆盖相关方法）");
        }
        return dbClass;
    }
}
