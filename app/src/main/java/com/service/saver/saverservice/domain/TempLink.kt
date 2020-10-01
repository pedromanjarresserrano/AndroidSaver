package com.service.saver.saverservice.domain

import java.io.Serializable
import java.util.*

class TempLink : Serializable {
    var id: Long = -1;
    var url: String = ""
    var createDate: Date = Date()

    constructor(id: Long,  url: String, createDate: Date) {
        this.id = id
        this.url = url
        this.createDate = createDate
    }

    constructor()


    override fun toString(): String {
        return "$url"
    }


    companion object {
        const val TABLE_NAME = "templink";
        const val TABLE_CREATE = "create table ${TABLE_NAME}(id INTEGER PRIMARY KEY,  url text UNIQUE,  createDate date)"
        const val DROP_TABLE = "drop table if exists ${TABLE_NAME}"
    }
}