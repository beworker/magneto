
| Feature | Magneto | Magnet | Dagger |
| ----- | ------ | ------ | ------ | 
| Hierarchy | DAG of Scopes | Tree of Scopes | DAG of Components |
| Dependency validation | Strong compile-time | Weak compile-time, Strong runtime | Strong compile-time |
| Entities | Scopes | Scopes | Components, Subcomponents, Modules | 
| Annotations | Injectable | Instance | Provides, Binds, Inject | 
| Provide into set | Global (between modules) | Global (between modules) | In single Component |
| Provide into map | No | No | In single Component | 
| Code generation | apt | apt | apt |
| Disposable entities | Scopes | Scopes | No |
| Full generics support | Yes | No | Yes |
| Support default arguments | Yes | Yes (w/ JvmOverloads) | No | 
| Support nullable types | Yes | Yes | No |
| Target Language | Kotlin | Java, Kotlin | Java |
