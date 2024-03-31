package com.mrmelon54.OmniPlay.event.events.common;

import com.mrmelon54.OmniPlay.event.EventResult;
import com.mrmelon54.OmniPlay.event.EventWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.TextFilter;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import remapped.architectury.event.Event;

import java.util.List;

public interface ChatEvent {
    interface Inner extends remapped.architectury.event.events.common.ChatEvent {
    }

    #if MC_VER > MC_1_18_2
    Event<Decorate> DECORATE = EventWrapper.of(Inner.DECORATE, decorate -> (serverPlayer, chatComponent) -> decorate.decorate(serverPlayer, new ChatComponent() {
        @Override
        public Component get() {
            return chatComponent.get();
        }

        @Override
        public void set(Component component) {
            chatComponent.set(component);
        }
    }));
    #endif
    Event<Received> RECEIVED = resolveReceivedEvent();

    static Event<Received> resolveReceivedEvent() {
        #if MC_VER < MC_1_19_2
        return EventWrapper.of(Inner.SERVER, received -> (serverPlayer, filteredText, component) -> EventResult.map(received.received(serverPlayer, new Component() {
            @Override
            public @NotNull Style getStyle() {
                return component.getFiltered().getStyle();
            }

            @Override
            public @NotNull String getContents() {
                return component.getFiltered().getContents();
            }

            @Override
            public @NotNull List<Component> getSiblings() {
                return component.getFiltered().getSiblings();
            }

            @Override
            public @NotNull MutableComponent plainCopy() {
                return component.getFiltered().plainCopy();
            }

            @Override
            public @NotNull MutableComponent copy() {
                return component.getFiltered().copy();
            }

            @Override
            public @NotNull FormattedCharSequence getVisualOrderText() {
                return component.getFiltered().getVisualOrderText();
            }
        })));
        #else
        return EventWrapper.of(Inner.RECEIVED, received -> (serverPlayer, component) -> EventResult.map(received.received(serverPlayer, component)));
        #endif
    }

    @FunctionalInterface
    interface Decorate {
        void decorate(@Nullable ServerPlayer player, ChatComponent component);
    }

    @FunctionalInterface
    interface Received {
        EventResult received(@Nullable ServerPlayer player, Component component);
    }

    interface ChatComponent {
        Component get();

        void set(Component component);
    }
}