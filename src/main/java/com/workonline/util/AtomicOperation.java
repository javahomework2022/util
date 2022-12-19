/**
 *这个包中包含前后端均需要使用的内容.
 * @author
 * @version JDK17
 *
 */
package com.workonline.util;

import java.io.Serializable;

/**
 * 本类中的操作是原子操作,分为保持、插入、删除.
 */
public abstract class AtomicOperation implements Cloneable, Serializable {
    /**
     * 实现判断原子操作是不是Insert.
     * @return 返回是否是Insert
     */
    public boolean isInsert(){
        return this instanceof Insert;
    }
    /**
     * 实现判断原子操作是不是Delete.
     * @return 返回是否是Delete
     */
    public boolean isDelete(){
        return this instanceof Delete;
    }

    /**
     * 实现判断原子操作是不是Retain.
     * @return 返回是否是Retain
     */
    public boolean isRetain(){
        return this instanceof Retain;
    }

    /**
     * 获取Retain操作的长度.
     * @return 操作长度
     */
    public int getRetainLength() {
        if(this instanceof Retain)
            return ((Retain)this).length;
        else
            return -1;
    }

    /**
     * 获取Insert操作的字符串.
     * @return 操作的字符串
     */
    public String getInsertString() {
        if(this instanceof Insert)
            return ((Insert)this).string;
        else
            return null;
    }

    /**
     * 获取Delete操作的长度.
     * @return 操作长度
     */
    public int getDeleteLength() {
        if(this instanceof Delete)
            return ((Delete)this).length;
        else
            return -1;
    }

    /**
     * 重写克隆方法.
     * @return 克隆的原子操作
     */
    @Override
    public Object clone() {
        AtomicOperation atomicOperation = null;
        try{
            atomicOperation = (AtomicOperation) super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return atomicOperation;
    }

}

/**
 * 原子操作中的Retain.
 */
class Retain extends AtomicOperation{
    int length;
    /**
     * 构造器.
     * @param length 保持的字符串长度
     */
    public Retain(int length){
        this.length = length;
    }

    /**
     * 重写toString方法.
     * @return 长度
     */
    @Override
    public String toString() {
        return "retain"+length;
    }
}

/**
 * 原子操作中的Insert.
 */
class Insert extends AtomicOperation{
    String string;//插入的字符串
    /**
     * 构造器.
     * @param string 插入的字符串
     */
    public Insert(String string){
        this.string = string;
    }
    /**
     * 重写toString方法.
     * @return 长度
     */
    @Override
    public String toString() {
        return "Insert"+string;
    }
}
/**
 * 原子操作中的Delete
 */
class Delete extends AtomicOperation{
    int length;//删除的字符串长度
    /**
     * 构造器.
     * @param length 删除的字符串长度
     */
    public Delete(int length){
        this.length = length;
    }
    /**
     * 重写toString方法.
     * @return 长度
     */
    @Override
    public String toString() {
        return "Delete"+length;
    }
}