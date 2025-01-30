package it.einjojo.regiondiscoveries;

import me.lucko.helper.mongo.external.morphia.annotations.Entity;
import me.lucko.helper.mongo.external.morphia.annotations.Id;
import me.lucko.helper.mongo.external.morphia.annotations.Indexed;
import me.lucko.helper.mongo.external.morphia.annotations.Property;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(value = "dplayers", noClassnameStored = true)
public class DPlayer {

    public DPlayer() {

    }

    public DPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Indexed
    @Id
    private UUID uniqueId;
    @Property("discovered")
    private Set<String> discoveredRegionsIds = new HashSet<>();

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Set<String> getDiscoveredRegionsIds() {
        return discoveredRegionsIds;
    }

    public void addDiscoveredRegion(String regionId) {
        discoveredRegionsIds.add(regionId);

    }
}
