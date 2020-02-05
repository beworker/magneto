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
    fun `Injectable with no type`() {
        val compilate = temporaryFolder.compile(
            SourceFile.kotlin(
                "Feature.kt",
                """
                    package test
                    import magneto.Injectable
                    
                    @Injectable
                    class Feature {
                        fun perform() { }
                    }
                """
            )
        )

        compilate.assertGeneratedCode(
            """
                package magneto.generated.factories

                import magneto.internal.InjectableFactory
                import test.Feature

                @InjectableFactory(metadata = "\n\u000ctest.Feature\u0012\u000ctest.Feature")
                fun test_Feature(): Feature = Feature()
                
            """
        )
    }

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
                package magneto.generated.factories

                import magneto.internal.InjectableFactory
                import test.DefaultFeature
                import test.Feature

                @InjectableFactory(metadata = "\n\u0013test.DefaultFeature\u0012\u000ctest.Feature")
                fun test_DefaultFeature(): Feature = DefaultFeature()
                
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
                package magneto.generated.factories

                import kotlin.String
                import magneto.internal.InjectableFactory
                import test.DefaultFeature
                import test.Feature

                @InjectableFactory(metadata =
                    "\n\u0013test.DefaultFeature\u0012\u000ctest.Feature\u001a\u0015\n\u0004task\u0012\rkotlin.String")
                fun test_DefaultFeature(task: String): Feature = DefaultFeature(task)
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
                package magneto.generated.factories

                import kotlin.Int
                import kotlin.String
                import magneto.internal.InjectableFactory
                import test.DefaultFeature
                import test.Feature

                @InjectableFactory(metadata =
                    "\n\u0013test.DefaultFeature\u0012\u000ctest.Feature\u001a\u0015\n\u0004task\u0012\rkotlin.String\u001a\u0015\n\u0007counter\u0012\nkotlin.Int")
                fun test_DefaultFeature(task: String, counter: Int): Feature = DefaultFeature(task, counter)
            """
        )
    }
}