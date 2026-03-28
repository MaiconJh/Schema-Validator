package com.maiconjh.schemacr.schemes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Registry of supported JSON Schema keywords in Schema-Validator.
 * 
 * <p>This class provides a central registry of all keywords that are currently
 * supported by the parser and validator implementations. It also serves as the
 * source of truth for documentation alignment.</p>
 * 
 * <p>Usage:</p>
 * <pre>
 * SupportedKeywordsRegistry registry = new SupportedKeywordsRegistry();
 * boolean isSupported = registry.isKeywordSupported("type");
 * {@code Set<String>} allKeywords = registry.getAllSupportedKeywords();
 * </pre>
 * 
 * @see <a href="https://json-schema.org/draft/2020-12/json-schema-core.html">JSON Schema Specification</a>
 */
public class SupportedKeywordsRegistry {

    /**
     * Supported keywords organized by category.
     */
    public enum KeywordCategory {
        TYPE_KEYWORDS("Type Keywords"),
        OBJECT_KEYWORDS("Object Keywords"),
        ARRAY_KEYWORDS("Array Keywords"),
        STRING_KEYWORDS("String Keywords"),
        NUMBER_KEYWORDS("Number Keywords"),
        COMPOSITION_KEYWORDS("Composition Keywords"),
        CONDITIONAL_KEYWORDS("Conditional Keywords"),
        REFERENCE_KEYWORDS("Reference Keywords"),
        FORMAT_KEYWORDS("Format Keywords"),
        CONSTRAINT_KEYWORDS("Constraint Keywords");

        private final String displayName;

        KeywordCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final Set<String> supportedKeywords;
    private final Logger logger;

    /**
     * Creates a new SupportedKeywordsRegistry with default supported keywords.
     */
    public SupportedKeywordsRegistry() {
        this(null);
    }

    /**
     * Creates a new SupportedKeywordsRegistry with a custom logger.
     * @param logger the logger to use, or null for no logging
     */
    public SupportedKeywordsRegistry(Logger logger) {
        this.logger = logger;
        this.supportedKeywords = initializeSupportedKeywords();
    }

    /**
     * Initializes the set of supported keywords.
     * 
     * @return an unmodifiable set of supported keyword strings
     */
    private Set<String> initializeSupportedKeywords() {
        Set<String> keywords = new HashSet<>();

        // === TYPE KEYWORDS ===
        keywords.add("type");

        // === OBJECT KEYWORDS ===
        keywords.add("properties");
        keywords.add("patternProperties");
        keywords.add("additionalProperties");
        keywords.add("required");
        keywords.add("minProperties");
        keywords.add("maxProperties");
        keywords.add("propertyNames");
        keywords.add("dependentRequired");
        keywords.add("dependentSchemas");
        keywords.add("unevaluatedProperties");
        keywords.add("dependencies"); // Legacy alias

        // === ARRAY KEYWORDS ===
        keywords.add("items");
        keywords.add("minItems");
        keywords.add("maxItems");
        keywords.add("uniqueItems");
        keywords.add("prefixItems");
        keywords.add("contains");
        keywords.add("minContains");
        keywords.add("maxContains");
        keywords.add("unevaluatedItems");
        keywords.add("additionalItems"); // Legacy keyword

        // === STRING KEYWORDS ===
        keywords.add("minLength");
        keywords.add("maxLength");
        keywords.add("pattern");
        keywords.add("format");

        // === NUMBER KEYWORDS ===
        keywords.add("minimum");
        keywords.add("maximum");
        keywords.add("exclusiveMinimum");
        keywords.add("exclusiveMaximum");
        keywords.add("multipleOf");

        // === COMPOSITION KEYWORDS ===
        keywords.add("allOf");
        keywords.add("anyOf");
        keywords.add("oneOf");
        keywords.add("not");

        // === CONDITIONAL KEYWORDS ===
        keywords.add("if");
        keywords.add("then");
        keywords.add("else");

        // === REFERENCE KEYWORDS ===
        keywords.add("$ref");
        keywords.add("$dynamicRef");
        keywords.add("$defs");
        keywords.add("definitions"); // Legacy alias
        keywords.add("$schema");
        keywords.add("$id");

        // === CONSTRAINT KEYWORDS ===
        keywords.add("enum");
        keywords.add("const");

        // === METADATA KEYWORDS ===
        keywords.add("title");
        keywords.add("description");
        keywords.add("default");
        keywords.add("examples");
        keywords.add("readOnly");
        keywords.add("writeOnly");
        keywords.add("deprecated");
        keywords.add("$comment");
        keywords.add("comment"); // Legacy alias
        keywords.add("contentEncoding");
        keywords.add("contentMediaType");
        keywords.add("contentSchema");

        // === VOCABULARY KEYWORDS ===
        keywords.add("$vocabulary");
        keywords.add("$dynamicAnchor");

        return Collections.unmodifiableSet(keywords);
    }

    /**
     * Checks if a keyword is supported by the validator.
     * 
     * @param keyword the keyword to check
     * @return true if the keyword is supported, false otherwise
     */
    public boolean isKeywordSupported(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return false;
        }
        return supportedKeywords.contains(keyword);
    }

    /**
     * Returns all supported keywords.
     * 
     * @return an unmodifiable set of all supported keywords
     */
    public Set<String> getAllSupportedKeywords() {
        return supportedKeywords;
    }

    /**
     * Returns the number of supported keywords.
     * 
     * @return the count of supported keywords
     */
    public int getSupportedKeywordCount() {
        return supportedKeywords.size();
    }

    /**
     * Checks if a keyword is supported and logs a warning if not.
     * 
     * @param keyword the keyword to check
     * @return true if supported, false otherwise
     */
    public boolean checkAndLogUnsupported(String keyword) {
        boolean supported = isKeywordSupported(keyword);
        if (!supported && logger != null) {
            logger.warning("Unsupported keyword detected: '" + keyword + 
                          "'. This keyword will be ignored during validation.");
        }
        return supported;
    }

    /**
     * Checks multiple keywords and returns the set of unsupported ones.
     * 
     * @param keywords the keywords to check
     * @return a set of unsupported keywords
     */
    public Set<String> findUnsupportedKeywords(Iterable<String> keywords) {
        Set<String> unsupported = new HashSet<>();
        for (String keyword : keywords) {
            if (!isKeywordSupported(keyword)) {
                unsupported.add(keyword);
            }
        }
        return unsupported;
    }

    /**
     * Returns keywords by category.
     * 
     * @param category the category to filter by
     * @return set of keywords in that category
     */
    public Set<String> getKeywordsByCategory(KeywordCategory category) {
        Set<String> result = new HashSet<>();
        
        switch (category) {
            case TYPE_KEYWORDS -> {
                result.add("type");
            }
            case OBJECT_KEYWORDS -> {
                result.add("properties");
                result.add("patternProperties");
                result.add("additionalProperties");
                result.add("required");
                result.add("minProperties");
                result.add("maxProperties");
                result.add("propertyNames");
                result.add("dependentRequired");
                result.add("dependentSchemas");
                result.add("unevaluatedProperties");
                result.add("dependencies");
            }
            case ARRAY_KEYWORDS -> {
                result.add("items");
                result.add("minItems");
                result.add("maxItems");
                result.add("uniqueItems");
                result.add("prefixItems");
                result.add("contains");
                result.add("minContains");
                result.add("maxContains");
                result.add("unevaluatedItems");
                result.add("additionalItems");
            }
            case STRING_KEYWORDS -> {
                result.add("minLength");
                result.add("maxLength");
                result.add("pattern");
                result.add("format");
            }
            case NUMBER_KEYWORDS -> {
                result.add("minimum");
                result.add("maximum");
                result.add("exclusiveMinimum");
                result.add("exclusiveMaximum");
                result.add("multipleOf");
            }
            case COMPOSITION_KEYWORDS -> {
                result.add("allOf");
                result.add("anyOf");
                result.add("oneOf");
                result.add("not");
            }
            case CONDITIONAL_KEYWORDS -> {
                result.add("if");
                result.add("then");
                result.add("else");
            }
            case REFERENCE_KEYWORDS -> {
                result.add("$ref");
                result.add("$dynamicRef");
                result.add("$defs");
                result.add("definitions");
                result.add("$schema");
                result.add("$id");
            }
            case FORMAT_KEYWORDS -> {
                result.add("format");
            }
            case CONSTRAINT_KEYWORDS -> {
                result.add("enum");
                result.add("const");
                result.add("contentEncoding");
                result.add("contentMediaType");
                result.add("contentSchema");
                result.add("default");
                result.add("examples");
                result.add("deprecated");
            }
        }
        
        // Filter to only return keywords that are actually supported
        result.retainAll(supportedKeywords);
        return Collections.unmodifiableSet(result);
    }

    /**
     * Gets a markdown-formatted string of all supported keywords.
     * Useful for documentation generation.
     * 
     * @return markdown string
     */
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Supported JSON Schema Keywords\n\n");
        sb.append("This document lists all JSON Schema keywords currently supported by Schema-Validator.\n\n");
        
        for (KeywordCategory category : KeywordCategory.values()) {
            Set<String> keywords = getKeywordsByCategory(category);
            if (!keywords.isEmpty()) {
                sb.append("## ").append(category.getDisplayName()).append("\n\n");
                sb.append("- `").append(String.join("`, `", keywords)).append("`\n\n");
            }
        }
        
        sb.append("---\n\n");
        sb.append("*Generated by SupportedKeywordsRegistry - Total: ").append(getSupportedKeywordCount()).append(" keywords*\n");
        
        return sb.toString();
    }

    /**
     * Gets a JSON-formatted string of all supported keywords.
     * Useful for API documentation and testing.
     * 
     * @return JSON string
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"supportedKeywords\": [\n");
        
        int i = 0;
        for (String keyword : supportedKeywords) {
            sb.append("    \"").append(keyword).append("\"");
            if (++i < supportedKeywords.size()) {
                sb.append(",");
            }
            sb.append("\n");
        }
        
        sb.append("  ],\n");
        sb.append("  \"totalCount\": ").append(supportedKeywords.size()).append(",\n");
        sb.append("  \"generatedAt\": \"").append(java.time.Instant.now()).append("\"\n");
        sb.append("}\n");
        
        return sb.toString();
    }

    @Override
    public String toString() {
        return "SupportedKeywordsRegistry{count=" + supportedKeywords.size() + 
               ", keywords=" + supportedKeywords + "}";
    }
}
