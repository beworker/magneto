package magneto.compiler.analyzer

import magneto.compiler.ProcessEnvironment
import magneto.compiler.model.*
import java.util.*

internal fun ProcessEnvironment.analyzeRegistry(registry: RegistryType): AnalyzedRegistryType {
    val injectableFactories = registry.injectables.associateBy { it.typeId }
    val analyzedScopes = registry.scopes.map { scope ->
        val bounds = scope.bound.associateBy { it.typeId }
        val inners = mutableMapOf<String, DependencyType>()
        val properties = mutableListOf<AnalyzedDependencyType>()
        for (instance in scope.exported) {
            injectInstance(
                instance,
                bounds,
                inners,
                injectableFactories,
                properties,
                Visibility.Public
            )
        }
        AnalyzedScopeType(scope.typeName, scope.bound, properties)
    }
    return AnalyzedRegistryType(registry.injectables, analyzedScopes)
}

private fun ProcessEnvironment.injectInstance(
    instance: DependencyType,
    bounds: Map<String, DependencyType>,
    inners: MutableMap<String, DependencyType>,
    injectableFactories: Map<String, InjectableType>,
    properties: MutableList<AnalyzedDependencyType>,
    visibility: Visibility,
    instantiations: LinkedList<DependencyType> = LinkedList()
) {
    val typeId = instance.typeId

    val instanceExists = instantiations.any { it.typeId == typeId }
    if (instanceExists) {
        // fixme: report circular dependency exception
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
            injectInstance(
                dependency,
                bounds,
                inners,
                injectableFactories,
                properties,
                Visibility.Private,
                instantiations
            )
        }
        inners[typeId] = instance
        properties += AnalyzedDependencyType(
            name = instance.name,
            typeName = instance.typeName,
            injectable = injectableFactory,
            visibility = visibility
        )
        instantiations.removeLast()
        return
    }

    // fixme: report error, neither bound, nor factory
    error("dependency cannot be found: $instance")
}
