package com.anakonda.client.module;

import com.anakonda.client.module.combat.BreachSwap;
import com.anakonda.client.module.combat.Triggerbot;
import com.anakonda.client.module.movement.NoSlowdown;
import com.anakonda.client.module.render.ESP;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    public static final ModuleManager INSTANCE = new ModuleManager();
    private final List<Module> modules = new ArrayList<>();

    public void init() {
        add(new Triggerbot());
        add(new BreachSwap());
        add(new NoSlowdown());
        add(new ESP());
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Module getModule(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> clazz) {
        return modules.stream()
                .filter(m -> m.getClass() == clazz)
                .map(m -> (T) m)
                .findFirst()
                .orElse(null);
    }

    public void onTick() {
        modules.stream().filter(Module::isEnabled).forEach(Module::onTick);
    }

    public void add(Module module) {
        modules.add(module);
    }
}
