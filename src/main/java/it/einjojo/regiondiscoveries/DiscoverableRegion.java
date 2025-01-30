package it.einjojo.regiondiscoveries;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public record DiscoverableRegion(String worldGuardId, Component displayTitle, Component description) {

    @Override
    public Component displayTitle() {
        return displayTitle.colorIfAbsent(NamedTextColor.YELLOW);
    }


}
