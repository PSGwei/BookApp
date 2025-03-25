package com.plcoding.bookpedia.book.data.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

// BookWordDto: the data type we want to operate
object BookWorkDtoSerializer : KSerializer<BookWorkDto> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        // dynamically to get the class name
        BookWorkDto::class.simpleName!!
    ) {
        // declare the json field element
        element<String?>("description")
    }

    override fun deserialize(decoder: Decoder): BookWorkDto = decoder.decodeStructure(descriptor) {
        var description: String? = null
        while (true) {
            // returns the index of the next field to be decoded, according to the order defined in the SerialDescriptor
            when (val index = decodeElementIndex(descriptor)) {
                0 -> {
                    val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException(
                        "This decode only works with JSON object"
                    )
                    // JsonElement could be a JsonObject, JsonArray, JsonPrimitive, or JsonNull
                    val element: JsonElement = jsonDecoder.decodeJsonElement()
                    // JsonObject: a collection of key-value pairs enclosed in curly braces {}
                    description = if (element is JsonObject) {
                        // deserialize a generic JsonElement (element) into a specific Kotlin type (DescriptionDto)
                        decoder.json.decodeFromJsonElement<DescriptionDto>(
                            element = element,
                            //  obtains the default serializer for the DescriptionDto class.
                            deserializer = DescriptionDto.serializer()
                        ).value
                    } else if (element is JsonPrimitive && element.isString) {
                        // value of String
                        element.content
                    } else {
                        null
                    }
                }
                // there are no more elements to decode
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unexpected index $index")
            }
        }
        return@decodeStructure BookWorkDto(description)
    }

    override fun serialize(encoder: Encoder, value: BookWorkDto) = encoder.encodeStructure(
        descriptor
    ) {
        value.description?.let {
            encodeStringElement(descriptor, 0, it)
        }
    }

}