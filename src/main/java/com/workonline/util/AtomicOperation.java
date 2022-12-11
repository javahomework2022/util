package com.workonline.util;

/*
本类中的操作是原子操作,分为保持、插入、删除
 */
public abstract class AtomicOperation implements Cloneable{
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