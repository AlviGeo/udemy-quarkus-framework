package com.cofecode.services;

import jakarta.enterprise.context.Dependent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class GameService {

    @ConfigProperty(name="test")
    String test;

    public void test() {
        System.out.println(test);
    }
}
