package com.exampley.instagram.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String = "",
                val displayName: String? = "",
                val imageUrl: String = "") : Parcelable