package com.workonline.util;

import java.util.*;

/*
该类将用户的操作转化为对于文本的原子操作集合，共为三种，分别为保持(Retain)、插入(Insert)和删除(Delete)。

注意要先建一个operation，之后使用里面的retain、insert、delete方法向空的operation里面加原子操作，此条注释针对于将文本转化为操作时(这方法没写)

使用方法：operation.retain(int n),n为保持的字符串长度，如果是0可以不写；
        operation.insert(String s),s为插入的字符串；
        operation.delete(n),这是个泛型，n可以删去的字符串，也可以是删去的字符串长度；
*/

public class Operation{
    //存储标准操作
    List<AtomicOperation> operations = new LinkedList<>();
    //操作作用于的字符串的长度
    int originalLength;
    //操作应用后字符串长度
    int resultLength;
    //构造方法
    public Operation(){
        this.originalLength = 0;
        this.resultLength = 0;
    }

    /*
    以下实现将保持、插入、删除添加到一个新的操作集合中，在构建新的操作时需要用到
     */
    public Operation retain(int len){
        if(len == 0)
            return this;
        this.originalLength+=len;
        this.resultLength+=len;
        if(this.operations.size()!=0&&this.operations.get(this.operations.size()-1).isRetain()){
            ((Retain)this.operations.get(this.operations.size() - 1)).length+=len;
        }
        else {
            AtomicOperation newAtomicOperation = new Retain(len);
            this.operations.add(newAtomicOperation);
        }
        return this;
    }
    public Operation insert(String string){
        if(string.equals("")){
            return this;
        }
        this.resultLength+=string.length();
        if(this.operations.size()!=0&&this.operations.get(this.operations.size()-1).isInsert()){
            ((Insert)this.operations.get(this.operations.size()-1)).string+=string;
        }
        else if(this.operations.size()!=0&&this.operations.get(this.operations.size()-1).isDelete()){
            if(this.operations.size()!=1&&this.operations.get(this.operations.size()-2).isInsert()){
                ((Insert)this.operations.get(this.operations.size()-2)).string+=string;
            }
            else {
                AtomicOperation newAtomicOperation = new Insert(string);
                this.operations.add(this.operations.size()-2,newAtomicOperation);
            }
        }
        else {
            AtomicOperation newAtomicOperation = new Insert(string);
            this.operations.add(newAtomicOperation);
        }
        return this;
    }
    public <T> Operation delete(T t){
        int len;
        if(t instanceof String){
            len = ((String) t).length();
        }
        else
            len = (Integer)t;
        if(len == 0)
            return this;
        this.originalLength+=len;
        if(this.operations.size()!=0&&this.operations.get(this.operations.size()-1).isDelete()){
            ((Delete)this.operations.get(this.operations.size()-1)).length+=len;
        }
        else {
            AtomicOperation newAtomicOperation = new Delete(len);
            this.operations.add(newAtomicOperation);
        }
        return this;
    }
}

