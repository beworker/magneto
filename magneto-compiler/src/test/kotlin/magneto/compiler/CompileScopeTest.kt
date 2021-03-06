package magneto.compiler

import com.tschuchort.compiletesting.SourceFile
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CompileScopeTest {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    @Ignore
    fun `Empty abstract scope`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Feature.kt",
                """
                    package test
                    import magneto.Scope 
                    
                    @Scope
                    abstract class Scope()
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package test
                
                class MagnetoScope : Scope()
                
            """
        )
    }

    @Test
    @Ignore
    fun `Abstract scope, simple type property`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Feature.kt",
                """
                    package test
                    import magneto.Scope 
                    
                    @Scope
                    abstract class Scope() {
                        abstract val value: String
                    }
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package test
                
                import kotlin.String
                import magneto.factories.scope_test_MagnetoScope
                import magneto.internal.Magneto
                
                class MagnetoScope : Scope() {
                  private val _factory: scope_test_MagnetoScope by lazy {
                      Magneto.getFactory(scope_test_MagnetoScope::class) }

                  override val value: String by lazy { _factory.value(this) }
                }
                
            """,
            """
                package magneto.factories
                
                import kotlin.String
                import test.Scope
                
                interface scope_test_MagnetoScope {
                  fun value(scope: Scope): String
                }
                
            """
        )
    }

    @Test
    @Ignore
    fun `Abstract scope, parametrized type property`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Feature.kt",
                """
                    package test
                    import magneto.Scope 
                    
                    @Scope
                    abstract class Scope() {
                        abstract val value: Set<String>
                    }
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package test
                
                import kotlin.String
                import kotlin.collections.Set
                import magneto.factories.scope_test_MagnetoScope
                import magneto.internal.Magneto
                
                class MagnetoScope : Scope() {
                  private val _factory: scope_test_MagnetoScope by lazy {
                      Magneto.getFactory(scope_test_MagnetoScope::class) }

                  override val value: Set<String> by lazy { _factory.value(this) }
                }
                
            """,
            """
                package magneto.factories
                
                import kotlin.String
                import kotlin.collections.Set
                import test.Scope
                
                interface scope_test_MagnetoScope {
                  fun value(scope: Scope): Set<String>
                }
                
            """
        )
    }

    @Test
    fun `Scope with parameters`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Scope.kt",
                """
                    package test
                    
                    import magneto.Scope
                     
                    class TypeA
                    class TypeB
                    class TypeC
                    class TypeD
                    
                    @Scope
                    abstract class Scope(
                        val typeA: TypeA,
                        internal val typeB: TypeB
                    ) {
                        abstract val typeC: TypeC
                        internal abstract val typeD: TypeD
                    }
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package test
                
                import magneto.generated.extensions.test_ScopeExtension
                import magneto.internal.Magneto
                
                class MagnetoScope(
                  typeA: TypeA,
                  typeB: TypeB
                ) : Scope(typeA, typeB) {
                  private val _extension: test_ScopeExtension =
                      Magneto.createScopeExtension(test_ScopeExtension::class,typeA,typeB)
                
                  override val typeC: TypeC
                    get() = _extension.typeC
                
                  override val typeD: TypeD
                    get() = _extension.typeD
                }
                
            """,
            """
                package magneto.generated.extensions
                
                import magneto.internal.ScopeExtension
                import test.TypeA
                import test.TypeB
                import test.TypeC
                import test.TypeD
                
                @ScopeExtension(metadata =
                    "\n\ntest.Scope\u0012\u0013\n\u0005typeA\u0012\ntest.TypeA\u0012\u0013\n\u0005typeB\u0012\ntest.TypeB\u001a\u0013\n\u0005typeC\u0012\ntest.TypeC\u001a\u0013\n\u0005typeD\u0012\ntest.TypeD")
                interface test_ScopeExtension {
                  val typeA: TypeA
                
                  val typeB: TypeB
                
                  val typeC: TypeC
                
                  val typeD: TypeD
                }
                
            """
        )
    }
}
