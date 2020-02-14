package universe

interface Constellation {
    val abbreviation: String
    val name: String
    val stars: Set<String>
    val isVisible: Boolean
}
