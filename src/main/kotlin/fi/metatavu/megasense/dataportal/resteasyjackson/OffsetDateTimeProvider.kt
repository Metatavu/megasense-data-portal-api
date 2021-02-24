package fi.metatavu.megasense.dataportal.resteasyjackson

import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.reflect.Type
import java.time.OffsetDateTime
import javax.ws.rs.ext.ParamConverter
import javax.ws.rs.ext.ParamConverterProvider

import javax.ws.rs.ext.Provider

/**
 * ParamConverterProvider implementation which allows translating OffsetDateTime
 * see https://github.com/quarkusio/quarkus/issues/5860
 *
 * @constructor Create empty Offset date time provider
 */
@Provider
internal class OffsetDateTimeProvider : ParamConverterProvider {

    override fun <T : Any?> getConverter(
        rawType: Class<T>?,
        genericType: Type?,
        annotations: Array<out Annotation>?
    ): ParamConverter<T>? {
        return if (rawType == OffsetDateTime::class.java) {
            object : ParamConverter<T> {
                val mapper = ObjectMapper().also { MyObjectMapperCustomizer().customize(it) }
                override fun toString(value: T?): String {
                    return mapper.writeValueAsString(value)
                }

                override fun fromString(value: String?): T {
                    return mapper.readValue(value, OffsetDateTime::class.java) as T
                }
            }
        } else null
    }

}
