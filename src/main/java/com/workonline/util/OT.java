package com.workonline.util;

import java.util.AbstractMap;
import java.util.List;

/*
OT中目前实现两个方法：
1.transform方法：传入A，B两个操作，<A',B'> = transform(A,B)，其中A'为先B后A',B'为先A后B’，对于这种操作顺序，能保证正确；
    使用方法：AbstractMap.SimpleEntry<Operation,Operation> entry = OT.transform(operation1,operation2)；
            A' = entry.getKey();B' = entry.getValue();
2.compose方法：传入A，B两个操作，其中B为A之后，在没有经过服务器确认之前，用户又对于其文本做的操作，compose方法将其合为一个操作。
    使用方法：Operation operation = OT.compose(operationA,operationB);注意B一定是紧接着A的操作；
 */

public class OT {
    /*
    该函数实现将两个并发操作转换为正确操作，注意其中需要对两种操作归属的原子操作进行枚举
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
    /*
    该方法实现将两个针对于一个文档的连续操作进行合并，使之成为一个操作
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

