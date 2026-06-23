package dev.ctsetera.ikaranpu.domain.model

data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int,
    val preRelease: List<String> = emptyList(),
) : Comparable<SemanticVersion> {

    override fun compareTo(other: SemanticVersion): Int {
        compareValuesBy(
            this,
            other,
            SemanticVersion::major,
            SemanticVersion::minor,
            SemanticVersion::patch,
        ).takeIf { it != 0 }?.let { return it }

        if (preRelease.isEmpty() && other.preRelease.isNotEmpty()) return 1
        if (preRelease.isNotEmpty() && other.preRelease.isEmpty()) return -1

        val maxSize = maxOf(preRelease.size, other.preRelease.size)
        for (index in 0 until maxSize) {
            val left = preRelease.getOrNull(index) ?: return -1
            val right = other.preRelease.getOrNull(index) ?: return 1
            val result = comparePreReleasePart(left, right)
            if (result != 0) return result
        }

        return 0
    }

    companion object {
        fun parse(value: String): SemanticVersion? {
            val normalized = value.removePrefix("v").removePrefix("V")
            val match = Regex("""^(\d+)\.(\d+)\.(\d+)(?:-([0-9A-Za-z.-]+))?$""")
                .matchEntire(normalized)
                ?: return null

            return SemanticVersion(
                major = match.groupValues[1].toInt(),
                minor = match.groupValues[2].toInt(),
                patch = match.groupValues[3].toInt(),
                preRelease = match.groupValues
                    .getOrNull(4)
                    ?.takeIf { it.isNotBlank() }
                    ?.split(".")
                    ?: emptyList(),
            )
        }

        private fun comparePreReleasePart(left: String, right: String): Int {
            val leftNumber = left.toIntOrNull()
            val rightNumber = right.toIntOrNull()

            return when {
                leftNumber != null && rightNumber != null -> leftNumber.compareTo(rightNumber)
                leftNumber != null -> -1
                rightNumber != null -> 1
                else -> left.compareTo(right)
            }
        }
    }
}
