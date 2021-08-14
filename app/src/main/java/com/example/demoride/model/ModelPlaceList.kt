package com.example.demoride.model
import com.google.gson.annotations.SerializedName


data class ModelPlaceList(
    @SerializedName("predictions")
    val predictions: MutableList<Prediction>?,
    @SerializedName("status")
    val status: String?
)

data class Prediction(
    @SerializedName("description")
    val description: String?,
    @SerializedName("matched_substrings")
    val matchedSubstrings: List<MatchedSubstring>?,
    @SerializedName("place_id")
    val placeId: String?,
    @SerializedName("reference")
    val reference: String?,
    @SerializedName("structured_formatting")
    val structuredFormatting: StructuredFormatting?,
    @SerializedName("terms")
    val terms: List<Term>?,
    @SerializedName("types")
    val types: List<String>?
)

data class MatchedSubstring(
    @SerializedName("length")
    val length: Int?,
    @SerializedName("offset")
    val offset: Int?
)

data class StructuredFormatting(
    @SerializedName("main_text")
    val mainText: String?,
    @SerializedName("main_text_matched_substrings")
    val mainTextMatchedSubstrings: List<MainTextMatchedSubstring>?,
    @SerializedName("secondary_text")
    val secondaryText: String?
)

data class Term(
    @SerializedName("offset")
    val offset: Int?,
    @SerializedName("value")
    val value: String?
)

data class MainTextMatchedSubstring(
    @SerializedName("length")
    val length: Int?,
    @SerializedName("offset")
    val offset: Int?
)