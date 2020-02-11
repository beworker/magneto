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
        for (instance in scope.exported) {
            injectInstance(instance, bounds, inners, injectableFactories)
        }
        println("")
    }
}

private fun ProcessEnvironment.injectInstance(
    instance: DependencyType,
    bounds: Map<String, DependencyType>,
    inners: MutableMap<String, DependencyType>,
    injectableFactories: Map<String, InjectableType>,
    instantiations: LinkedList<DependencyType> = LinkedList()
) {
    val typeId = instance.typeId

    val instanceExists = instantiations.any { it.typeId == typeId }
    if (instanceExists) {
        // fixme: throw circular dependency exception
        error("circular dependency: $instantiations")
    }

    instantiations.addLast(instance)

    val bound = bounds[typeId]
    if (bound != null) {
        instantiations.removeLast()
        return
    }

    val inner = inners[typeId]
    if (inner != null) {
        instantiations.removeLast()
        return
    }

    val injectableFactory = injectableFactories[typeId]
    if (injectableFactory != null) {
        for (dependency in injectableFactory.dependencies) {
            injectInstance(dependency, bounds, inners, injectableFactories, instantiations)
        }
        inners[typeId] = instance
        instantiations.removeLast()
        return
    }

    // fixme: throw error, neither bound, nor factory
    error("dependency cannot be found: $instance")
}
