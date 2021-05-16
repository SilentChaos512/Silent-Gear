package net.silentchaos512.gear.api.stats;

import net.silentchaos512.gear.api.util.StatGearKey;

public class StatInstanceWithSource extends StatInstance {
    private final String source;

    @Deprecated
    public StatInstanceWithSource(float value, Operation op, String source) {
        this(value, op, DEFAULT_KEY, source);
    }

    public StatInstanceWithSource(float value, Operation op, StatGearKey key, String source) {
        super(value, op, key);
        this.source = source;
    }

    @Override
    public StatInstance copySetValue(float newValue) {
        return withSource(newValue, this.op, this.key, this.source);
    }

    @Override
    public String getSource() {
        return source;
    }
}
