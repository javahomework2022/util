/**
 *这个包中包含前后端均需要使用的内容.
 * @author 不要爆零小组
 * @version JDK17
 *
 */
package com.workonline.util;

import java.util.AbstractMap;
import java.util.List;

/**
 * 该类实现OT算法的核心内容：transform方法和compose方法.
 */
public class OT {
    /**
     * 该函数实现将两个并发操作转换为顺序执行的正确操作.
     * @param operation1 操作转换是相对于operation1执行的
     * @param operation2 需要转换的操作
     * @return 一对operation，其中key值为转换后的operation2
     * @throws Exception 当执行operation1需要的初始长度不等于执行operation2需要的初始长度时抛出异常
     */
    public static AbstractMap.SimpleEntry<Operation,Operation> transform(Operation operation1, Operation operation2)throws Exception{
        if(operation1.originalLength!=operation2.originalLength)
            throw new Exception("Operations must have the same originalLength");
        Operation rightOperation1 = new Operation();
        Operation rightOperation2 = new Operation();
        List<AtomicOperation> ops1 = operation1.operations;
        List<AtomicOperation> ops2 = operation2.operations;
        int i1 = 0,i2 = 0,len1 = ops1.size(),len2 = ops2.size();
        AtomicOperation op1 = (AtomicOperation)ops1.get(i1).clone();
        AtomicOperation op2 = (AtomicOperation)ops2.get(i2).clone();
        while(true){
            if(i1>=len1&&i2>=len2){
                break;
            }
            if(op1!=null&&op1.isInsert()){
                rightOperation1 = rightOperation1.insert(((Insert)op1).string);
                rightOperation2 = rightOperation2.retain(((Insert)op1).string.length());
                i1++;
                if(i1>=len1)
                    op1 = null;
                else
                    op1 = (AtomicOperation)ops1.get(i1).clone();
                continue;
            }
            if(op2!=null&&op2.isInsert()){
                rightOperation1 = rightOperation1.retain(((Insert)op2).string.length());
                rightOperation2 = rightOperation2.insert(((Insert)op2).string);
                i2++;
                if(i2>=len2)
                    op2 = null;
                else
                    op2 = (AtomicOperation)ops2.get(i2).clone();
                continue;
            }

            if (i1>=len1||op1 == null) {
                throw new Exception("Cannot compose operations: first operation is too short.");
            }
            if (i2>=len2||op2 ==null) {
                throw new Exception("Cannot compose operations: first operation is too long.");
            }

            int min;
            if(op1.isRetain()&&op2.isRetain()){
                int length1 = ((Retain)op1).length;
                int length2 = ((Retain)op2).length;
                if(length1>length2){
                    min = length2;
                    ((Retain)op1).length = ((Retain)op1).length-((Retain)op2).length;
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else if(length1 == length2){
                    min = length2;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else {
                    min = length1;
                    ((Retain)op2).length = ((Retain)op2).length-((Retain)op1).length;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                }
                rightOperation1 = rightOperation1.retain(min);
                rightOperation2 = rightOperation2.retain(min);
            }
            else if(op1.isDelete()&&op2.isDelete()){
                int length1 = ((Delete)op1).length;
                int length2 = ((Delete)op2).length;
                if(length1>length2){
                    ((Delete)op1).length = ((Delete)op1).length+((Delete)op2).length;
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else if(length1 == length2){
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else {
                    ((Delete)op2).length = ((Delete)op2).length+((Delete)op1).length;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                }
            }
            else if(op1.isDelete() && op2.isRetain()){
                int length1 = ((Delete)op1).length;
                int length2 = ((Retain)op2).length;
                if(length1>length2){
                    min = length2;
                    ((Delete)op1).length = ((Delete)op1).length-((Retain)op2).length;
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else if(length1 == length2){
                    min = length2;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else {
                    min = length1;
                    ((Retain)op2).length = ((Retain)op2).length-((Delete)op1).length;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                }
                rightOperation1 = rightOperation1.delete(min);
            }
            else if(op1.isRetain() && op2.isDelete()){
                int length1 = ((Retain)op1).length;
                int length2 = ((Delete)op2).length;
                if(length1>length2){
                    min = length2;
                    ((Retain)op1).length = ((Retain)op1).length-((Delete)op2).length;
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else if(length1 == length2){
                    min = length2;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else {
                    min = length1;
                    ((Delete)op2).length = ((Delete)op2).length-((Retain)op1).length;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                }
                rightOperation2 = rightOperation2.delete(min);
            }
            else {
                throw new Exception("The two operations cannot be transformed");
            }
        }
        return new AbstractMap.SimpleEntry<Operation,Operation>(rightOperation1,rightOperation2);
    }
    /**
     * 该方法实现将两个针对于一个文档的连续操作进行合并，使之成为一个操作
     * @param operation1 对于文档先进行的一个操作
     * @param operation2 对于同一个文档紧接着operation1进行的操作
     * @return 一个合并后的operation
     * @throws Exception 当执行operation1后文档的长度不等于执行operation2需要的初始长度时抛出异常
     */
    public static Operation compose(Operation operation1,Operation operation2) throws Exception{
        if(operation1.resultLength!=operation2.originalLength)
            throw new Exception("The originalLength of the second operation has to be the resultLength of the first operation");
        Operation rightOperation = new Operation();
        List<AtomicOperation> ops1 = operation1.operations;
        List<AtomicOperation> ops2 = operation2.operations;
        int i1 = 0,i2 = 0,len1 = ops1.size(),len2 = ops2.size();
        AtomicOperation op1 = (AtomicOperation)ops1.get(i1).clone();
        AtomicOperation op2 = (AtomicOperation)ops2.get(i2).clone();
        while(true){
            if(i1>=len1&&i2>=len2){
                break;
            }
            if(op1!=null&&op1.isDelete()){
                rightOperation = rightOperation.delete(((Delete)op1).length);
                i1++;
                if(i1>=len1)
                    op1 = null;
                else
                    op1 = (AtomicOperation)ops1.get(i1).clone();
                continue;
            }
            if(op2!=null&&op2.isInsert()){
                rightOperation = rightOperation.insert(((Insert)op2).string);
                i2++;
                if(i2>=len2)
                    op2 = null;
                else
                    op2 = (AtomicOperation)ops2.get(i2).clone();
                continue;
            }

            if (i1>=len1||op1 == null) {
                throw new Exception("Cannot compose operations: first operation is too short.");
            }
            if (i2>=len2||op2 == null) {
                throw new Exception("Cannot compose operations: first operation is too long.");
            }

            if(op1.isRetain()&&op2.isRetain()){
                int length1 = ((Retain)op1).length;
                int length2 = ((Retain)op2).length;
                if(length1>length2){
                    rightOperation = rightOperation.retain(((Retain)op2).length);
                    ((Retain)op1).length = ((Retain)op1).length-((Retain)op2).length;
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else if(length1 == length2){
                    rightOperation = rightOperation.retain(((Retain)op1).length);
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else {
                    rightOperation = rightOperation.retain(((Retain)op1).length);
                    ((Retain)op2).length = ((Retain)op2).length-((Retain)op1).length;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                }
            }
            else if(op1.isInsert()&&op2.isDelete()){
                int length1 = ((Insert)op1).string.length();
                int length2 = ((Delete)op2).length;
                if(length1>length2){
                    ((Insert)op1).string = ((Insert)op1).string.substring(((Delete)op2).length);
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else if(length1 == length2){
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else {
                    ((Delete)op2).length = ((Delete)op2).length-((Insert)op1).string.length();
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                }
            }
            else if(op1.isInsert() && op2.isRetain()){
                int length1 = ((Insert)op1).string.length();
                int length2 = ((Retain)op2).length;
                if(length1>length2){
                    rightOperation = rightOperation.insert(((Insert)op1).string.substring(0,length2));
                    ((Insert)op1).string = ((Insert)op1).string.substring(length2);
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else if(length1 == length2){
                    rightOperation = rightOperation.insert(((Insert)op1).string);
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else {
                    rightOperation = rightOperation.insert(((Insert)op1).string);
                    ((Retain)op2).length = ((Retain)op2).length-length1;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                }
            }
            else if(op1.isRetain() && op2.isDelete()){
                int length1 = ((Retain)op1).length;
                int length2 = ((Delete)op2).length;
                if(length1>length2){
                    rightOperation = rightOperation.delete(length2);
                    ((Retain)op1).length = ((Retain)op1).length-((Delete)op2).length;
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else if(length1 == length2){
                    rightOperation = rightOperation.delete(length2);
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                    i2++;
                    if(i2>=len2)
                        op2 = null;
                    else
                        op2 = (AtomicOperation)ops2.get(i2).clone();
                }
                else {
                    rightOperation = rightOperation.delete(length1);
                    ((Delete)op2).length = ((Delete)op2).length-((Retain)op1).length;
                    i1++;
                    if(i1>=len1)
                        op1 = null;
                    else
                        op1 = (AtomicOperation)ops1.get(i1).clone();
                }
            }
            else {
                throw new Exception( "This shouldn't happen: op1: "+op1+",op2: "+op2);
            }
        }
        return rightOperation;
    }
}

