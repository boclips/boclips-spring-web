package com.boclips.web

data class ApiErrorResource(
        val path: String,
        val status: Int,
        val timestamp: String,
        val error: String,
        val message: String
)