package magneto.compiler

import com.tschuchort.compiletesting.SourceFile
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CompileInjectableTest {

    @Rule
    @JvmField
    var temporaryFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun `Injectable with interface type, no parameters`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Feature.kt",
                """
                    package test 
                    interface Feature {
                        fun perform()
                    }
                """
            ),
            SourceFile.kotlin(
                "DefaultFeature.kt",
                """
                    package test
                    import magneto.Injectable
                    
                    @Injectable(type = Feature::class)
                    internal class DefaultFeature : Feature {
                        override fun perform() {}
                    }
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package magneto.factories

                import magneto.internal.Factory
                import test.DefaultFeature
                import test.Feature

                @Factory
                fun create_test_DefaultFeature(): Feature = DefaultFeature()
                
            """
        )
    }

    @Test
    fun `Injectable with interface type, single parameter`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Feature.kt",
                """
                    package test 
                    interface Feature {
                        fun perform()
                    }
                """
            ),
            SourceFile.kotlin(
                "DefaultFeature.kt",
                """
                    package test
                    import magneto.Injectable
                    
                    @Injectable(type = Feature::class)
                    internal class DefaultFeature(val task: String) : Feature {
                        override fun perform() {}
                    }
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package magneto.factories

                import kotlin.String
                import magneto.internal.Factory
                import test.DefaultFeature
                import test.Feature

                @Factory
                fun create_test_DefaultFeature(task: String): Feature = DefaultFeature(task)
            """
        )
    }

    @Test
    fun `Injectable with interface type, many parameters`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Feature.kt",
                """
                    package test 
                    interface Feature {
                        fun perform()
                    }
                """
            ),
            SourceFile.kotlin(
                "DefaultFeature.kt",
                """
                    package test
                    import magneto.Injectable
                    
                    @Injectable(type = Feature::class)
                    internal class DefaultFeature(val task: String, val counter: Int) : Feature {
                        override fun perform() {}
                    }
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package magneto.factories

                import kotlin.Int
                import kotlin.String
                import magneto.internal.Factory
                import test.DefaultFeature
                import test.Feature

                @Factory
                fun create_test_DefaultFeature(task: String, counter: Int): Feature = DefaultFeature(task, counter)
            """
        )
    }
}