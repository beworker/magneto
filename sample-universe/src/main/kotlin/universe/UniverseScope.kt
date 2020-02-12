package universe

import magneto.Injectable
import magneto.Scope

@Scope
abstract class UniverseScope(
    // bound
    val language: String
) {
    // provided, not exported to child scopes
    //internal abstract val name: InternalType

    // exported
    //abstract val constellations: Set<Constellation>
    abstract val typeA: TypeA
    abstract val typeZ: TypeZ
}

//@TestProfile
abstract class TestUniverseScope(
    language: String
) : UniverseScope(language) {

    //@TestProfile
    abstract override val typeZ: TypeZ
}

@Scope
abstract class StellarScope(
    val distance: Int
) {
    //abstract val name: String
}

@Injectable
class InternalType(
    private val typeA: TypeA,
    private val typeB: TypeB
)

@Injectable
class TypeA(val typeC: TypeC?)

@Injectable
class TypeB

@Injectable
class TypeC(val typeB: TypeB)

interface TypeZ

@Injectable(type = TypeZ::class)
class DefaultTypeZ : TypeZ

//@TestProfile
//@Injectable(type = TypeZ::class)
//class TestTypeZ : TypeZ

//@TestProfile
//@Injectable(type = TypeZ::class)
//fun createTypeZ(): TypeZ = TestTypeZ()