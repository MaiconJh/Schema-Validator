package com.yourname.schemacr.validation;

import com.yourname.schemacr.schemes.Schema;

import java.util.List;

/**
 * Base validator contract.
 */
public interface Validator {

    /**
     * Validate data against a schema.
     *
     * @param data      runtime data node
     * @param schema    target schema node
     * @param path      logical node path (e.g. $.user.name)
     * @param parentKey optional parent key for context
     * @return validation errors; empty list means success
     */
    List<ValidationError> validate(Object data, Schema schema, String path, String parentKey);
}
