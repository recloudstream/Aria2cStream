package com.lagradost.fetchbutton

import android.content.Context
import android.content.SharedPreferences
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.lagradost.fetchbutton.Aria2Save.setKey

object Aria2Save {
    val mapper = JsonMapper.builder().addModule(KotlinModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build()!!

    const val KEY = "Aria2Save"

    fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
    }

    fun <T> Context.setKey(id: Long, value: T) {
        try {
            val editor: SharedPreferences.Editor = getPref(this).edit()
            editor.putString(id.toString(), mapper.writeValueAsString(value))
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inline fun <reified T : Any> Context.getKey(id : Long) : T? {
        try {
            val str = getPref(this).getString(id.toString(), null) ?: return null
            return mapper.readValue(str)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}