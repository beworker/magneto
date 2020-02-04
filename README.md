
| Feature | Dagger | Magneto | Magnet | 
| ----- | ------ | ------ | ------ | 
| Hierarchy | DAG of Components | DAG of Scopes | Tree of Scopes | 
| Dependency validation | Strong compile-time | Strong compile-time | Weak compile-time, Strong runtime | 
| Entities | Components, Subcomponents, Modules | Scopes | Scopes |
| Annotations | Provides, Binds, Inject | Injectable | Instance |
| Provide into set | Within Component | Global (between modules) | Global (between modules) | 
| Provide into map | Within Component | No | No |
| Reflection-less | Yes | Yes | No (Single class) |
| Disposable | No | Scopes | Scopes |
| Full generics support | Yes | Yes | No |
| Support default arguments | No | Yes | Yes (w/ JvmOverloads) |
| Target Language | Java | Kotlin | Java, Kotlin |
