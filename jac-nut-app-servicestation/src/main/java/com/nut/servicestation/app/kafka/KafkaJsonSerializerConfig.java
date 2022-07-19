/*
package com.nut.servicestation.app.kafka;


import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;

public class KafkaJsonSerializerConfig extends AbstractConfig {
    public static final String JSON_INDENT_OUTPUT = "json.indent.output";
    public static final boolean JSON_INDENT_OUTPUT_DEFAULT = false;
    public static final String JSON_INDENT_OUTPUT_DOC = "Whether JSON output should be indented (\"pretty-printed\")";
    private static ConfigDef config;

    public KafkaJsonSerializerConfig(Map<?, ?> props) {
        super(config, props);
    }

    static {
        config = (new ConfigDef()).define("json.indent.output", Type.BOOLEAN, false, Importance.LOW, "Whether JSON output should be indented (\"pretty-printed\")");
    }
}

*/
