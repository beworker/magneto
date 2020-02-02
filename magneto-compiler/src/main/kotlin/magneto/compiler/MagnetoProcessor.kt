package magneto.compiler

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import magneto.Injectable
import magneto.Registry
import magneto.Scope
import magneto.compiler.annotations.injectable.generateInjectables
import magneto.compiler.annotations.injectable.getInjectableTypes
import magneto.compiler.annotations.registry.getRegistryType
import magneto.compiler.annotations.scope.generateScopes
import magneto.compiler.annotations.scope.getScopeTypes
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

class MagnetoProcessor : AbstractProcessor() {

    private lateinit var messager: Messager
    private lateinit var elements: Elements
    private lateinit var types: Types
    private lateinit var filer: Filer

    override fun getSupportedAnnotationTypes(): Set<String> =
        setOf(
            Injectable::class.java.canonicalName,
            Scope::class.java.canonicalName,
            Registry::class.java.canonicalName
        )

    override fun getSupportedSourceVersion(): SourceVersion? =
        SourceVersion.latest()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        elements = processingEnv.elementUtils
        types = processingEnv.typeUtils
        filer = processingEnv.filer
    }

    @KotlinPoetMetadataPreview
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val previousRoundFailed = roundEnv.errorRaised()
        if (previousRoundFailed) return false

        val env = ProcessEnvironment(messager, elements, types, filer, roundEnv)
        try {
            val injectables = env.getInjectableTypes()
            env.generateInjectables(injectables)

            val scopes = env.getScopeTypes()
            env.generateScopes(scopes)

            val registry = env.getRegistryType()
            if (registry != null) {
                //env.generateRegistry(registry)
            }

        } catch (e: CompilationException) {
            env.messager.printMessage(Diagnostic.Kind.ERROR, e.message, e.element)
        }
        return false
    }
}
