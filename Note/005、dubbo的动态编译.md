#### compiler.compile(code, classLoader)源码解析
```text
compiler.compile(code, classLoader);
    -->loader.getDefaultExtension();
    -->JavassistCompiler.compile(code, classLoader);
        -->doCompile(className, code);【参考CompilerByJavassist实现】
            
```
#### getDefaultExtension();
 * ![avatar](./pic/005_dubbo.png) 
 * ![avatar](./pic/016_dubbo.png) 
 * ![avatar](./pic/017_dubbo.png) 
 * ![avatar](./pic/018_dubbo.png)