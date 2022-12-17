package com.workonline.util;

import java.io.Serializable;

/*
本类中的操作是原子操作,分为保持、插入、删除
 */
public abstract class AtomicOperation implements Cloneable, Serializable {
    /*
    以下实现判断原子操作是什么
     */
    public boolean isInsert(){
        return this instanceof Insert;
    }
    public boolean isDelete(){
        return this instanceof Delete;
    }
    public boolean isRetain(){
        return this instanceof Retain;
    }

    public int getRetainLength() {
        if(this instanceof Retain)
            return ((Retain)this).length;
        else
            return -1;
    }

    public String getInsertString() {
        if(this instanceof Insert)
            return ((Insert)this).string;
        else
            return null;
    }

    public int getDeleteLength() {
        if(this instanceof Delete)
            return ((Delete)this).length;
        else
            return -1;
    }
    /*
    重写clone方法
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

class Retain extends AtomicOperation{
    int length;
    public Retain(int length){
        this.length = length;
    }

    @Override
    public String toString() {
        return "retain"+length;
    }
}

class Insert extends AtomicOperation{
    String string;//插入的字符串
    public Insert(String string){
        this.string = string;
    }

    @Override
    public String toString() {
        return "Insert"+string;
    }
}

class Delete extends AtomicOperation{
    int length;//删除的字符串长度
    public Delete(int length){
        this.length = length;
    }

    @Override
    public String toString() {
        return "Delete"+length;
    }
}