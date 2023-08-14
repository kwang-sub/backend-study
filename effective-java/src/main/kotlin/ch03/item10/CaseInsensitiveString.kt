package ch03.item10

class CaseInsensitiveString(
    val s: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is CaseInsensitiveString)
            return s.equals(other.s, true)

        if (other is String)
            return s.equals(other, true)

        return false
    }

}