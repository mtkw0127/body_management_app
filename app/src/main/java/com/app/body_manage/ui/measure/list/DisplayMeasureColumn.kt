package com.app.body_manage.ui.measure.list

enum class DisplayMeasureColumn(val measureType: MeasureType, val display: String) {
    TIME(MeasureType.COMMON, "計測時刻"),
    WEIGHT(MeasureType.BODY, "体重"),
    FAT(MeasureType.BODY, "体脂肪率"),
    PHOTO_NUM(MeasureType.COMMON, "写真枚数"),
    DETAIL(MeasureType.COMMON, "詳細"),
}