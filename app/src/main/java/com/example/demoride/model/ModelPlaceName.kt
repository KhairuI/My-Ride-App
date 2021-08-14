package com.example.demoride.model
import com.google.gson.annotations.SerializedName


data class ModelPlaceName(
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any>?,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("status")
    val status: String?
)

data class Result(
    @SerializedName("address_components")
    val addressComponents: List<AddressComponent>?,
    @SerializedName("adr_address")
    val adrAddress: String?,
    @SerializedName("formatted_address")
    val formattedAddress: String?,
    @SerializedName("geometry")
    val geometry: Geometry?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("icon_background_color")
    val iconBackgroundColor: String?,
    @SerializedName("icon_mask_base_uri")
    val iconMaskBaseUri: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photos")
    val photos: List<Photo>?,
    @SerializedName("place_id")
    val placeId: String?,
    @SerializedName("reference")
    val reference: String?,
    @SerializedName("types")
    val types: List<String>?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("utc_offset")
    val utcOffset: Int?,
    @SerializedName("vicinity")
    val vicinity: String?
)

data class AddressComponent(
    @SerializedName("long_name")
    val longName: String?,
    @SerializedName("short_name")
    val shortName: String?,
    @SerializedName("types")
    val types: List<String>?
)

data class Geometry(
    @SerializedName("location")
    val location: Location?,
    @SerializedName("viewport")
    val viewport: Viewport?
)

data class Photo(
    @SerializedName("height")
    val height: Int?,
    @SerializedName("html_attributions")
    val htmlAttributions: List<String>?,
    @SerializedName("photo_reference")
    val photoReference: String?,
    @SerializedName("width")
    val width: Int?
)

data class Location(
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?
)

data class Viewport(
    @SerializedName("northeast")
    val northeast: Northeast?,
    @SerializedName("southwest")
    val southwest: Southwest?
)

data class Northeast(
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?
)

data class Southwest(
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?
)