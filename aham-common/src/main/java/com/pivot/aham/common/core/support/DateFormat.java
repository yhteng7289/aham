package com.pivot.aham.common.core.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pivot.aham.common.core.util.DateUtils;

/**
 * 时间格式化扩展
 *
 * @author addison
 * @since 2017年6月30日 下午7:40:00
 */
@SuppressWarnings("serial")
public class DateFormat extends SimpleDateFormat {

    public DateFormat(String pattern) {
        super(pattern);
    }

    @Override
    public Date parse(String source) throws ParseException {
        return DateUtils.parseDate(source);
    }
}
