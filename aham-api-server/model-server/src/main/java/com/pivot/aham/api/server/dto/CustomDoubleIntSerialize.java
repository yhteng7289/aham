package com.pivot.aham.api.server.dto;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;

/**
 * Created by luyang.li on 19/1/22.
 */
public class CustomDoubleIntSerialize implements ObjectSerializer {

    private DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        serializer.write(df.format(object));
    }
}
