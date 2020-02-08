package magneto.compiler.analyzer

import magneto.compiler.ProcessEnvironment
import magneto.compiler.model.DependencyType
import magneto.compiler.model.InjectableType
import magneto.compiler.model.RegistryType
import java.util.*

internal fun ProcessEnvironment.analyzeRegistry(registry: RegistryType) {

    val injectableFactories = registry.injectables.associateBy { it.typeId }
    for (scope in registry.scopes) {
        val bounds = scope.bound.associateBy { it.typeId }
        val inners = mutableMapOf<String, DependencyType>()
        val instantiations: LinkedList<DependencyType> = LinkedList()
        for (instance in scope.exported) {
            injectInstance(instance, bounds, inners, injectableFactories, instantiations)
        }
    }

    // todo analyse scopes and dependencies here
    //  - dependency completeness
    //  - circular dependencies
}

private fun ProcessEnvironment.injectInstance(
    instance: DependencyType,
    bounds: Map<String, DependencyType>,
    inners: MutableMap<String, DependencyType>,
    injectableFactories: Map<String, InjectableType>,
    instantiations: LinkedList<DependencyType>
) {
    val typeId = instance.typeId

    val bound = bounds[typeId]
    if (bound != null) {
        // fixme: initialize from bound
        // val boundType: BoundType get() = boundType
        return
    }

    val inner = inners[typeId]
    if (inner != null) {
        // fixme: initialize from inner
        // val innerType: InnerType get() = innerType
        return
    }

    val injectableFactory = injectableFactories[typeId]
    if (injectableFactory != null) {
        val instanceExists = instantiations.any { it.typeId == typeId }
        if (instanceExists) {
            // fixme: throw circular dependency exception
            return
        }

        instantiations.addLast(instance)
        for (dependency in injectableFactory.dependencies) {
            injectInstance(dependency, bounds, inners, injectableFactories, instantiations)
            inners[typeId] = dependency
        }
        instantiations.removeLast()
        return
    }

    // fixme: throw error, neither bound, nor factory
}
