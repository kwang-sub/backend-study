package ch03.item10

class PhoneNumber private constructor(
    private val areaCode: Short,
    private val prefix: Short,
    private val lineNum: Short,
) {
    constructor(areaCode: Int, prefix: Int, lineNum: Int): this(
        areaCode = rangeCheck(areaCode, 999, "지역코드"),
        prefix = rangeCheck(prefix, 999, "지역코드"),
        lineNum = rangeCheck(lineNum, 999, "지역코드"),
    )

    override fun equals(other: Any?): Boolean {
        if (other == this) return true
        if (other !is PhoneNumber) return false
        return other.lineNum == lineNum && other.prefix == prefix && other.areaCode == areaCode
    }

    override fun hashCode(): Int {
        var result = areaCode.hashCode()
        result = 31 * result + prefix.hashCode()
        result = 31 * result + lineNum.hashCode()
        return result
    }

    companion object {
        fun rangeCheck(value : Int, max: Int, arg: String): Short {
            if (value !in 0..max) throw IllegalArgumentException("$arg: $value")
            return value.toShort()
        }
    }

}