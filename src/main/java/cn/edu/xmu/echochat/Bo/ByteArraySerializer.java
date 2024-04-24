package cn.edu.xmu.echochat.Bo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

public class ByteArraySerializer extends StdSerializer<byte[]> {

    public ByteArraySerializer() {
        super(byte[].class);
    }

    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartArray(); // 开始数组
        for (byte b : value) {
            gen.writeNumber(b & 0xFF); // 写入字节值，使用 & 0xFF 来转换为无符号数
        }
        gen.writeEndArray(); // 结束数组
    }
}