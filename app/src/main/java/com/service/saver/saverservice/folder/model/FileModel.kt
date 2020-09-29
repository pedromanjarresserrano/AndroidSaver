package com.service.saver.saverservice.folder.model

import java.io.Serializable

class FileModel : Serializable {
    var id: Long? = null
    var name: String? = null
    var filepath: String? = null
    var isFolder: Boolean = false
    var parent: String = ""
    constructor(id: Long?, name: String, filepath: String, b: Boolean, parent: String) {
        this.id = id
        this.name = name
        this.filepath = filepath
        this.isFolder = b
        this.parent= parent
    }
}
