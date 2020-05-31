package com.jenkin.dubbo.wrapper;

public interface Car {
    String getBrand();
    long getWeight();
    void make(String brand, long weight);
}