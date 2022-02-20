package com.art.maker.ad

interface AdReporter {
    fun onAdImpression(oid: String, format: Int)
}

internal fun getReportedAdID(adId: String): String {
    return adId.substring(adId.length - 4)
}