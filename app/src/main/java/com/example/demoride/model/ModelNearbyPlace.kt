package com.example.demoride.model
import com.google.gson.annotations.SerializedName


data class ModelNearbyPlace(
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any>?,
    @SerializedName("next_page_token")
    val nextPageToken: String?,
    @SerializedName("results")
    val results: List<ResultR>?,
    @SerializedName("status")
    val status: String?
)

data class ResultR(
    @SerializedName("business_status")
    val businessStatus: String?,
    @SerializedName("geometry")
    val geometry: GeometryR?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("icon_background_color")
    val iconBackgroundColor: String?,
    @SerializedName("icon_mask_base_uri")
    val iconMaskBaseUri: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("opening_hours")
    val openingHours: OpeningHours?,
    @SerializedName("permanently_closed")
    val permanentlyClosed: Boolean?,
    @SerializedName("photos")
    val photos: List<PhotoR>?,
    @SerializedName("place_id")
    val placeId: String?,
    @SerializedName("plus_code")
    val plusCode: PlusCode?,
    @SerializedName("price_level")
    val priceLevel: Int?,
    @SerializedName("rating")
    val rating: Double?,
    @SerializedName("reference")
    val reference: String?,
    @SerializedName("scope")
    val scope: String?,
    @SerializedName("types")
    val types: List<String>?,
    @SerializedName("user_ratings_total")
    val userRatingsTotal: Int?,
    @SerializedName("vicinity")
    val vicinity: String?
)

data class GeometryR(
    @SerializedName("location")
    val location: LocationR?,
    @SerializedName("viewport")
    val viewport: ViewportR?
)

data class OpeningHours(
    @SerializedName("open_now")
    val openNow: Boolean?
)

data class PhotoR(
    @SerializedName("height")
    val height: Int?,
    @SerializedName("html_attributions")
    val htmlAttributions: List<String>?,
    @SerializedName("photo_reference")
    val photoReference: String?,
    @SerializedName("width")
    val width: Int?
)

data class PlusCode(
    @SerializedName("compound_code")
    val compoundCode: String?,
    @SerializedName("global_code")
    val globalCode: String?
)

data class LocationR(
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?
)

data class ViewportR(
    @SerializedName("northeast")
    val northeast: NortheastR?,
    @SerializedName("southwest")
    val southwest: SouthwestR?
)

data class NortheastR(
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?
)

data class SouthwestR(
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?
)