package net.silentchaos512.gear.util;

/**
 * Thrown on the client when attempting to connect to a server running an incompatible version
 */
public class MismatchedVersionsException extends RuntimeException {
    public MismatchedVersionsException(String msg) {
        super(msg);
    }
}
