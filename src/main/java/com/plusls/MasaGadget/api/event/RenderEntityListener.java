package com.plusls.MasaGadget.api.event;

import top.hendrixshen.magiclib.api.event.Listener;

import net.minecraft.world.entity.Entity;

public interface RenderEntityListener extends Listener {
    void postRenderEntity(Entity entity);
}
