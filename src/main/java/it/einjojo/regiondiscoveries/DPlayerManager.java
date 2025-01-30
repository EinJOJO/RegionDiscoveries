package it.einjojo.regiondiscoveries;

import me.lucko.helper.mongo.Mongo;
import me.lucko.helper.mongo.external.morphia.query.Query;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;

public class DPlayerManager {

    private final Mongo mongo;

    public DPlayerManager(Mongo mongo) {
        this.mongo = mongo;
    }

    public @Nullable DPlayer loadPlayer(UUID uuid) {
        Query<DPlayer> query = mongo.getMorphiaDatastore().find(DPlayer.class).filter("uniqueId", uuid);
        return query.get();
    }

    public void savePlayer(@Nonnull DPlayer player) {
        mongo.getMorphiaDatastore().save(player);
    }
}
