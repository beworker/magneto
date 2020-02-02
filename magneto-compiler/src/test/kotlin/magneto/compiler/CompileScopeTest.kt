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
}
