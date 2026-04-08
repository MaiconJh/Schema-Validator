package com.maiconjh.schemacr.schemes;

import java.nio.file.Path;

/**
 * Metadata describing how a schema entered the runtime registry.
 */
public record RegisteredSchemaMetadata(String name,
                                       SchemaRegistrationSource source,
                                       Path sourcePath,
                                       long registeredAtEpochMillis) {
}
