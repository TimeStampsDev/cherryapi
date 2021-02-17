package net.cherryflavor.api.bungee.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * DOES NOT SUPPORT {@link HoverEvent.Action SHOW_ITEM} THIS IS BECAUSE ITEMSTACK IS IN SERVER SIDE, NOT PROXY!
 */
public class HoverComponent {

    public String hoverText;
    public HoverEvent.Action action;

    public HoverComponent(String hoverText) {
        this.hoverText = hoverText;
    }

    /**
     * Shows show
     * @param show
     * @return
     */
    public TextComponent showText(String show) {
        this.action = HoverEvent.Action.SHOW_TEXT;
        TextComponent newHoverEvent = new TextComponent(hoverText);
        newHoverEvent.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(show)));
        return newHoverEvent;
    }

    /**
     * Shows text, and when clicked opens url
     * @param show
     * @param url
     * @return
     */
    public TextComponent showTextAndOpenURL(String show, String url) {
        this.action = HoverEvent.Action.SHOW_TEXT;
        TextComponent newHoverEvent = new ClickComponent(hoverText).openURL(url);
        newHoverEvent.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(show)));
        return newHoverEvent;
    }

    /**
     * Shows show
     * @param show
     * @return
     */
    public TextComponent showText(String... show) {
        this.action = HoverEvent.Action.SHOW_TEXT;
        TextComponent newHoverEvent = new TextComponent(hoverText);
        ComponentBuilder builder = new ComponentBuilder();

        for (String t : show) {
            builder.append(show + "\n");
        }

        newHoverEvent.setHoverEvent(new HoverEvent(action, builder.create()));
        return newHoverEvent;
    }

    /**
     * Shows text, and when clicked opens url
     * @param url
     * @param show
     * @return
     */
    public TextComponent showTextAndOpenURL(String url, String... show) {
        this.action = HoverEvent.Action.SHOW_TEXT;
        TextComponent newHoverEvent = new ClickComponent(hoverText).openURL(url);
        ComponentBuilder builder = new ComponentBuilder();

        for (String t : show) {
            builder.append(show + "\n");
        }

        newHoverEvent.setHoverEvent(new HoverEvent(action, builder.create()));
        return newHoverEvent;
    }

    /**
     * Shows text
     * @param show
     * @return
     */
    public BaseComponent componentShowText(String show) {
        this.action = HoverEvent.Action.SHOW_TEXT;
        ComponentBuilder builder = new ComponentBuilder();
        builder.append(show);
        builder.event(new HoverEvent(action, TextComponent.fromLegacyText(show)));
        return builder.create()[0];
    }

    /**
     * Shows text, opens url
     * @param show
     * @return
     */
    public BaseComponent componentShowTextAndOpenURL(String show, String url) {
        this.action = HoverEvent.Action.SHOW_TEXT;
        ComponentBuilder builder = new ComponentBuilder();
        builder.append(new ClickComponent(hoverText).openURL(url));
        builder.event(new HoverEvent(action, TextComponent.fromLegacyText(show)));
        return builder.create()[0];
    }

    /**
     * Shows text
     * @param show
     * @return
     */
    public BaseComponent componentsShowText(String... show) {
        this.action = HoverEvent.Action.SHOW_TEXT;
        ComponentBuilder builder = new ComponentBuilder(hoverText);
        ComponentBuilder showBuilder = new ComponentBuilder();
        for (String t : show) {
            showBuilder.append(t);
        }
        builder.event(new HoverEvent(action, showBuilder.create()));
        return builder.create()[0];
    }

    /**
     * Shows text
     * @param show
     * @return
     */
    public BaseComponent componentsShowTextAndOpenURL(String url, String... show) {
        this.action = HoverEvent.Action.SHOW_TEXT;
        ComponentBuilder builder = new ComponentBuilder(hoverText);
        ComponentBuilder showBuilder = new ComponentBuilder();
        for (String t : show) {
            showBuilder.append(t);
        }
        builder.event(new HoverEvent(action, showBuilder.create()));
        return builder.create()[0];
    }

}