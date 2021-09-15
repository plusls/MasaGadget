package com.plusls.MasaGadget.util;

import com.google.common.collect.Maps;
import com.plusls.MasaGadget.ModInfo;
import net.fabricmc.mapping.reader.v2.MappingGetter;
import net.fabricmc.mapping.reader.v2.TinyMetadata;
import net.fabricmc.mapping.reader.v2.TinyV2Factory;
import net.fabricmc.mapping.reader.v2.TinyVisitor;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Stack;

// Code from https://github.com/TISUnion/Carpet-TIS-Addition/blob/master/src/main/java/carpettisaddition/utils/deobfuscator/StackTraceDeobfuscator.java
public class YarnUtil {

    private static final String MAPPING_NAME = "yarn-1.15.2+build.17-v2";
    private static final Map<String, String> obfuscateMappings = Maps.newHashMap();
    private static final Map<String, String> deobfuscateMappings = Maps.newHashMap();

    static {
        loadMapping();
    }

    public static void loadMapping() {
        InputStream inputStream = YarnUtil.class.getClassLoader().getResourceAsStream(String.format("assets/%s/%s.tiny", ModInfo.MOD_ID, MAPPING_NAME));
        if (inputStream == null) {
            throw new IllegalStateException("YarnUtil loadMapping fail! Can't open mapping!");
        }

        BufferedReader mappingReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            TinyV2Factory.visit(mappingReader, new MappingVisitor(obfuscateMappings, deobfuscateMappings));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("YarnUtil loadMapping fail!");
        }

        if (obfuscateMappings.isEmpty() || deobfuscateMappings.isEmpty()) {
            throw new IllegalStateException("YarnUtil loadMapping fail! mapping is Empty!");
        }
    }

    private static class MappingVisitor implements TinyVisitor {
        private final Map<String, String> obfuscateMappings;
        private final Map<String, String> deobfuscateMappings;
        private int intermediaryIndex;
        private int namedIndex;
        private Stack<Integer> classIdxStack = new Stack<>();
        private Stack<String> classNameStack = new Stack<>();
        private int currentIdx = 0;

        public MappingVisitor(Map<String, String> obfuscateMappings, Map<String, String> deobfuscateMappings) {
            this.obfuscateMappings = obfuscateMappings;
            this.deobfuscateMappings = deobfuscateMappings;
        }

        private void putMappings(MappingGetter name) {
            String intermediaryName = name.get(this.intermediaryIndex);
            String remappedName = name.get(this.namedIndex);
            putMappings(intermediaryName, remappedName);
        }

        private void putMappings(String intermediaryName, String remappedName) {
            this.deobfuscateMappings.put(intermediaryName, remappedName);
            this.obfuscateMappings.put(remappedName, intermediaryName);
        }

        @Override
        public void start(TinyMetadata metadata) {
            this.intermediaryIndex = metadata.index("intermediary");
            this.namedIndex = metadata.index("named");
        }

        @Override
        public void pushClass(MappingGetter name) {
            String intermediaryName = name.get(this.intermediaryIndex);
            classNameStack.push(intermediaryName);
            classIdxStack.push(currentIdx);
            ++currentIdx;
            this.putMappings(name);
        }

        @Override
        public void pushField(MappingGetter name, String descriptor) {
            ++currentIdx;
            String intermediaryName = String.format("L%s;%s:%s", classNameStack.peek(), name.get(this.intermediaryIndex), descriptor);
            String remappedName = String.format("L%s;%s:%s", classNameStack.peek(), name.get(this.namedIndex), descriptor);
            putMappings(intermediaryName, remappedName);
        }

        @Override
        public void pushMethod(MappingGetter name, String descriptor) {
            ++currentIdx;
            String intermediaryName = String.format("L%s;%s%s", classNameStack.peek(), name.get(this.intermediaryIndex), descriptor);
            String remappedName = String.format("L%s;%s%s", classNameStack.peek(), name.get(this.namedIndex), descriptor);
            putMappings(intermediaryName, remappedName);
        }

        @Override
        public void pushParameter(MappingGetter name, int localVariableIndex) {
            ++currentIdx;
        }

        @Override
        public void pushLocalVariable(MappingGetter name, int localVariableIndex, int localVariableStartOffset, int localVariableTableIndex) {
            ++currentIdx;
        }

        @Override
        public void pushComment(String comment) {
            ++currentIdx;
        }

        @Override
        public void pop(int count) {
            currentIdx -= count;
            if (currentIdx == classIdxStack.peek()) {
                classNameStack.pop();
                classIdxStack.pop();
            }
        }

    }

    @Nullable
    public static String getMinecraftTypeStr(String str, int startIdx) {
        int lIndex = str.indexOf("Lnet/minecraft/", startIdx);
        if (lIndex == -1) {
            return null;
        }
        int rIndex = str.indexOf(";", lIndex);
        assert rIndex != -1;
        return str.substring(lIndex + 1, rIndex);
    }

    public static String obfuscateString(String str) {
        return mapMinecraftType(str, obfuscateMappings);
    }

    public static String mapMinecraftType(String str, Map<String, String> mappings) {
        int nextIdx = -1;
        for (String minecraftTypeStr = getMinecraftTypeStr(str, 0); minecraftTypeStr != null; minecraftTypeStr = getMinecraftTypeStr(str, nextIdx)) {
            nextIdx = str.indexOf(minecraftTypeStr, nextIdx + 1);
            str = str.replace(minecraftTypeStr, mappings.getOrDefault(minecraftTypeStr, minecraftTypeStr));
        }
        str = mappings.getOrDefault(str, str);
        return str;
    }

    public static String deobfuscateString(String str) {
        return mapMinecraftType(str, deobfuscateMappings);
    }
}
