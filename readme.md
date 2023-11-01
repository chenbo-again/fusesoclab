## spinalhdl + fusesoc example(fork from SpinalTemplateSbt)
使用方法:

1. 创建 FuseSocGeneratorBuilder 实例, 它是所有功能的代理
2. 使用 buildScript 方法得到当前 Component 的 core 文件模板
3. 利用模板创建 core 文件, 添加 tagets, 如 myTopLevel.core, 添加到当前工作目录 `fusesoc library add spinal_example ./`
4. 添加 generator `fusesoc library add spinal_generator ./spinal_generator`(之后这个generator可以上传到 github,用 fusesoc 的 provider 功能访问)
5. 创建主函数, 运行 run 方法
6. `fusesoc run --target=xxx compname`