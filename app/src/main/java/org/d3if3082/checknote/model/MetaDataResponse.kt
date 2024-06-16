package org.d3if3082.checknote.model

data class MetaDataResponse(
    val name: String,
    val bucket: String,
    val generation: String,
    val metageneration: String,
    val contentType: String,
    val timeCreated: String,
    val updated: String,
    val storageClass: String,
    val size: String,
    val md5Hash: String,
    val contentEncoding: String,
    val contentDisposition: String,
    val crc32c: String,
    val etag: String,
    val downloadTokens: String
)
