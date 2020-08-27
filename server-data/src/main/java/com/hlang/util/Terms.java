package com.hlang.util;

import com.xiaoping.utils.DataRow;

import java.util.Map;

public class Terms extends DataRow
{

    /**
     *
     * 描述：获取一个实例
     *
     * 使用方式：Trems.newInstance().add(key1, val1).add(key2, val2);
     * @author 简超平
     * @created 2019年9月24日 下午8:39:39
     * @since
     * @return
     */
    public static Terms newInstance() {
        return new Terms();
    }

    /**
     *
     * 描述：添加条件
     * @author 简超平
     * @created 2019年9月24日 下午3:56:18
     * @since
     * @param name
     * @param value
     * @return
     */
    public Terms add(String name, int value) {
        set(name, value);
        return this;
    }

    public Terms add(String name, long value) {
        set(name, value);
        return this;
    }

    public Terms add(String name, double value) {
        set(name, value);
        return this;
    }

    public Terms add(String name, float value) {
        set(name, value);
        return this;
    }

    public Terms add(String name, boolean value) {
        set(name, value);
        return this;
    }

    public Terms add(String name, String value) {
        set(name, value);
        return this;
    }

    public Terms add(String name, Object value) {
        set(name, value);
        return this;
    }


    public Terms addAll(Map m) {
        this.putAll(m);
        return this;
    }

    /**
     *
     * 描述：DataRow 的非空判断
     * data = {} 判定为空
     * @author 简超平
     * @created 2019年11月5日 下午2:55:41
     * @since
     * @param data
     * @return
     */
    public static boolean isNotBlank (Map data) {
        return null != data && !data.isEmpty();
    }

    /**
     *
     * 描述：DataRow 空判断
     * @author 简超平
     * @created 2019年11月11日 上午10:34:13
     * @since
     * @param data
     * @return
     */
    public static boolean isBlank (Map data) {
        return null == data || data.isEmpty();
    }
}
