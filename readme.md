## spinalhdl + fusesoc example(fork from SpinalTemplateSbt)
usage:
```bash
# add this library to current workspace, name it spinalhdlexample
fusesoc library add spinalhdlexample .
# run target sim. in fact, it will not execute, because the lack of tb
fusesoc run --target=sim test:demo:spinalhdlexample:0
```