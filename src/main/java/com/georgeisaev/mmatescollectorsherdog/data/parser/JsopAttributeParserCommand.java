package com.georgeisaev.mmatescollectorsherdog.data.parser;

import com.georgeisaev.mmatescollectorsherdog.domain.Fighter;
import com.georgeisaev.mmatescollectorsherdog.utils.DateTimeUtils;
import jakarta.annotation.Nonnull;
import org.jsoup.nodes.Document;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.function.Function;

import static com.georgeisaev.mmatescollectorsherdog.utils.ParserUtils.extractAndSet;
import static org.springframework.util.ReflectionUtils.findMethod;
import static org.springframework.util.ReflectionUtils.invokeMethod;

/**
*
 * @param <T>
*/
public interface JsopAttributeParserCommand<T> extends AttributeParserCommand<T, Document> {

  default void parse(final Document source, final T object) {
    parseAttribute(source, object, String.class, s -> s);
    parseAttribute(source, object, Integer.class, Integer::parseInt);
    parseAttribute(source, object, LocalDate.class, DateTimeUtils::parseDate);
  }

  String getSelector();


  /**
   * Parses and sets the attribute
   * @param source the source document from which attribute should be parsed
   * @param object the object which attribute should be parsed
   * @param attributeType attribute type class
   * @param valueConverter converter from string value into attribute type
   * @param <A> attribute type
   */
  default  <A> void parseAttribute(
          @Nonnull final Document source,
          @Nonnull final T object,
          @Nonnull final Class<A> attributeType,
          @Nonnull final Function<String, A> valueConverter) {
    final Method setter = findMethod(object.getClass(), getAttribute(), attributeType);
    if (setter == null) return;
    extractAndSet(
            source,
            getSelector(),
            getAttribute(),
            s -> invokeMethod(setter, object, valueConverter.apply(s)),
            elements -> elements.get(0).html());
  }

}
