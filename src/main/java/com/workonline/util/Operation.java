/**
 *这个包中包含前后端均需要使用的内容.
 * @author 不要爆零小组
 * @version JDK17
 *
 */
package com.workonline.util;

import java.io.Serializable;
import java.util.*;

/**
 *该类将用户的操作转化为对于文本的原子操作集合，共为三种，分别为保持(Retain)、插入(Insert)和删除(Delete).
 * <p>
 * 使用方法：operation.retain(int n),n为保持的字符串长度，如果是0可以不写；operation.insert(String s),s为插入的字符串；operation.delete(n),这是个泛型，n可以删去的字符串，也可以是删去的字符串长度.
 */
public class Operation implements Serializable,Cloneable {
    /**
     * 原子操作列表.
     */
    List<AtomicOperation> operations = new LinkedList<>();
    /**
     * 操作作用于的字符串的长度.
     */
    int originalLength;
    /**
     * 操作应用后字符串长度.
     */
    int resultLength;
    /**
     * 构造器.
     */
    public Operation(){
        this.originalLength = 0;
        this.resultLength = 0;
    }

    /**
     * 实现将Retain操作添加到操作列表中
     * @param len Retain操作的长度
     * @return 操作列表
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
    /**
     * 实现将Insert操作添加到操作列表中
     * @param string Insert操作的字符串
     * @return 操作列表
     */
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
                this.operations.add(this.operations.size()-1,newAtomicOperation);
            }
        }
        else {
            AtomicOperation newAtomicOperation = new Insert(string);
            this.operations.add(newAtomicOperation);
        }
        return this;
    }

    /**
     * 实现将Delete操作添加到操作列表中.
     * @param t Delete操作的字符串或其长度
     * @return 操作列表
     * @param <T> String或Integer
     */
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

    /**
     * 重写clone方法
     * @return Operation
     */
    @Override
    public Object clone() {
        Operation operation = null;
        try{
            operation = (Operation) super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return operation;
    }

    public List<AtomicOperation> getOperations() {
        return operations;
    }

    /**
     * 重写toString方法
     * @return String
     */
    @Override
    public String toString() {
        String ans = "";
        for (var o:operations) {
            ans += operations.toString()+"\n";
        }
        ans += resultLength;
        return ans;
    }
}

