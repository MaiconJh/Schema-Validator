package com.yourname.schemacr.schemes;

import java.util.Collections;
import java.util.Map;

/**
 * Basic schema model used by validators.
 *
 * <p>This intentionally small model is enough for skeleton work. Extend it with constraints
 * (required fields, min/max, enum values, regex, etc.) as your addon evolves.</p>
 */
public class Schema {

    private final String name;
    private final SchemaType type;
    private final Map<String, Schema> properties;
    private final Schema itemSchema;

    public Schema(String name, SchemaType type, Map<String, Schema> properties, Schema itemSchema) {
        this.name = name;
        this.type = type;
        this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
        this.itemSchema = itemSchema;
    }

    public String getName() {
        return name;
    }

    public SchemaType getType() {
        return type;
    }

    public Map<String, Schema> getProperties() {
        return properties;
    }

    public Schema getItemSchema() {
        return itemSchema;
    }
}
