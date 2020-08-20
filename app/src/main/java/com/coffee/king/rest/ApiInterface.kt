package com.coffee.king.rest

import com.coffee.king.responseCallback.*
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiInterface {

    @POST("login")
    @FormUrlEncoded
    fun doLogin(@Field("userName") userName: String, @Field("password") password: String): Call<LoginResponse>

    @POST("insert_event")
    @FormUrlEncoded
    fun addEvent(
        @Field("booking_type") booking_type: Int,
        @Field("userId") userId: String,
        @Field("eventTypeId") eventTypeId: String,
        @Field("customerName") customerName: String,
        @Field("customerNumber") customerNumber: String,
        @Field("customerEmail") customerEmail: String,
        @Field("noOfPeople") noOfPeople: String,
        @Field("bookingVenue") bookingVenue: Int,
        @Field("packageType") packageType: Int,
        @Field("dateOfEnquiry") dateOfEnquiry: String,
        @Field("dateOfEvent") dateOfEvent: String,
        @Field("fromTime") fromTime: String,
        @Field("toTime") toTime: String,
        @Field("referenceName") referenceName: String
    ): Call<ResponseStatusCode>

    @POST("update_event")
    @FormUrlEncoded
    fun updateEvent(
        @Field("booking_id") booking_id: String,
        @Field("booking_type") booking_type: Int,
        @Field("userId") userId: String,
        @Field("eventId") eventId: String,
        @Field("customerName") customerName: String,
        @Field("customerNumber") customerNumber: String,
        @Field("customerEmail") customerEmail: String,
        @Field("noOfPeople") noOfPeople: String,
        @Field("bookingVenue") bookingVenue: Int,
        @Field("packageType") packageType: Int,
        @Field("dateOfEnquiry") dateOfEnquiry: String,
        @Field("dateOfEvent") dateOfEvent: String,
        @Field("fromTime") fromTime: String,
        @Field("toTime") toTime: String,
        @Field("referenceName") referenceName: String
    ): Call<ResponseStatusCode>


    @GET("get_event_list")
    fun getEvents(): Call<EventsResponse>

    @GET("get_package_list")
    fun getPackage(): Call<PackageResponse>

    @POST("insert_package")
    @FormUrlEncoded
    fun addPackage(
        @Field("eventId") eventId: String,
        @Field("selectionJson") selectionJson: String,
        @Field("totalCategoryPrice") totalCategoryPrice: Int
    ): Call<ResponseStatusCode>

    @POST("update_package")
    @FormUrlEncoded
    fun updatePackage(
        @Field("eventId") eventId: String,
        @Field("selectionJson") selectionJson: String,
        @Field("totalCategoryPrice") totalCategoryPrice: Int,
        @Field("advancePaid") advancePaidAmount: String
    ): Call<ResponseStatusCode>


    @GET("get_upgrade_event_list")
    fun getUpgradeEventList(): Call<UpgradeEventResponse>

    @POST("insert_upgrade_event")
    @FormUrlEncoded
    fun addUpgradeEvent(
        @Field("eventId") eventId: String,
        @Field("selectionJson") selectionJson: String,
        @Field("totalPrice") totalPrice: String,
        @Field("advancePayment") advancePayment: Int
    ): Call<ResponseStatusCode>

    @POST("insert_upgrade_event")
    @FormUrlEncoded
    fun updateUpgradeEvent(
        @Field("eventId") eventId: String,
        @Field("selectionJson") selectionJson: String,
        @Field("totalPrice") totalPrice: String,
        @Field("advancePayment") advancePayment: Int
    ): Call<ResponseStatusCode>


    @POST("get_couple_package")
    @FormUrlEncoded
    fun getCouplePackage(
        @Field("venueType") venueType: Int
    ): Call<CouplePackageResponse>

    @GET("get_all_couple_package")
    fun getCouplePackage(): Call<CouplePackageResponse>

    @POST("insert_couple_package_master")
    @FormUrlEncoded
    fun addCouplePackageMaster(
        @Field("eventId") eventId: String,
        @Field("selectionJson") selectionJson: String,
        @Field("totalPrice") totalPrice: String,
        @Field("advancePayment") advancePayment: Int
    ): Call<ResponseStatusCode>

    @POST("update_couple_package_master")
    @FormUrlEncoded
    fun updateCouplePackageMaster(
        @Field("eventId") eventId: String,
        @Field("selectionJson") selectionJson: String,
        @Field("totalPrice") totalPrice: String,
        @Field("advancePayment") advancePayment: Int
    ): Call<ResponseStatusCode>

    //TODO Category Operation

    @GET("get_category_list")
    fun getCategoryList(): Call<CategoryResponse>


    @POST("insert_update_category")
    @FormUrlEncoded
    fun addUpdateCategory(
        @Field("userId") userId: String,
        @Field("categoryId") categoryId: String,
        @Field("categoryName") categoryName: String,
        @Field("categoryPrice") categoryPrice: String,
        @Field("maximum") maximum: String
    ): Call<ResponseStatusCode>

    @POST("delete_category")
    @FormUrlEncoded
    fun deleteCategory(@Field("categoryId") categoryId: String): Call<ResponseStatusCode>


    //TODO Food Operation

    @GET("get_food_list")
    fun getFoodList(): Call<FoodResponse>

    @POST("insert_update_food")
    @FormUrlEncoded
    fun addUpdateFood(
        @Field("userId") userId: String,
        @Field("foodId") foodId: String,
        @Field("foodName") foodName: String,
        @Field("categoryId") categoryId: String
    ): Call<ResponseStatusCode>

    @POST("delete_food")
    @FormUrlEncoded
    fun deleteFood(@Field("foodId") foodId: String): Call<ResponseStatusCode>


    //TODO Event Type Operation

    @POST("insert_update_event_type")
    @FormUrlEncoded
    fun addUpdateEvent(
        @Field("userId") userId: String,
        @Field("eventTypeId") eventTypeId: String,
        @Field("eventTypeName") eventTypeName: String
    ): Call<ResponseStatusCode>

    @POST("delete_event_type")
    @FormUrlEncoded
    fun deleteEventType(@Field("eventTypeId") eventTypeId: String): Call<ResponseStatusCode>

    //TODO Upgrade Event Operation


    @POST("insert_update_upgrade_event")
    @FormUrlEncoded
    fun addUpdateUpgradeEvent(
        @Field("userId") userId: String,
        @Field("upgradeEventId") upgradeEventId: String,
        @Field("eventTitle") eventTitle: String,
        @Field("eventPrice") eventPrice: String
    ): Call<ResponseStatusCode>


    @POST("delete_upgrade_event")
    @FormUrlEncoded
    fun deleteUpgradeEvent(@Field("upgradeEventId") upgradeEventId: String): Call<ResponseStatusCode>

    //TODO Couple Package Operation

    @POST("insert_update_couple_package")
    @FormUrlEncoded
    fun addUpdateCouplePackage(
        @Field("userId") userId: String,
        @Field("couplePackageId") couplePackageId: String,
        @Field("packageTitle") packageTitle: String,
        @Field("packagePrice") packagePrice: String,
        @Field("packageType") packageType: String
    ): Call<ResponseStatusCode>

    @POST("delete_couple_package")
    @FormUrlEncoded
    fun deleteCouplePackage(@Field("couplePackageId") couplePackageId: String): Call<ResponseStatusCode>

    //TODO User Operation

    @GET("get_user")
    fun getUser(): Call<UserResponse>

    @POST("delete_user")
    @FormUrlEncoded
    fun deleteUser(@Field("userId") userId: String): Call<ResponseStatusCode>


    @POST("insert_update_user")
    @FormUrlEncoded
    fun addUpdateUser(
        @Field("userId") userId: String,
        @Field("userName") userName: String,
        @Field("userMobile") userMobile: String,
        @Field("userPassword") userPassword: String,
        @Field("userType") userType: String,
        @Field("locationId") locationId: String,
        @Field("active") active: Int
    ): Call<ResponseStatusCode>

    //TODO Calendar Operation

    @GET("get_booking_list")
    fun getBookingEvent(): Call<BookEventResponse>

    @POST("generate_pdf")
    @FormUrlEncoded
    fun generatePdf(
        @Field("bookingId") bookingId: String
    ): Call<ResponseStatusCode>

    @POST("update_status_discount")
    @FormUrlEncoded
    fun updateStatusDiscount(
        @Field("bookingId") bookingId: String,
        @Field("status") status: String,
        @Field("discount") discount: String
    ): Call<ResponseStatusCode>


    @POST("get_dashboard")
    @FormUrlEncoded
    fun getDashboard(@Field("currentDate") currentDate: String): Call<DashboardResponse>

    @POST("send_token")
    @FormUrlEncoded
    fun sendToken(
        @Field("token") token: String,
        @Field("loginId") loginId: String
    ): Call<ResponseStatusCode>

    //ToDo User Type
    @GET("get_user_type")
    fun getUserType(): Call<UserTypeResponse>

    //ToDo User Type End

    //ToDo Check List Manager

    @GET("get_check_list")
    fun getCheckList(): Call<CheckListResponse>

    @POST("insert_update_check_list")
    @FormUrlEncoded
    fun addUpdateCheckList(
        @Field("userId") userId: String,
        @Field("checkListId") checkListId: String,
        @Field("checkListName") checkListName: String
    ): Call<ResponseStatusCode>

    @POST("delete_check_list")
    @FormUrlEncoded
    fun deleteCheckList(@Field("checked_list_id") id: String): Call<ResponseStatusCode>

    @POST("get_manager_check_list")
    @FormUrlEncoded
    fun getManagerCheckList(
        @Field("userId") userId: String,
        @Field("date") date: String
    ): Call<CheckListResponse>

    //ToDo Check List Manager End

    @POST("insert_manager_check_list")
    @FormUrlEncoded
    fun addMangerCheckList(
        @Field("userId") userId: String,
        @Field("selectionJson") selectionJson: String,
        @Field("date") date: String
    ): Call<ResponseStatusCode>


    @GET("get_server_date")
    fun getServerDate(): Call<String>

    @GET("get_cafe_location")
    fun getCafeLocation(): Call<CafeLocationResponse>

    @POST("insert_reminder")
    @FormUrlEncoded
    fun addReminder(
        @Field("userId") userId: String?,
        @Field("reminderDate") reminderDate: String,
        @Field("reminderTime") reminderTime: String,
        @Field("responsibleId") responsibleId: String,
        @Field("notes") notes: String
    ): Call<ResponseStatusCode>

    @POST("get_reminder_by_id")
    @FormUrlEncoded
    fun getReminder(
        @Field("userId") userId: String?
    ): Call<ReminderResponse>

    @POST("insert_negative_feedback")
    @FormUrlEncoded
    fun addNegativeFeedback(
        @Field("userId") userId: String?,
        @Field("issueComplaints") issueComplaints: String,
        @Field("solution") solution: String,
        @Field("responsibleId") responsibleId: String
    ): Call<ResponseStatusCode>


    @POST("get_negative_feedback_by_id")
    @FormUrlEncoded
    fun getNegativeFeedback(
        @Field("userId") userId: String?
    ): Call<NegativeFeedbackResponse>

    @POST("insert_notes")
    @FormUrlEncoded
    fun addNotes(
        @Field("userId") userId: String?,
        @Field("noteTitle") noteTitle: String,
        @Field("noteDesc") noteDesc: String
    ): Call<ResponseStatusCode>

    @POST("update_notes")
    @FormUrlEncoded
    fun updateNotes(
        @Field("userId") userId: String?,
        @Field("noteId") noteId: String,
        @Field("noteTitle") noteTitle: String,
        @Field("noteDesc") noteDesc: String
    ): Call<ResponseStatusCode>

    @POST("get_user_notes")
    @FormUrlEncoded
    fun getNotes(
        @Field("userId") userId: String?
    ): Call<NotesResponse>

    @POST("insert_lost")
    @FormUrlEncoded
    fun addLostItem(
        @Field("userId") userId: String?,
        @Field("itemName") itemName: String,
        @Field("date") date: String,
        @Field("time") time: String,
        @Field("name") name: String,
        @Field("contactNumber") contactNumber: String
    ): Call<ResponseStatusCode>

    @POST("update_found_claim")
    @FormUrlEncoded
    fun addFoundOrClaimItem(
        @Field("userId") userId: String?,
        @Field("lostFoundItemId") lostFoundItemId: String,
        @Field("claimBy") claimBy: String,
        @Field("status") status: String
    ): Call<ResponseStatusCode>

    @GET("get_lost_found")
    fun getLostFound(): Call<LostFoundResponse>

    @POST("insert_damage_destroy")
    @FormUrlEncoded
    fun addDamageDestroy(
        @Field("userId") userId: String?,
        @Field("itemName") itemName: String,
        @Field("reason") reason: String,
        @Field("responsibleName") responsibleName: String,
        @Field("responsibleId") responsibleId: String,
        @Field("inChargeId") inChargeId: String,
        @Field("contactNumber") contactNumber: String
    ): Call<ResponseStatusCode>

    @GET("get_damage_destroy")
    fun getDamageDestroy(): Call<DamageDestroyResponse>

    @GET("get_shift")
    fun getShift(): Call<ShiftResponse>


    @POST("insert_shift")
    @FormUrlEncoded
    fun addShift(
        @Field("addedBy") addedById: String,
            @Field("userId") userId: String,
        @Field("selectionJson") selectionJson: String
    ): Call<ResponseStatusCode>

    @POST("get_user_shift_pdf")
    @FormUrlEncoded
    fun getPdf(
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String): Call<ResponseStatusCode>

    @GET("get_duties")
    fun getDuties(): Call<DutyResponse>

    @POST("insert_duty_chart")
    @FormUrlEncoded
    fun addDutyChart(
        @Field("addedBy") addedById: String,
        @Field("userId") userId: String,
        @Field("monthNumber") monthNumber: Int,
        @Field("yearNumber") yearNumber: Int,
        @Field("selectionJson") selectionJson: String
    ): Call<ResponseStatusCode>


    @POST("get_duty_chart_pdf")
    @FormUrlEncoded
    fun getDutyChartPdf(
        @Field("month") month: Int,
        @Field("year") year: Int,
        @Field("monthName") monthName: String
    ): Call<ResponseStatusCode>

    @POST("insert_a_shift")
    @FormUrlEncoded
    fun addAShift(
        @Field("userId") userId: String,
        @Field("shiftName") shiftName: String,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String
    ): Call<ResponseStatusCode>

    @POST("delete_a_shift")
    @FormUrlEncoded
    fun deleteShift(@Field("shiftId") foodId: String): Call<ResponseStatusCode>

    @POST("get_assign_duty")
    @FormUrlEncoded
    fun getDutyList(
        @Field("userId") userId: String?
    ): Call<AssignDutyResponse>

    @POST("get_daily_check_list_master")
    @FormUrlEncoded
    fun getCheckListMaster(
        @Field("checkListType") checkListType: String?
    ): Call<DailyCheckListResponse>

    @POST("get_daily_user_check_list_master")
    @FormUrlEncoded
    fun getUserCheckListMaster(
        @Field("checkListType") checkListType: String?
    ): Call<DailyCheckListResponse>

    @POST("insert_daily_check_list")
    @FormUrlEncoded
    fun addDailyCheckList(
        @Field("userId") userId: String,
        @Field("selectionJson") selectionJson: String
    ): Call<ResponseStatusCode>

    @POST("insert_daily_grooming_check_list")
    @FormUrlEncoded
    fun addGroomingCheckList(
        @Field("addedBy") addedBy: String,
        @Field("userId") userId: String,
        @Field("selectionJson") selectionJson: String
    ): Call<ResponseStatusCode>


    @GET("get_grooming_checked_user")
    fun getGroomingCheckedUser(): Call<UserResponse>

    @POST("get_my_schedule")
    @FormUrlEncoded
    fun getMySchedule(
        @Field("userId") userId: String,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String): Call<ResponseStatusCode>


    @POST("get_my_duty_pdf")
    @FormUrlEncoded
    fun getMyDutyPdf(
        @Field("userId") userId: String,
        @Field("startDate") startDate: String,
        @Field("endDate") endDate: String): Call<ResponseStatusCode>

}