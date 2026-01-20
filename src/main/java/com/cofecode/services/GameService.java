package com.cofecode.services;

import jakarta.enterprise.context.Dependent;

@Dependent
public class GameService {
    public void test() {
        System.out.println("TEST!!");
    }
}
