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
                    package test
                     
                    data class Type1(val value: String)
                    data class Type2(val value: String)
                """
            ),
            SourceFile.kotlin(
                "create_Type1.kt",
                """
                    package solomon.factories
                    import solomon.internal.Factory 
                    import test.Type1
                    
                    @Factory
                    fun create_Test_Type1() = Type1("type1")
                """
            ),
            SourceFile.kotlin(
                "create_Type2.kt",
                """
                    package solomon.factories
                    import solomon.internal.Factory 
                    import test.Type2
                    
                    @Factory
                    fun create_Test_Type2() = Type2("type2")
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package solomon.internal
                
                class Registry {
                  
                }
            """
        )
    }
}
