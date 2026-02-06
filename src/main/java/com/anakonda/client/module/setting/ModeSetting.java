package com.anakonda.client.module.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultValue, String... modes) {
        super(name, defaultValue);
        this.modes = Arrays.asList(modes);
        this.index = this.modes.indexOf(defaultValue);
    }

    public void cycle() {
        index = (index + 1) % modes.size();
        setValue(modes.get(index));
    }

    public boolean is(String mode) {
        return getValue().equalsIgnoreCase(mode);
    }
}
