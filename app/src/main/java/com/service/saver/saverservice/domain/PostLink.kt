package com.service.saver.saverservice.domain

import java.io.Serializable
import java.util.*

class PostLink : Serializable {
    var id: Number = -1;
    var url: String = ""
    var username: String = ""
    var createDate: Date = Date()
    var save: Boolean = false
    var filedir: String = ""


    constructor(id: Number, url: String, username: String, createDate: Date, save: Boolean) {
        this.id = id
        this.url = url
        this.username = username
        this.createDate = createDate
        this.save = save
    }

    constructor()


    companion object {
        const val TABLE_NAME = "postlink";
        const val TABLE_CREATE = "create table  ${TABLE_NAME}(id INTEGER PRIMARY KEY, url text UNIQUE, username text, createDate DATETIME  , save integer)"
        const val DROP_TABLE = "drop table if exists ${TABLE_NAME}"
        const val ALTER_TABLE = "ALTER TABLE ${TABLE_NAME} ADD COLUMN filedir INTEGER DEFAULT ''"
    }
}