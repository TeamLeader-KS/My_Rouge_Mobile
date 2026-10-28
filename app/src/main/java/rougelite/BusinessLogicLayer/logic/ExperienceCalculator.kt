package rougelite.BusinessLogicLayer.logic

import java.io.PrintStream
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToLong

object ExperienceCalculator {
    private const val BASE_EXP: Double = 100.0
    private const val GROWTH_RATE: Double = 1.5 // ← МЕНЯЙТЕ ТОЛЬКО ЭТО

    // Автоматический расчёт (не трогать)
    private const val SUM_DENOM: Double = GROWTH_RATE - 1.0
    private const val INV_BASE: Double = BASE_EXP / SUM_DENOM // для обратной формулы

    @JvmStatic
    fun expNextRaw(level: Int): Double {
        if (level < 1) return 0.0
        return BASE_EXP * GROWTH_RATE.pow(level - 1.0)
    }

    @JvmStatic
    fun expNext(level: Int): Long = expNextRaw(level).roundToLong()

    @JvmStatic
    fun totalExpRaw(level: Int): Double {
        if (level <= 1) return 0.0
        return INV_BASE * (GROWTH_RATE.pow(level - 1.0) - 1.0)
    }

    @JvmStatic
    fun totalExp(level: Int): Long = totalExpRaw(level).roundToLong()

    @JvmStatic
    fun getLevelByExp(currentExp: Long): Int {
        if (currentExp <= 0L) return 1

        val value = currentExp.toDouble() / INV_BASE + 1.0
        val logVal = ln(value)
        val logRate = ln(GROWTH_RATE)

        var level = floor(logVal / logRate).toInt() + 1

        if (level < 1) level = 1
        if (totalExp(level) > currentExp && level > 1) level--

        return level
    }

    @JvmStatic
    fun expToReach(fromLevel: Int, toLevel: Int): Long {
        if (toLevel <= fromLevel) return 0L
        return totalExp(toLevel) - totalExp(fromLevel)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            System.setOut(PrintStream(System.out, true, "UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
        }


        for (level in 1..50) {
            System.out.printf("%3d | %13d | %11d%n", level, expNext(level), totalExp(level))
        }

        // Тест обратной формулы

        for (level in 1..20) {
            val exp = totalExp(level)
            val calculatedLevel = getLevelByExp(exp)

        }
    }
}