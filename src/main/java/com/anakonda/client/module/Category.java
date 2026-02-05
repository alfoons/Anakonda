package com.anakonda.client.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    WORLD("World"),
    MISC("Misc");

    public final String name;

    Category(String name) {
        this.name = name;
    }
}
