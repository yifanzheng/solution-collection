package top.yifan.masterha.util;

import io.etcd.jetcd.ByteSequence;

import java.nio.charset.StandardCharsets;

/**
 * @author star
 */
public class ByteSequenceUtil {

    private ByteSequenceUtil() {}

    public static ByteSequence fromString(String source) {
        return ByteSequence.from(source, StandardCharsets.UTF_8);
    }
}
