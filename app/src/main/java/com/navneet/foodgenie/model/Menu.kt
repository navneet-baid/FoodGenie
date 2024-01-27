package com.navneet.foodgenie.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Menu(
    val id: String,
    val name: String,
    val price: String,
    val resId: String
):Parcelable


