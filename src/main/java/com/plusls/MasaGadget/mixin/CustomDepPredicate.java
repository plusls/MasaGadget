package com.plusls.MasaGadget.mixin;

import org.objectweb.asm.tree.ClassNode;

import java.util.function.Predicate;

// make java compiler happy
public interface CustomDepPredicate extends Predicate<ClassNode> {
}
