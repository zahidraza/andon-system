/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

/**
 *
 * @author Md Zahid Raza
 */
public class Tuple<A,B,C,D> {
    private A value1;
    private B value2;
    private C value3;
    private D value4;

    public Tuple(){}
    
    public Tuple(A value1, B value2, C value3,D value4) {
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
    }

    public A getValue1() {
        return value1;
    }

    public void setValue1(A value1) {
        this.value1 = value1;
    }

    public B getValue2() {
        return value2;
    }

    public void setValue2(B value2) {
        this.value2 = value2;
    }

    public C getValue3() {
        return value3;
    }

    public void setValue3(C value3) {
        this.value3 = value3;
    }

    public D getValue4() {
        return value4;
    }

    public void setValue4(D value4) {
        this.value4 = value4;
    }
    
    
    
}
