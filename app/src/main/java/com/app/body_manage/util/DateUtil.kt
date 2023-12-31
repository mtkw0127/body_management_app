package com.app.body_manage.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month

class DateUtil {
    // 休み種別
    enum class DateType {
        // 休日
        HOLIDAY {},

        // 代休
        COMPENSATION {},

        // 平日
        WEEKDAY {},
    }

    companion object {

        /**
         * 引数のLocalDateTimeをMM/DDの文字列に変換して返却
         */
        fun localDateConvertMMDD(localDateTime: LocalDateTime): String {
            return "${localDateTime.monthValue}/${localDateTime.dayOfMonth}"
        }

        /**
         * 引数のLocalDateをYYYY年MM月DD日(曜日)のフォーマットに変換して返却.
         */
        fun localDateConvertJapaneseFormatYearMonthDay(localDate: LocalDate): String {
            val japaneseDayOfWeek = when (localDate.dayOfWeek) {
                DayOfWeek.SUNDAY -> "日"
                DayOfWeek.MONDAY -> "月"
                DayOfWeek.TUESDAY -> "火"
                DayOfWeek.WEDNESDAY -> "水"
                DayOfWeek.THURSDAY -> "木"
                DayOfWeek.FRIDAY -> "金"
                DayOfWeek.SATURDAY -> "土"
            }
            return "${localDate.year}年${localDate.monthValue}月${localDate.dayOfMonth}日(${japaneseDayOfWeek})"
        }

        /**
         * 引数のLocalDateをYYYY年MM月DD日(曜日)のフォーマットに変換して返却.
         */
        fun localDateConvertMonthDay(localDate: LocalDate): String {
            return "${localDate.monthValue}月${localDate.dayOfMonth}日"
        }

        /**
         * 引数のLocalDateをYYYY年MM月のフォーマットに変換して返却.
         */
        fun localDateConvertJapaneseFormatYearMonth(localDate: LocalDate): String {
            return "${localDate.year}年${localDate.monthValue}月"
        }

        fun localDateConvertLocalTimeDateToTime(localDateTime: LocalDateTime): String {
            val hour = String.format("%02d", localDateTime.hour)
            val minute = String.format("%02d", localDateTime.minute)
            return "${hour}時${minute}分"
        }

        fun localDateTimeToJapanese(localDateTime: LocalDateTime): String {
            val dateStr = localDateConvertJapaneseFormatYearMonthDay(localDateTime.toLocalDate())
            val hourTime = "${localDateTime.hour}時${localDateTime.minute}分"
            return "$dateStr$hourTime"
        }

        /**
         * 引数の年月日が祝日か判定する.
         */
        fun isHoliday(localDate: LocalDate): DateType {
            when (localDate.dayOfWeek) {
                DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> return DateType.HOLIDAY
                else -> {}
            }
            // 1月
            if (localDate.month == Month.JANUARY) {
                when (localDate.dayOfMonth) {
                    // 元旦, 成人の日
                    1, 10 -> return DateType.HOLIDAY
                }
            }
            // 2月
            if (localDate.month == Month.FEBRUARY) {
                when (localDate.dayOfMonth) {
                    // 建国記念日, 天皇誕生日
                    11, 23 -> return DateType.HOLIDAY
                }
            }
            // 3月
            if (localDate.month == Month.MARCH) {
                when (localDate.dayOfMonth) {
                    // 春分の日
                    21 -> return DateType.HOLIDAY
                }
            }
            // 4月
            if (localDate.month == Month.APRIL) {
                when (localDate.dayOfMonth) {
                    // 昭和の日
                    29 -> return DateType.HOLIDAY
                }
            }
            // 5月
            if (localDate.month == Month.MAY) {
                when (localDate.dayOfMonth) {
                    // みどりの日, こどもの日
                    4, 5 -> return DateType.HOLIDAY
                }
            }
            // 7月
            if (localDate.month == Month.JULY) {
                when (localDate.dayOfMonth) {
                    // 海の日
                    // TODO: 第3月曜判定
                    18 -> return DateType.HOLIDAY
                }
            }
            // 8月
            if (localDate.month == Month.AUGUST) {
                when (localDate.dayOfMonth) {
                    // 山の日
                    11 -> return DateType.HOLIDAY
                }
            }
            // 9月
            if (localDate.month == Month.SEPTEMBER) {
                when (localDate.dayOfMonth) {
                    // 敬老の日, 秋分の日
                    // TODO: 第3月曜日判定
                    19, 23 -> return DateType.HOLIDAY
                }
            }
            // 10月
            if (localDate.month == Month.OCTOBER) {
                when (localDate.dayOfMonth) {
                    // スポーツの日
                    // TODO: 第2月曜日
                    10 -> return DateType.HOLIDAY
                }
            }
            // 11月
            if (localDate.month == Month.NOVEMBER) {
                when (localDate.dayOfMonth) {
                    // 文化の日, 勤労感謝の日
                    3, 23 -> return DateType.HOLIDAY
                }
            }
            // 12月
            if (localDate.month == Month.DECEMBER) {
                //　天皇誕生日(2018まで）
                if (localDate.year <= 2018 && localDate.dayOfMonth == 23) {
                    return DateType.HOLIDAY
                }
            }
            return DateType.WEEKDAY
        }
    }
}