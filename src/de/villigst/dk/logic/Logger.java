package de.villigst.dk.logic;

import org.jetbrains.annotations.NotNull;

public class Logger {

    public static void info(@NotNull String msg) {
        System.out.println("[Info] " + msg);
    }

    public static void error(@NotNull String msg) {
        System.out.println("[ERROR] " + msg);
    }

}
