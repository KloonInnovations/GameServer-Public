package io.kloon.gameserver.minestom.blocks.properties;

import net.minestom.server.instance.block.Block;

import java.util.*;

public class EnumProp<E extends Enum<E>> {
    private final String key;
    private final Class<E> enumClass;

    private final EnumMap<E, BlockProp> map;
    private final Map<String, E> inverseMap = new HashMap<>();
    private final List<E> options = new ArrayList<>();
    private final E def;

    public EnumProp(String propertyKey, Class<E> enumClass) {
        this.key = propertyKey;
        this.enumClass = enumClass;

        this.map = new EnumMap<>(enumClass);
        E[] enumConstants = enumClass.getEnumConstants();
        def = enumConstants[0];
        for (E enumConstant : enumConstants) {
            options.add(enumConstant);
            String propertyValue = enumConstant.name().toLowerCase();
            map.put(enumConstant, new BlockProp(propertyKey, propertyValue));
            inverseMap.put(propertyValue, enumConstant);
        }
    }

    public Class<E> getEnumClass() {
        return enumClass;
    }

    public BlockProp get(E value) {
        return map.get(value);
    }

    public E get(Block block) {
        String property = block.getProperty(key);
        return inverseMap.getOrDefault(property, def);
    }

    public List<E> getOptions() {
        return options;
    }
}
