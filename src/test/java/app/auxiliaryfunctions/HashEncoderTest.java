package app.auxiliaryfunctions;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HashEncoderTest {
    @Test
    public void encodeTest() {
        HashEncoder hashEncoder = new HashEncoder();
        assertThat(hashEncoder.encode("test")).isEqualTo(hashEncoder.encode("test"));
    }
}