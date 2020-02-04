package magneto.compiler

import com.tschuchort.compiletesting.SourceFile
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CompileRegistryTest {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    @Ignore
    fun `Empty abstract scope`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Model.kt",
                """
                    package magneto.test
                     
                    data class TypeA(val value: String)
                    data class TypeB(val value: Int)
                """
            ),
            SourceFile.kotlin(
                "magneto_test_TypeA.kt",
                """
                    package magneto.generated.factories
                    import magneto.internal.Factory 
                    import magneto.test.TypeA
                    
                    @Factory(metadata = "\n\u0016\n\u0005value\u0012\rkotlin.String\u0012\u0012magneto.test.TypeA")
                    fun magneto_test_TypeA(value: String) = TypeA(value)
                """
            ),
            SourceFile.kotlin(
                "magneto_test_TypeB.kt",
                """
                    package magneto.generated.factories
                    import magneto.internal.Factory 
                    import magneto.test.TypeB
                    
                    @Factory(metadata = "\n\u0013\n\u0005value\u0012\nkotlin.Int\u0012\u0012magneto.test.TypeB")
                    fun magneto_test_TypeB(value: Int) = TypeB(value)
                """
            ),
            SourceFile.kotlin(
                "test_MagnetoScopeExtension.kt",
                """
                    package magneto.generated.extensions
                    
                    import magneto.internal.ScopeExtension
                    import test.TypeA
                    import test.TypeB
                    
                    @ScopeExtension(metadata =
                        "\n\ntest.Scope\u0012\u0013\n\u0005typeA\u0012\ntest.TypeA\u001a\u0013\n\u0005typeB\u0012\ntest.TypeB")
                    interface test_MagnetoScopeExtension {
                      val typeA: TypeA
                      val typeB: TypeB
                    }
                """
            ),
            SourceFile.kotlin(
                "Registry.kt",
                """
                    package test.main
                    import magneto.Registry
                    
                    @Registry
                    interface MainRegistry
                """
            )
        )

        compilate.assertGeneratedCode(
            """
            """
        )
    }
}
