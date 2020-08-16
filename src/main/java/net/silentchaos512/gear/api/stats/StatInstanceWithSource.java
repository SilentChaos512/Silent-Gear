package net.silentchaos512.gear.api.stats;

public class StatInstanceWithSource extends StatInstance {
    private final String source;

    public StatInstanceWithSource(float value, Operation op, String source) {
        super(value, op);
        this.source = source;
    }

    @Override
    public StatInstance copySetValue(float newValue) {
        return of(newValue, this.op, this.source);
    }

    @Override
    public String getSource() {
        return source;
    }
}
