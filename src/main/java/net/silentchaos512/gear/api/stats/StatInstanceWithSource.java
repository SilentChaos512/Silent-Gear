package net.silentchaos512.gear.api.stats;

public class StatInstanceWithSource extends StatInstance {
    private final String source;

    public StatInstanceWithSource(float value, Operation op, String source) {
        super(value, op, DEFAULT_KEY);
        this.source = source;
    }

    @Override
    public StatInstance copySetValue(float newValue) {
        return withSource(newValue, this.op, this.source);
    }

    @Override
    public String getSource() {
        return source;
    }
}
