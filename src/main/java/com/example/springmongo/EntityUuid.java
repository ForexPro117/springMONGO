package com.example.springmongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.HashIndexed;

import java.util.UUID;

public abstract class EntityUuid {
    @Id
    private UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
