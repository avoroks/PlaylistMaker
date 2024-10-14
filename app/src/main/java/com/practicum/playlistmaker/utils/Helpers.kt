package com.practicum.playlistmaker.utils

import android.content.Context
import android.view.View
import com.google.android.material.internal.ViewUtils.dpToPx

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun Context.dpToPx(dp: Int) = dpToPx(this, dp).toInt()