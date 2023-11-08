# 生成MIPS设计

## 之前的事

实际上，我们不得不先恶补一下MIPS的基础知识

[MIPS 通用寄存器](https://e-mailky.github.io/2017-02-14-mips-registers)

[MIPS 中的子程序和函数 - Sylvain's Blog](https://valeeraz.github.io/2019/09/30/subroutine-functions-mips/)

MIPS中寄存器及其功能如下

| REGISTER  | NAME      | USAGE                                                 |
| --------- | --------- | ----------------------------------------------------- |
| `$0`      | `$zero`   | 常量0(constant value 0)                                 |
| `$1`      | `$at`     | 保留给汇编器(Reserved for assembler)                        |
| `$2-$3`   | `$v0-$v1` | 函数调用返回值(values for results and expression evaluation) |
| `$4-$7`   | `$a0-$a3` | 函数调用参数(arguments)                                     |
| `$8-$15`  | `$t0-$t7` | 暂时的(或随便用的)                                            |
| `$16-$23` | `$s0-$s7` | 保存的(或如果用，需要SAVE/RESTORE的)(saved)                      |
| `$24-$25` | `$t8-$t9` | 暂时的(或随便用的)                                            |
| `$28`     | `$gp`     | 全局指针(Global Pointer)                                  |
| `$29`     | `$sp`     | 堆栈指针(Stack Pointer)                                   |
| `$30`     | `$fp`     | 帧指针(Frame Pointer)                                    |
| `$31`     | `$ra`     | 返回地址(return address)                                  |

各个系统调用中给`$v0`赋的值如下

| Service                    | Code in $v0 | Arguments                                        | Result |
| -------------------------- | ----------- | ------------------------------------------------ | ------ |
| print integer              | 1           | $a0 = integer to print                           |        |
| print string               | 4           | $a0 = address of null-terminated string to print |        |
| exit (terminate execution) | 10          |                                                  |        |

虽然说我们不打算实现所谓的优化，但我们不得不承认，在生成代码的过程中尽可能地做一些较好的处理时可行地，由此产生如下内容

## 总体设计

总体上分为两大段`.data`和`.text`，分别用两个大大的字符串来存。完成后拼接成一个大大的长长的字符串，然后输出到mips.txt中

## .data段

`.data`段要存的有如下内容

1. 在主函数外面定义的全局变量和全局常量

2. `printf`中用到的字符串

### 全局变量和全局常量

参照C语言，我们知道

1. 全部变量如果没有显式地给定初始值，那么其初始值默认为0。如果是一个数组的话，那么这个数组的每一个下标处都是0
