package com.wang.container.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 获取泛型相关操作
 */
object GenericUtils {

    /**
     * 根据泛型获取dataBinding的view
     *
     * @param parent inflate的parent
     */
    fun getGenericVB(
        context: Context,
        baseClass: Class<*>,
        childClass: Class<*>,
        parent: ViewGroup?
    ): ViewBinding {
        //获取子类的dataBinding名
        val dbClass = getBindingClass(baseClass, childClass)
        try {
            val im = dbClass.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType
            )
            return im.invoke(null, LayoutInflater.from(context), parent, false) as ViewBinding
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("未知错误：$dbClass，class：$childClass", e)
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
    fun <T> getGenericClass(
        genericSuperClass: Class<*>,
        endClass: Class<*>,
        myClass: Class<*>?
    ): Class<T>? {
        if (myClass == endClass || myClass == Any::class.java || myClass == null) {
            return null
        }
        val superType = myClass.genericSuperclass
        if (superType is ParameterizedType) {
            superType.actualTypeArguments.forEach { type ->
                if (type is Class<*>) {
                    if (genericSuperClass.isAssignableFrom(type)) {
                        return type as Class<T>?
                    }
                }
            }
        }
        return getGenericClass(genericSuperClass, endClass, myClass.superclass)
    }

    private fun getBindingClass(
        baseClass: Class<*>,
        childClass: Class<*>
    ): Class<out ViewBinding> {
        val dbClass: Class<out ViewBinding>? = getGenericClass(
            ViewBinding::class.java, baseClass, childClass
        )
        if (dbClass == null || dbClass == ViewBinding::class.java) {
            throw RuntimeException("泛型不合规：$dbClass，class：$childClass（如果想自定义，你必须覆盖相关方法）")
        }
        return dbClass
    }
}