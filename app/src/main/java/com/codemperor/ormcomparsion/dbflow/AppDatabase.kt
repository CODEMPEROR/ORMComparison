package com.codemperor.ormcomparsion.dbflow

import com.raizlabs.android.dbflow.annotation.Database

/**
 * Created by feng on 2017/6/21.
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
object AppDatabase {
    const val NAME = "_dbflow"
    const val VERSION = 1
}