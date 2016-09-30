package me.gking2224.common.utils;

import static java.lang.String.format;

import java.util.Optional;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.slf4j.Logger;
import org.springframework.util.Assert;

public class ObjectUtils {

    private static ConvertUtilsBean converter = new ConvertUtilsBean();
    
    static final Logger logger = org.slf4j.LoggerFactory.getLogger(ObjectUtils.class);

    /**
     * Cast the given object to the given type, or throw an error.
     *  
     * @param obj
     * @param clazz
     * @throws {@link ClassCastException} if <code>obj</code> could not be cast to <code>clazz</code>
     * @return <code>null</code> if <code>obj</code> is <code>null</code>, otherwise return <code>obj</code>
     */
    public static <T> T castObjectOrError(Object obj, Class<T> clazz) {
        if (obj == null) return (T)null;
        return ObjectUtils.castObject(obj, clazz).orElseThrow(
                () -> new ClassCastException(format("Could not cast %s to type %s", obj, clazz)));
    }

    /**
     * Cast the given object to the given type.
     * 
     * @param obj
     * @param clazz
     * @return <code>null</code> if <code>obj</code> is <code>null</code> or cannot be cast, otherwise return <code>obj</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> castObject(Object obj, Class<T> clazz) {
        Assert.notNull(clazz);
        Object val = null;
        try {
            val = (obj == null) ? null : converter.convert(obj, clazz);
        }
        catch (ConversionException e) {
            logger.warn("Object {} cannot be cast to type {}", obj, clazz);
        }
        return Optional.ofNullable((T)val);
    }

}
