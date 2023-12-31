package com.app.body_manage.ui.measure.list

enum class MeasureType(val title: String, val pagerNum: Int) {
    BODY(title = "体型", pagerNum = 1),
    MEAL(title = "食事", pagerNum = 2),
    COMMON(title = "共通", pagerNum = 0)
}
