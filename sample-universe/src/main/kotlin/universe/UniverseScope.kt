package universe

import magneto.Injectable
import magneto.Scope

@Scope
internal abstract class UniverseScope(
    // bound
    private val language: String
) {
    // not exported to child scopes
    internal abstract val name: InternalType

    // exported
    abstract val constellations: Set<Constellation>
    abstract val typeA: TypeA
}

@Scope
abstract class StellarScope(
    val distance: Int
) {
    abstract val name: String
}

@Injectable(type = InternalType::class)
internal class InternalType(
    private val typeA: TypeA,
    private val typeB: TypeB
)

@Injectable(type = TypeA::class)
class TypeA(val typeC: TypeC?)

@Injectable(type = TypeB::class)
class TypeB

@Injectable(type = TypeC::class)
class TypeC(val typeB: TypeB)
