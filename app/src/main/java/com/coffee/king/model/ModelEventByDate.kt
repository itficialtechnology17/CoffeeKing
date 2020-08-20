package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelEventByDate : Serializable {

    @SerializedName("bookingId")
    var bookingId=""

    @SerializedName("bookingType")
    var bookingType=""

    @SerializedName("bookingVenue")
    var bookingVenue=""

    @SerializedName("eventTypeId")
    var eventTypeId=""

    @SerializedName("eventTitle")
    var eventTitle=""

    @SerializedName("bookingPackageType")
    var bookingPackageType=""

    @SerializedName("noOfPeople")
    var noOfPeople=""

    @SerializedName("customerName")
    var customerName=""

    @SerializedName("customerNumber")
    var customerNumber=""

    @SerializedName("customerEmail")
    var customerEmail=""

    @SerializedName("dateOfEnquiry")
    var dateOfEnquiry=""

    @SerializedName("dateOfEvent")
    var dateOfEvent=""

    @SerializedName("startTime")
    var startTime=""

    @SerializedName("endTime")
    var endTime=""

    @SerializedName("status")
    var status=""

    @SerializedName("referenceName")
    var referenceName=""

    @SerializedName("addedBy")
    var addedBy=""

    @SerializedName("userId")
    var userId=""

    @SerializedName("discountAmount")
    var discountAmount=""

    @SerializedName("totalAmount")
    var totalAmount=""

    @SerializedName("advancePaid")
    var advancePaidAmount=""

    @SerializedName("package")
    var arrOfPackage=ArrayList<ModelPackage>()

    @SerializedName("upgrade_event")
    var arrOfUpgradeEvent=ArrayList<ModelUpgradeEvent>()

    @SerializedName("couple_package")
    var arrOfCouplePackage=ArrayList<ModelCouplePackage>()
}