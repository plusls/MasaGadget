package com.plusls.MasaGadget.impl.event;

import com.plusls.MasaGadget.api.event.RenderEntityListener;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import top.hendrixshen.magiclib.api.event.Event;

import net.minecraft.world.entity.Entity;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RenderEntityEvent implements Event<RenderEntityListener> {
    private final Entity entity;

    public static RenderEntityEvent create(Entity entity) {
        return new RenderEntityEvent(entity);
    }

    @Override
    public void dispatch(List<RenderEntityListener> listener) {
        for (RenderEntityListener renderEntityListener : listener) {
            renderEntityListener.postRenderEntity(this.entity);
        }
    }

    @Override
    public Class<RenderEntityListener> getListenerType() {
        return RenderEntityListener.class;
    }
}
