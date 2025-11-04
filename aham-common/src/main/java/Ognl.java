import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * mybatis的Ognl,不能有包路径
 * @author gaohang on 16/6/16.
 */
public final class Ognl {
    private Ognl() {
    }

    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            return StringUtils.isNotEmpty((CharSequence) obj);
        }
        if (obj instanceof Collection) {
            return !((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return !((Map) obj).isEmpty();
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) != 0;
        }
        return true;
    }

    public static boolean isNumber(Object obj) {
        if (isNotEmpty(obj)) {
            return NumberUtils.isNumber(obj.toString());
        }
        return false;
    }

}