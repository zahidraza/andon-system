/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.models;

import java.util.Map;

/**
 *
 * @author Md Zahid Raza
 */
public class Pair<K,V> implements Map.Entry{
    
    private K key;
    private V value;
    
    public Pair(){}
    
    public Pair(K key,V value){
        this.key = key;
        this.value = value;
    }
    
    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public Object setValue(Object value) {
        this.value = (V)value;
        return this.value;
    }
    
}
